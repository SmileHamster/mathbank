-- 문제-태그 매핑 (sample_problems.sql이 먼저 실행되어 있어야 함)
-- 태그 id와 문제 id 모두 하드코딩하지 않고 조회해서 쓴다 (환경마다 auto_increment 시작값이 다를 수 있음)

SET @g_1 = (SELECT id FROM tag WHERE tag_type='GRADE' AND tag_value='중1');
SET @g_2 = (SELECT id FROM tag WHERE tag_type='GRADE' AND tag_value='중2');
SET @g_3 = (SELECT id FROM tag WHERE tag_type='GRADE' AND tag_value='중3');
SET @g_h1 = (SELECT id FROM tag WHERE tag_type='GRADE' AND tag_value='고1');
SET @g_h2 = (SELECT id FROM tag WHERE tag_type='GRADE' AND tag_value='고2');
SET @sem_1 = (SELECT id FROM tag WHERE tag_type='SEMESTER' AND tag_value='1학기');
SET @sem_2 = (SELECT id FROM tag WHERE tag_type='SEMESTER' AND tag_value='2학기');
SET @u_num = (SELECT id FROM tag WHERE tag_type='UNIT' AND tag_value='수와 연산');
SET @u_alg = (SELECT id FROM tag WHERE tag_type='UNIT' AND tag_value='문자와 식');
SET @u_func = (SELECT id FROM tag WHERE tag_type='UNIT' AND tag_value='함수');
SET @u_prob = (SELECT id FROM tag WHERE tag_type='UNIT' AND tag_value='확률과 통계');
SET @su_rational = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='정수와 유리수');
SET @su_lineq = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='일차방정식');
SET @su_linfunc = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='일차함수');
SET @su_quadeq = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='이차방정식');
SET @su_quadfunc = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='이차함수');
SET @ty_concept = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='개념확인');
SET @ty_calc = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='계산');
SET @ty_desc = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='서술');
SET @ty_apply = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='응용');
SET @d_low = (SELECT id FROM tag WHERE tag_type='DIFFICULTY' AND tag_value='하');
SET @d_mid = (SELECT id FROM tag WHERE tag_type='DIFFICULTY' AND tag_value='중');
SET @d_high = (SELECT id FROM tag WHERE tag_type='DIFFICULTY' AND tag_value='상');

-- sample_problems.sql의 첫 문제 제목으로 시작 id를 찾아 나머지는 오프셋으로 계산
-- (다중 행 INSERT는 연속 id로 채번됨이 보장됨)
SET @base = (SELECT id FROM problem WHERE title = '정수의 덧셈');

