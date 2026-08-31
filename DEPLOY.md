# 배포 가이드 (Google Cloud Free Tier)

GCP Compute Engine의 Always Free VM(`e2-micro`) 한 대에 애플리케이션과 MariaDB를 함께 올려서 운영한다.
관리형 DB(RDS 등) 없이 완전 무료로 유지하는 게 목적이라, 백업은 직접 크론으로 챙겨야 한다.

> 원래는 AWS EC2 + RDS로 배포했었다. AWS 프리티어는 12개월 한정이라, 다시 바빠지기 전에
> 영구 무료인 GCP Always Free로 옮겼다. (Oracle Cloud Always Free가 더 넉넉하지만 계정 생성이 막혀서 GCP로 결정)

AWS 콘솔/GCP 콘솔에서 리소스를 생성하는 단계는 CLI/에이전트가 대신할 수 없으므로 직접 진행해야 한다.

## 0. 준비물

- Google Cloud 계정, 프로젝트 생성
- 로컬에서 SSH 키페어를 만들 수 있는 환경

---

## 1. GCP VM 생성

**Compute Engine → VM 인스턴스 → 만들기**

- 리전: `us-west1` / `us-central1` / `us-east1` 중 하나 (Always Free 조건)
- 머신 유형: `e2-micro` (Always Free)
- 부팅 디스크: Ubuntu 22.04 LTS (minimal 이미지는 피할 것 — 아래 트러블슈팅 참고)
- 방화벽: "HTTP 트래픽 허용" 체크 (80번 포트 자동으로 열림)
- 생성 후 **외부 IP** 기록

---

## 2. SSH 접속 설정

GCP는 AWS와 달리 인스턴스 생성 시 키페어를 자동으로 심어주지 않는다. 로컬에서 키를 만들고 VM 메타데이터에 공개키를 등록하는 방식이다.

```bash
ssh-keygen -t ed25519 -f mathbank-gcp-key -N "" -C "mathbank-admin"
cat mathbank-gcp-key.pub
```

**인스턴스 상세 페이지 → 수정 → "SSH 키" 섹션 → 항목 추가 →** 아래 형식으로 등록 (사용자명 접두사 필수):

```
mathbank-admin:ssh-ed25519 AAAA... mathbank-admin
```

저장 후 접속:

```bash
ssh -i mathbank-gcp-key mathbank-admin@<외부_IP>
```

> ⚠️ **막히면**: 이 프로젝트에서 실제로 두 가지 이유로 이게 안 됐다.
> 1. **OS Login이 켜져있으면** VM이 메타데이터 SSH 키를 아예 무시한다. 인스턴스 메타데이터에 `enable-oslogin=FALSE`를 추가하고 **VM을 재부팅**해야 반영된다 (메타데이터만 바꾸고 재부팅 안 하면 sshd 설정이 그대로라 안 먹힘).
> 2. 그래도 안 되면 **콘솔의 "SSH" 브라우저 버튼**으로 접속한 뒤, 그 세션에서 직접 계정을 만들고 `~/.ssh/authorized_keys`에 공개키를 넣는 게 가장 확실하다:
>    ```bash
>    sudo useradd -m -s /bin/bash mathbank-admin
>    sudo mkdir -p /home/mathbank-admin/.ssh
>    echo "<공개키>" | sudo tee /home/mathbank-admin/.ssh/authorized_keys
>    sudo chown -R mathbank-admin:mathbank-admin /home/mathbank-admin/.ssh
>    sudo chmod 700 /home/mathbank-admin/.ssh
>    sudo chmod 600 /home/mathbank-admin/.ssh/authorized_keys
>    ```
> 3. GCP는 이렇게 생성된 계정을 자동으로 `google-sudoers` 그룹(비밀번호 없이 전체 sudo)에 넣어준다 — AWS `ec2-user`와 동일한 패턴. 별도 sudoers 설정 없이도 CI에서 `sudo systemctl restart` 정도는 바로 된다.

---

## 3. VM 초기 설정 (Java + MariaDB)

```bash
sudo apt-get update -qq
sudo apt-get install -y openjdk-17-jre-headless mariadb-server iptables

sudo systemctl enable --now mariadb

sudo mysql -e "
CREATE DATABASE mathbank CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'mathbank'@'localhost' IDENTIFIED BY '<강력한_비밀번호>';
GRANT ALL PRIVILEGES ON mathbank.* TO 'mathbank'@'localhost';
FLUSH PRIVILEGES;
"

sudo mkdir -p /opt/mathbank/uploads/problem
sudo chown -R mathbank-admin:mathbank-admin /opt/mathbank
```

MariaDB는 기본적으로 `localhost`에만 바인딩되므로 외부 포트를 열 필요가 없다 (RDS 때보다 오히려 공격 표면이 줄었다).

---

## 4. 80번 포트 — iptables 리다이렉트 (setcap 아님)

애플리케이션은 8080(기본값)으로 띄우고, 외부의 80번 요청을 커널 레벨에서 8080으로 리다이렉트한다.

```bash
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
```

