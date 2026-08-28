# AWS 배포 가이드

EC2 + RDS + GitHub Actions로 배포한다. 도메인 없이 EC2 퍼블릭 IP + 80 포트로 운영한다.
AWS 콘솔에서 리소스를 생성하는 단계는 CLI/에이전트가 대신할 수 없으므로 직접 진행해야 한다.

## 0. 준비물

- AWS 계정, 결제 정보 등록
- 로컬에 SSH 키페어를 만들 수 있는 환경 (또는 콘솔에서 발급한 .pem)
- 로컬 MariaDB 클라이언트 (스키마 초기 적용용, 이미 설치돼 있음)

---

## 1. RDS (MariaDB) 생성

1. RDS 콘솔 → 데이터베이스 생성
   - 엔진: MariaDB, 프리 티어 템플릿
   - 인스턴스 클래스: `db.t3.micro`
   - DB 인스턴스 식별자: `mathbank-db`
   - 마스터 사용자: `admin`, 마스터 암호: 강력한 값으로 직접 설정 (나중에 `.env`에 씀)
   - 퍼블릭 액세스: **아니오** (EC2에서만 접속, 보안 강화)
   - VPC 보안 그룹: 새로 생성 — 이름 `mathbank-rds-sg`
   - 초기 데이터베이스 이름: `mathbank`
2. 생성 후 엔드포인트(`mathbank-db.xxxxx.ap-northeast-2.rds.amazonaws.com`) 기록.

---

## 2. EC2 생성

1. EC2 콘솔 → 인스턴스 시작
   - AMI: Amazon Linux 2023
   - 인스턴스 유형: `t3.micro` (프리 티어) 또는 `t3.small`
   - 키 페어: 새로 생성 후 `.pem` 다운로드 (분실 시 재발급 불가하니 안전한 곳에 보관)
   - 네트워크: 새 보안 그룹 `mathbank-ec2-sg`
     - 인바운드: SSH(22) — 내 IP만 / HTTP(80) — 0.0.0.0/0
2. 생성 후 퍼블릭 IP 기록.
3. **RDS 보안 그룹(`mathbank-rds-sg`)의 인바운드 규칙**에 `mathbank-ec2-sg`를 소스로 하는 MySQL/Aurora(3306) 규칙 추가 → EC2에서만 RDS 접속 가능하게 제한.

---

## 3. EC2 초기 설정

```bash
ssh -i mathbank-key.pem ec2-user@<EC2_퍼블릭_IP>

# Java 17 설치
sudo dnf install -y java-17-amazon-corretto-headless

# 80번 포트는 특권 포트이므로, 일반 사용자(ec2-user)로 실행되는 java가 바인딩할 수 있도록 권한 부여
sudo setcap 'cap_net_bind_service=+ep' $(readlink -f $(which java))

# 앱 디렉토리 준비
sudo mkdir -p /opt/mathbank/uploads/problem
sudo chown -R ec2-user:ec2-user /opt/mathbank
```

`.env` 파일 생성 (`deploy/.env.example` 참고, 실제 값으로 채워서 EC2에 직접 작성 — 절대 git에 커밋하지 않음):

```bash
vi /opt/mathbank/.env
```

```
DB_URL=jdbc:mariadb://<RDS_엔드포인트>:3306/mathbank?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
DB_USERNAME=admin
DB_PASSWORD=<RDS_마스터_암호>
ADMIN_INIT_PASSWORD=<앱에서 처음 로그인할 관리자 비밀번호>
```

systemd 유닛 설치 (로컬 `deploy/mathbank.service`를 EC2로 복사하거나 `scp`):

```bash
scp -i mathbank-key.pem deploy/mathbank.service ec2-user@<EC2_IP>:/tmp/
ssh -i mathbank-key.pem ec2-user@<EC2_IP> '
  sudo mv /tmp/mathbank.service /etc/systemd/system/mathbank.service
  sudo systemctl daemon-reload
  sudo systemctl enable mathbank
'
```

GitHub Actions가 배포마다 `sudo systemctl restart mathbank`를 실행할 수 있도록 sudoers 설정:

