-- ============================================================
-- 수학 문제은행 DDL  (MariaDB 10.x)
-- 실행 순서: DROP → CREATE (FK 역순 DROP, 의존 순서 CREATE)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS student_answer;
DROP TABLE IF EXISTS exam_sheet_problem;
DROP TABLE IF EXISTS problem_tag;
DROP TABLE IF EXISTS exam_sheet;
DROP TABLE IF EXISTS problem;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS member;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. member
-- ============================================================
CREATE TABLE member (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '회원 PK',
    username   VARCHAR(50)  NOT NULL                COMMENT '로그인 아이디',
    password   VARCHAR(255) NOT NULL                COMMENT '암호화된 비밀번호',
    role       ENUM('ADMIN','STUDENT') NOT NULL DEFAULT 'STUDENT' COMMENT '역할',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원';

-- ============================================================
-- 2. tag
-- ============================================================
CREATE TABLE tag (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '태그 PK',
    tag_type   ENUM('GRADE','SEMESTER','UNIT','SUB_UNIT','TYPE','DIFFICULTY')
               NOT NULL                            COMMENT '태그 분류축',
    tag_value  VARCHAR(100) NOT NULL               COMMENT '태그 값',
    sort_order INT          NOT NULL DEFAULT 0     COMMENT '정렬 순서',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_type_value (tag_type, tag_value),
    KEY idx_tag_type (tag_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='태그 (6축 분류)';

-- ============================================================
-- 3. problem
-- ============================================================
CREATE TABLE problem (
    id          BIGINT    NOT NULL AUTO_INCREMENT  COMMENT '문제 PK',
    title       VARCHAR(500) NOT NULL              COMMENT '문제 제목',
    content     LONGTEXT  NOT NULL                 COMMENT '문제 본문 (LaTeX)',
    answer      TEXT      NOT NULL                 COMMENT '정답',
    explanation LONGTEXT  NULL                     COMMENT '해설 (LaTeX)',
    created_by  BIGINT    NOT NULL                 COMMENT '등록자 member.id',
    created_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '등록일시',
    updated_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_problem_created_by (created_by),
    KEY idx_problem_created_at (created_at),
    CONSTRAINT fk_problem_member
        FOREIGN KEY (created_by) REFERENCES member (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='문제';

-- ============================================================
-- 4. problem_tag  (복합 PK + 복합 인덱스)
-- ============================================================
CREATE TABLE problem_tag (
    problem_id BIGINT NOT NULL COMMENT '문제 PK',
    tag_id     BIGINT NOT NULL COMMENT '태그 PK',
    PRIMARY KEY (problem_id, tag_id),
    KEY idx_problem_tag_tag_id (tag_id),
    CONSTRAINT fk_problem_tag_problem
        FOREIGN KEY (problem_id) REFERENCES problem (id) ON DELETE CASCADE,
    CONSTRAINT fk_problem_tag_tag
        FOREIGN KEY (tag_id)     REFERENCES tag     (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='문제-태그 매핑';

-- ============================================================
-- 5. exam_sheet
-- ============================================================
CREATE TABLE exam_sheet (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '시험지 PK',
    name         VARCHAR(200) NOT NULL               COMMENT '시험지 이름',
    grade_tag_id BIGINT       NULL                   COMMENT '학년 태그 tag.id',
    total_count  INT          NOT NULL DEFAULT 0     COMMENT '총 문항 수',
    created_by   BIGINT       NOT NULL               COMMENT '생성자 member.id',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (id),
    KEY idx_exam_sheet_created_by (created_by),
    KEY idx_exam_sheet_created_at (created_at),
    KEY idx_exam_sheet_grade_tag  (grade_tag_id),
    CONSTRAINT fk_exam_sheet_member
        FOREIGN KEY (created_by)   REFERENCES member (id),
    CONSTRAINT fk_exam_sheet_grade_tag
        FOREIGN KEY (grade_tag_id) REFERENCES tag    (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시험지';

-- ============================================================
-- 6. exam_sheet_problem  (복합 PK)
-- ============================================================
CREATE TABLE exam_sheet_problem (
    exam_sheet_id BIGINT NOT NULL COMMENT '시험지 PK',
    problem_id    BIGINT NOT NULL COMMENT '문제 PK',
    sort_order    INT    NOT NULL DEFAULT 0 COMMENT '문항 순서',
    PRIMARY KEY (exam_sheet_id, problem_id),
    KEY idx_esp_problem_id (problem_id),
    CONSTRAINT fk_esp_exam_sheet
        FOREIGN KEY (exam_sheet_id) REFERENCES exam_sheet (id) ON DELETE CASCADE,
    CONSTRAINT fk_esp_problem
        FOREIGN KEY (problem_id)    REFERENCES problem    (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시험지-문제 매핑';

-- ============================================================
-- 7. student
-- ============================================================
CREATE TABLE student (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '학생 PK',
    name       VARCHAR(100) NOT NULL               COMMENT '학생 이름',
    grade      VARCHAR(20)  NULL                   COMMENT '학년 (예: 중2, 고1)',
    memo       VARCHAR(500) NULL                   COMMENT '메모',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (id),
    KEY idx_student_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학생';

-- ============================================================
-- 8. student_answer
-- ============================================================
CREATE TABLE student_answer (
    id            BIGINT     NOT NULL AUTO_INCREMENT COMMENT '응시 기록 PK',
    student_id    BIGINT     NOT NULL               COMMENT '학생 PK',
    exam_sheet_id BIGINT     NOT NULL               COMMENT '시험지 PK',
    problem_id    BIGINT     NOT NULL               COMMENT '문제 PK',
    is_correct    TINYINT(1) NOT NULL DEFAULT 0     COMMENT '정답 여부 (0: 오답, 1: 정답)',
    submitted_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '응시일시',
    PRIMARY KEY (id),
    KEY idx_sa_student_id    (student_id),
    KEY idx_sa_exam_sheet_id (exam_sheet_id),
    KEY idx_sa_problem_id    (problem_id),
    KEY idx_sa_student_exam  (student_id, exam_sheet_id),
    CONSTRAINT fk_sa_student
        FOREIGN KEY (student_id)    REFERENCES student    (id) ON DELETE CASCADE,
    CONSTRAINT fk_sa_exam_sheet
        FOREIGN KEY (exam_sheet_id) REFERENCES exam_sheet (id) ON DELETE CASCADE,
    CONSTRAINT fk_sa_problem
        FOREIGN KEY (problem_id)    REFERENCES problem    (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학생 응시 기록';