> ⚠️ **`setcap 'cap_net_bind_service=+ep' $(which java)`로 java에 직접 80번 권한을 주는 방법은 쓰지 않는다.**
> file capability가 설정된 실행파일은 glibc가 secure-execution mode로 돌리면서
> `$ORIGIN` 상대경로 라이브러리 탐색과 `LD_LIBRARY_PATH`를 무시해버린다.
> 그 결과 JVM 런처가 같은 디렉토리의 `libjli.so`를 못 찾고
> `error while loading shared libraries: libjli.so`로 깨진다. 실제로 이 프로젝트에서 겪은 문제다.

iptables 규칙은 재부팅하면 사라지므로, 부팅마다 다시 걸어주는 systemd 유닛을 등록한다 (`deploy/mathbank-portfwd.service`):

```bash
scp deploy/mathbank-portfwd.service mathbank-admin@<IP>:/tmp/
ssh mathbank-admin@<IP> '
  sudo mv /tmp/mathbank-portfwd.service /etc/systemd/system/
  sudo systemctl daemon-reload
  sudo systemctl enable --now mathbank-portfwd
'
```

---

## 5. `.env` + systemd 유닛

`/opt/mathbank/.env` (직접 작성, git에 커밋 금지 — `deploy/.env.example` 참고):

```
DB_URL=jdbc:mariadb://localhost:3306/mathbank?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
DB_USERNAME=mathbank
DB_PASSWORD=<위에서 만든 비밀번호>
ADMIN_INIT_PASSWORD=<앱 최초 관리자 비밀번호>
```

`deploy/mathbank.service`를 `/etc/systemd/system/`에 설치 (`User=mathbank-admin`로 되어 있음 — 다른 계정명을 쓴다면 수정):

```bash
scp deploy/mathbank.service mathbank-admin@<IP>:/tmp/
ssh mathbank-admin@<IP> '
  sudo mv /tmp/mathbank.service /etc/systemd/system/
  sudo systemctl daemon-reload
  sudo systemctl enable mathbank
'
```

---

## 6. DB 스키마 + 시드 데이터 적용

```bash
mysql -h localhost -u mathbank -p mathbank < src/main/resources/sql/schema.sql
mysql -h localhost -u mathbank -p mathbank < src/main/resources/sql/sample_problems.sql
mysql -h localhost -u mathbank -p mathbank < src/main/resources/sql/sample_problem_tags.sql
mysql -h localhost -u mathbank -p mathbank < src/main/resources/sql/sample_중2_pdf_test.sql
mysql -h localhost -u mathbank -p mathbank < src/main/resources/sql/sample_중2_100_problems.sql
```

(다른 환경에서 옮겨온 DB가 있다면 `mysqldump`로 덤프 후 그대로 복원해도 된다. `tag_data.sql`은 앱이 기동할 때 `INSERT IGNORE`로 자동 반영되므로 별도 실행 불필요.)

---

## 7. 첫 배포 + GitHub Secrets 등록

```bash
./mvnw clean package -DskipTests
scp target/mathbank-0.0.1-SNAPSHOT.jar mathbank-admin@<IP>:/opt/mathbank/app.jar
ssh mathbank-admin@<IP> 'sudo systemctl start mathbank'
```

CI 배포용 SSH 키를 별도로 하나 더 만들어서 (접속 키와 별개로) VM의 `authorized_keys`에 추가하고, GitHub 저장소 → Settings → Secrets and variables → Actions에 등록:

| Secret 이름 | 값 |
|---|---|
| `EC2_HOST` | VM 외부 IP |
| `EC2_USER` | `mathbank-admin` |
| `EC2_SSH_KEY` | CI용 키의 개인키 전체 |

이후 `main` push마다 `.github/workflows/deploy.yml`이 Maven 빌드 → scp → `systemctl restart mathbank`를 자동 수행한다.

---

## 트러블슈팅

- **SSH 키 등록했는데 `Permission denied (publickey)`**: 위 2번의 OS Login / minimal 이미지 문제 참고. `ssh -vvv`로 붙여보면 서버가 키를 아예 모르는 건지(즉시 거절) 다른 문제인지 구분 가능.
- **java 실행 시 `libjli.so` 관련 오류**: `getcap $(readlink -f $(which java))`로 확인해서 capability가 걸려있으면 `sudo setcap -r <경로>`로 제거. iptables 방식으로 전환.
- **80번 포트로 접속이 안 됨**: `sudo iptables -t nat -L PREROUTING -n`으로 리다이렉트 규칙 확인, GCP 방화벽에 HTTP(80) 허용 규칙이 있는지 확인.
- **DB 연결 실패**: MariaDB가 `bind-address 127.0.0.1`로 떠있는지 (`sudo ss -tlnp | grep 3306`), `.env`의 `DB_URL`이 `localhost`를 가리키는지 확인.
- **systemctl restart 시 sudo 비밀번호 요구**: `/etc/sudoers.d/mathbank`가 root 소유(`chown root:root`, `chmod 440`)인지 확인 — `scp`+`mv`로 옮기면 소유자가 그대로 남아서 sudo가 그 파일을 무시한다.
- **GitHub Actions SSH 실패**: `EC2_SSH_KEY`에 개인키 전체(줄바꿈 포함)가 들어갔는지, VM `authorized_keys`에 대응하는 공개키가 등록됐는지 확인.