```bash
scp -i mathbank-key.pem deploy/mathbank-sudoers ec2-user@<EC2_IP>:/tmp/
ssh -i mathbank-key.pem ec2-user@<EC2_IP> '
  sudo visudo -cf /tmp/mathbank-sudoers && sudo mv /tmp/mathbank-sudoers /etc/sudoers.d/mathbank
'
```

GitHub Actions가 EC2에 SSH로 접속할 수 있도록, 배포 전용 키를 `ec2-user`의 `~/.ssh/authorized_keys`에 등록 (아래 4번에서 만드는 키의 **공개키**를 등록).

---

## 4. DB 스키마 최초 적용

RDS는 퍼블릭 액세스를 껐으므로, 로컬에서 바로 접속은 안 된다. 가장 간단한 방법은 EC2를 경유하거나,
**RDS 보안 그룹에 내 IP를 임시로 허용**한 뒤 로컬 클라이언트로 접속해 스키마를 넣고 다시 규칙을 제거하는 것이다.

```bash
# (RDS 보안 그룹에 내 로컬 IP를 3306 포트로 임시 허용한 뒤)
mysql -h <RDS_엔드포인트> -P 3306 -u admin -p mathbank < src/main/resources/sql/schema.sql
mysql -h <RDS_엔드포인트> -P 3306 -u admin -p mathbank < src/main/resources/sql/sample_중2_100_problems.sql
# 확인 후 RDS 보안 그룹에서 내 IP 허용 규칙 삭제 (tag_data.sql은 앱이 기동할 때 자동 삽입되므로 생략 가능)
```

---

## 5. GitHub Secrets 등록

배포용 SSH 키 쌍을 새로 만든다 (EC2 접속용 `.pem`과는 별개로 CI 전용 키를 쓰는 것을 권장):

```bash
ssh-keygen -t ed25519 -f mathbank-deploy-key -N ""
# mathbank-deploy-key.pub 내용을 EC2의 ~/.ssh/authorized_keys 에 추가
ssh -i mathbank-key.pem ec2-user@<EC2_IP> "echo '<공개키 내용>' >> ~/.ssh/authorized_keys"
```

GitHub 저장소 → Settings → Secrets and variables → Actions 에 등록:

| Secret 이름 | 값 |
|---|---|
| `EC2_HOST` | EC2 퍼블릭 IP |
| `EC2_USER` | `ec2-user` |
| `EC2_SSH_KEY` | `mathbank-deploy-key`의 **개인키** 전체 내용 |

---

## 6. 배포

`main` 브랜치에 push하면 `.github/workflows/deploy.yml`이 자동으로:
Maven 빌드 → jar를 EC2로 scp → `systemctl restart mathbank` 순서로 배포한다.

수동 실행: GitHub 저장소 → Actions → "Deploy to EC2" → Run workflow.

배포 확인:

```bash
curl -I http://<EC2_퍼블릭_IP>/auth/login   # 200 OK 확인
ssh -i mathbank-key.pem ec2-user@<EC2_IP> 'sudo systemctl status mathbank --no-pager'
ssh -i mathbank-key.pem ec2-user@<EC2_IP> 'sudo journalctl -u mathbank -n 50 --no-pager'  # 문제 시 로그 확인
```

브라우저에서 `http://<EC2_퍼블릭_IP>/auth/login` 접속 후 `admin` / `.env`에 설정한 `ADMIN_INIT_PASSWORD`로 로그인 확인.

---

## 트러블슈팅

- **80 포트 바인딩 실패**: `setcap` 적용 여부와 대상이 시스템에 실제 사용되는 `java` 바이너리인지 확인 (`readlink -f $(which java)`).
- **DB 연결 실패**: RDS 보안 그룹이 `mathbank-ec2-sg`를 3306으로 허용하는지, `.env`의 `DB_URL`에 정확한 RDS 엔드포인트가 들어갔는지 확인.
- **systemctl restart 시 sudo 비밀번호 요구**: `/etc/sudoers.d/mathbank` 적용 여부와 `visudo -cf` 검증 통과 여부 확인.
- **GitHub Actions SSH 실패**: `EC2_SSH_KEY`에 개인키 전체(줄바꿈 포함)가 들어갔는지, EC2 `authorized_keys`에 대응하는 공개키가 등록됐는지 확인.