INSERT INTO problem_tag (problem_id, tag_id) VALUES
-- base+0: 정수의 덧셈 (중1/1학기/수와연산/정수와유리수/계산/하)
(@base+0,@g_1),(@base+0,@sem_1),(@base+0,@u_num),(@base+0,@su_rational),(@base+0,@ty_calc),(@base+0,@d_low),
-- base+1: 절댓값의 성질 (중1/1학기/수와연산/정수와유리수/개념확인/중)
(@base+1,@g_1),(@base+1,@sem_1),(@base+1,@u_num),(@base+1,@su_rational),(@base+1,@ty_concept),(@base+1,@d_mid),
-- base+2: 유리수의 곱셈 (중1/2학기/수와연산/정수와유리수/계산/중)
(@base+2,@g_1),(@base+2,@sem_2),(@base+2,@u_num),(@base+2,@su_rational),(@base+2,@ty_calc),(@base+2,@d_mid),
-- base+3: 일차방정식 풀기 기본 (중2/1학기/문자와식/일차방정식/계산/하)
(@base+3,@g_2),(@base+3,@sem_1),(@base+3,@u_alg),(@base+3,@su_lineq),(@base+3,@ty_calc),(@base+3,@d_low),
-- base+4: 일차방정식 풀기 이항 (중2/1학기/문자와식/일차방정식/계산/중)
(@base+4,@g_2),(@base+4,@sem_1),(@base+4,@u_alg),(@base+4,@su_lineq),(@base+4,@ty_calc),(@base+4,@d_mid),
-- base+5: 일차방정식 활용 (중2/1학기/문자와식/일차방정식/응용/상)
(@base+5,@g_2),(@base+5,@sem_1),(@base+5,@u_alg),(@base+5,@su_lineq),(@base+5,@ty_apply),(@base+5,@d_high),
-- base+6: 비례식 (중2/1학기/문자와식/일차방정식/계산/중)
(@base+6,@g_2),(@base+6,@sem_1),(@base+6,@u_alg),(@base+6,@su_lineq),(@base+6,@ty_calc),(@base+6,@d_mid),
-- base+7: 일차함수의 기울기 (중2/1학기/함수/일차함수/개념확인/하)
(@base+7,@g_2),(@base+7,@sem_1),(@base+7,@u_func),(@base+7,@su_linfunc),(@base+7,@ty_concept),(@base+7,@d_low),
-- base+8: 일차함수 그래프 (중2/2학기/함수/일차함수/계산/중)
(@base+8,@g_2),(@base+8,@sem_2),(@base+8,@u_func),(@base+8,@su_linfunc),(@base+8,@ty_calc),(@base+8,@d_mid),
-- base+9: 두 일차함수의 교점 (중2/2학기/함수/일차함수/계산/상)
(@base+9,@g_2),(@base+9,@sem_2),(@base+9,@u_func),(@base+9,@su_linfunc),(@base+9,@ty_calc),(@base+9,@d_high),
-- base+10: 일차함수 응용 속력 (중2/2학기/함수/일차함수/응용/상)
(@base+10,@g_2),(@base+10,@sem_2),(@base+10,@u_func),(@base+10,@su_linfunc),(@base+10,@ty_apply),(@base+10,@d_high),
-- base+11: 이차방정식 인수분해 (중3/1학기/문자와식/이차방정식/계산/중)
(@base+11,@g_3),(@base+11,@sem_1),(@base+11,@u_alg),(@base+11,@su_quadeq),(@base+11,@ty_calc),(@base+11,@d_mid),
-- base+12: 이차방정식 근의 공식 (중3/1학기/문자와식/이차방정식/계산/상)
(@base+12,@g_3),(@base+12,@sem_1),(@base+12,@u_alg),(@base+12,@su_quadeq),(@base+12,@ty_calc),(@base+12,@d_high),
-- base+13: 완전제곱식 (중3/1학기/문자와식/이차방정식/계산/중)
(@base+13,@g_3),(@base+13,@sem_1),(@base+13,@u_alg),(@base+13,@su_quadeq),(@base+13,@ty_calc),(@base+13,@d_mid),
-- base+14: 이차방정식 판별식 (중3/1학기/문자와식/이차방정식/개념확인/중)
(@base+14,@g_3),(@base+14,@sem_1),(@base+14,@u_alg),(@base+14,@su_quadeq),(@base+14,@ty_concept),(@base+14,@d_mid),
-- base+15: 이차방정식 활용 넓이 (중3/1학기/문자와식/이차방정식/응용/상)
(@base+15,@g_3),(@base+15,@sem_1),(@base+15,@u_alg),(@base+15,@su_quadeq),(@base+15,@ty_apply),(@base+15,@d_high),
-- base+16: 이차함수의 꼭짓점 (중3/2학기/함수/이차함수/계산/중)
(@base+16,@g_3),(@base+16,@sem_2),(@base+16,@u_func),(@base+16,@su_quadfunc),(@base+16,@ty_calc),(@base+16,@d_mid),
-- base+17: 이차함수 최솟값 (중3/2학기/함수/이차함수/계산/중)
(@base+17,@g_3),(@base+17,@sem_2),(@base+17,@u_func),(@base+17,@su_quadfunc),(@base+17,@ty_calc),(@base+17,@d_mid),
-- base+18: 이차함수와 직선의 교점 수 (중3/2학기/함수/이차함수/서술/상)
(@base+18,@g_3),(@base+18,@sem_2),(@base+18,@u_func),(@base+18,@su_quadfunc),(@base+18,@ty_desc),(@base+18,@d_high),
-- base+19: 집합의 원소 개수 (고1/1학기/문자와식/개념확인/하)
(@base+19,@g_h1),(@base+19,@sem_1),(@base+19,@u_alg),(@base+19,@ty_concept),(@base+19,@d_low),
-- base+20: 명제의 역 (고1/1학기/문자와식/개념확인/중)
(@base+20,@g_h1),(@base+20,@sem_1),(@base+20,@u_alg),(@base+20,@ty_concept),(@base+20,@d_mid),
-- base+21: 절대부등식 (고1/1학기/문자와식/계산/상)
(@base+21,@g_h1),(@base+21,@sem_1),(@base+21,@u_alg),(@base+21,@ty_calc),(@base+21,@d_high),
-- base+22: 함수의 합성 (고1/1학기/함수/계산/중)
(@base+22,@g_h1),(@base+22,@sem_1),(@base+22,@u_func),(@base+22,@ty_calc),(@base+22,@d_mid),
-- base+23: 등차수열 일반항 (고1/2학기/수와연산/계산/중)
(@base+23,@g_h1),(@base+23,@sem_2),(@base+23,@u_num),(@base+23,@ty_calc),(@base+23,@d_mid),
-- base+24: 로그의 성질 (고2/1학기/수와연산/계산/중)
(@base+24,@g_h2),(@base+24,@sem_1),(@base+24,@u_num),(@base+24,@ty_calc),(@base+24,@d_mid),
-- base+25: 삼각함수 값 (고2/1학기/함수/계산/중)
(@base+25,@g_h2),(@base+25,@sem_1),(@base+25,@u_func),(@base+25,@ty_calc),(@base+25,@d_mid),
-- base+26: 미분 기본 (고2/2학기/함수/계산/중)
(@base+26,@g_h2),(@base+26,@sem_2),(@base+26,@u_func),(@base+26,@ty_calc),(@base+26,@d_mid),
-- base+27: 적분 기본 (고2/2학기/함수/계산/상)
(@base+27,@g_h2),(@base+27,@sem_2),(@base+27,@u_func),(@base+27,@ty_calc),(@base+27,@d_high),
-- base+28: 경우의 수 (고1/2학기/확률과통계/계산/하)
(@base+28,@g_h1),(@base+28,@sem_2),(@base+28,@u_prob),(@base+28,@ty_calc),(@base+28,@d_low),
-- base+29: 조합 (고1/2학기/확률과통계/계산/중)
(@base+29,@g_h1),(@base+29,@sem_2),(@base+29,@u_prob),(@base+29,@ty_calc),(@base+29,@d_mid),
-- base+30: 확률의 덧셈정리 (고1/2학기/확률과통계/계산/중)
(@base+30,@g_h1),(@base+30,@sem_2),(@base+30,@u_prob),(@base+30,@ty_calc),(@base+30,@d_mid);
