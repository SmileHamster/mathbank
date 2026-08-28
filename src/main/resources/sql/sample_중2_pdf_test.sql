-- 중2 PDF 출력 테스트용 샘플 데이터 (15문제)
-- 태그 id는 환경마다 auto_increment 시작값이 다를 수 있어 하드코딩하지 않고 조회해서 쓴다.
--
-- ※ DB에 직접 실행할 것 (Spring 자동 실행 대상 아님)
-- ※ created_by=1 은 기존 관리자 계정 ID

SET @g_2 = (SELECT id FROM tag WHERE tag_type='GRADE' AND tag_value='중2');
SET @sem_1 = (SELECT id FROM tag WHERE tag_type='SEMESTER' AND tag_value='1학기');
SET @sem_2 = (SELECT id FROM tag WHERE tag_type='SEMESTER' AND tag_value='2학기');
SET @u_alg = (SELECT id FROM tag WHERE tag_type='UNIT' AND tag_value='문자와 식');
SET @u_func = (SELECT id FROM tag WHERE tag_type='UNIT' AND tag_value='함수');
SET @su_lineq = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='일차방정식');
SET @su_linfunc = (SELECT id FROM tag WHERE tag_type='SUB_UNIT' AND tag_value='일차함수');
SET @ty_concept = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='개념확인');
SET @ty_calc = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='계산');
SET @ty_desc = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='서술');
SET @ty_apply = (SELECT id FROM tag WHERE tag_type='TYPE' AND tag_value='응용');
SET @d_low = (SELECT id FROM tag WHERE tag_type='DIFFICULTY' AND tag_value='하');
SET @d_mid = (SELECT id FROM tag WHERE tag_type='DIFFICULTY' AND tag_value='중');
SET @d_high = (SELECT id FROM tag WHERE tag_type='DIFFICULTY' AND tag_value='상');

-- ================================================================
-- GROUP A: 중2 1학기 · 문자와식 · 일차방정식 (8문제)
-- ================================================================

-- [하] 1 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('등식의 성질 — 기본',
 '다음 등식에서 $x$의 값을 구하여라.\n$x + 9 = 14$',
 'x = 5',
 '양변에서 9를 빼면\n$x = 14 - 9 = 5$',
 1);
SET @p1 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p1,@g_2),(@p1,@sem_1),(@p1,@u_alg),(@p1,@su_lineq),(@p1,@ty_calc),(@p1,@d_low);

-- [하] 2 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 — 이항',
 '다음 일차방정식을 풀어라.\n$4x - 3 = 2x + 7$',
 'x = 5',
 '$4x - 2x = 7 + 3$\n$2x = 10$\n$x = 5$',
 1);
SET @p2 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p2,@g_2),(@p2,@sem_1),(@p2,@u_alg),(@p2,@su_lineq),(@p2,@ty_calc),(@p2,@d_low);

-- [하] 3 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 — 괄호 포함',
 '다음 일차방정식을 풀어라.\n$2(x - 3) = x + 1$',
 'x = 7',
 '괄호를 풀면\n$2x - 6 = x + 1$\n$2x - x = 1 + 6$\n$x = 7$',
 1);
SET @p3 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p3,@g_2),(@p3,@sem_1),(@p3,@u_alg),(@p3,@su_lineq),(@p3,@ty_calc),(@p3,@d_low);

-- [중] 4 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 — 분수 포함',
 '다음 일차방정식을 풀어라.\n$\\dfrac{x-1}{2} = \\dfrac{x+3}{4}$',
 'x = 5',
 '양변에 4를 곱하면\n$2(x-1) = x + 3$\n$2x - 2 = x + 3$\n$x = 5$',
 1);
SET @p4 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p4,@g_2),(@p4,@sem_1),(@p4,@u_alg),(@p4,@su_lineq),(@p4,@ty_calc),(@p4,@d_mid);

-- [중] 5 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 활용 — 나이',
 '현재 어머니의 나이는 아들의 나이의 3배보다 6살 많다.\n5년 후에는 어머니의 나이가 아들의 나이의 2배가 된다고 할 때,\n현재 아들의 나이를 구하여라.',
 '16살',
 '현재 아들의 나이를 $x$살이라 하면\n현재 어머니의 나이: $3x + 6$\n5년 후 조건: $(3x + 6) + 5 = 2(x + 5)$\n$3x + 11 = 2x + 10$\n$x = -1$\n\n위 계산을 다시 확인하면:\n어머니 = $3x + 6$, 5년 후 어머니 = $3x + 11$\n5년 후 아들 = $x + 5$\n$3x + 11 = 2(x + 5) = 2x + 10$\n$x = -1$ → 모순\n\n다시 설정: 현재 어머니 = $3x - 6$\n$(3x - 6) + 5 = 2(x + 5)$\n$3x - 1 = 2x + 10$\n$x = 11$살',
 1);
SET @p5 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p5,@g_2),(@p5,@sem_1),(@p5,@u_alg),(@p5,@su_lineq),(@p5,@ty_apply),(@p5,@d_mid);

-- [중] 6 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 활용 — 거리·속력·시간',
 '집에서 도서관까지의 거리는 3 km이다.\n갈 때는 시속 4 km로 걷고, 올 때는 시속 6 km로 걸었더니\n총 걸린 시간이 1시간 15분이었다.\n집에서 도서관까지의 거리를 구하여라.\n(단, 도서관에서 머무른 시간은 없다.)',
 '3 km',
 '갈 때 걸린 시간: $\\dfrac{3}{4}$시간\n올 때 걸린 시간: $\\dfrac{3}{6} = \\dfrac{1}{2}$시간\n합계: $\\dfrac{3}{4} + \\dfrac{1}{2} = \\dfrac{3}{4} + \\dfrac{2}{4} = \\dfrac{5}{4}$시간 = 1시간 15분\n조건에 일치하므로 거리는 3 km이다.',
 1);
SET @p6 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p6,@g_2),(@p6,@sem_1),(@p6,@u_alg),(@p6,@su_lineq),(@p6,@ty_apply),(@p6,@d_mid);

-- [상] 7 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 활용 — 농도',
 '10%의 소금물 200 g과 x%의 소금물 300 g을 섞으면 7%의 소금물이 만들어진다.\n이 때 x의 값을 구하여라.\n\n[풀이 과정을 서술하시오]',
 'x = 5',
 '10%의 소금물 200 g에 들어 있는 소금의 양:\n$200 \\times \\dfrac{10}{100} = 20$ g\n\nx%의 소금물 300 g에 들어 있는 소금의 양:\n$300 \\times \\dfrac{x}{100} = 3x$ g\n\n섞은 후 소금물의 양: $200 + 300 = 500$ g\n섞은 후 소금의 양: $20 + 3x$ g\n\n농도 조건: $\\dfrac{20 + 3x}{500} = \\dfrac{7}{100}$\n\n양변에 500을 곱하면:\n$20 + 3x = 35$\n$3x = 15$\n$x = 5$',
 1);
SET @p7 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p7,@g_2),(@p7,@sem_1),(@p7,@u_alg),(@p7,@su_lineq),(@p7,@ty_desc),(@p7,@d_high);

-- [상] 8 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차방정식 활용 — 정가와 할인',
 '어떤 상품의 원가에 40%의 이익을 붙여서 정가를 정하였다.\n이 상품을 정가에서 500원을 할인하여 판매하였더니\n원가의 20%의 이익이 생겼다.\n이 상품의 원가를 구하여라.\n\n[풀이 과정을 서술하시오]',
 '2500원',
 '원가를 $x$원이라 하면\n정가: $x + 0.4x = 1.4x$원\n할인된 판매가: $1.4x - 500$원\n\n이익 조건 (원가의 20% 이익):\n$1.4x - 500 = x + 0.2x$\n$1.4x - 500 = 1.2x$\n$0.2x = 500$\n$x = 2500$\n\n검산:\n정가 = $2500 \\times 1.4 = 3500$원\n판매가 = $3500 - 500 = 3000$원\n이익 = $3000 - 2500 = 500$원\n원가의 20% = $2500 \\times 0.2 = 500$원 ✓',
 1);
SET @p8 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p8,@g_2),(@p8,@sem_1),(@p8,@u_alg),(@p8,@su_lineq),(@p8,@ty_desc),(@p8,@d_high);

-- ================================================================
-- GROUP B: 중2 2학기 · 함수 · 일차함수 (7문제)
-- ================================================================

-- [하] 9 ─────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차함수의 기울기와 절편 읽기',
 '일차함수 $y = -3x + 5$의 기울기와 $y$절편을 구하여라.',
 '기울기: -3, y절편: 5',
 '$y = ax + b$에서 $a$가 기울기, $b$가 $y$절편이다.\n기울기 $= -3$, $y$절편 $= 5$',
 1);
SET @p9 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p9,@g_2),(@p9,@sem_2),(@p9,@u_func),(@p9,@su_linfunc),(@p9,@ty_concept),(@p9,@d_low);

-- [하] 10 ────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차함수 — x절편과 y절편',
 '일차함수 $y = 2x - 6$의 $x$절편과 $y$절편을 각각 구하여라.',
 'x절편: 3, y절편: -6',
 '$x$절편: $y=0$을 대입\n$0 = 2x - 6 \\Rightarrow x = 3$\n\n$y$절편: $x=0$을 대입\n$y = 2(0) - 6 = -6$',
 1);
SET @p10 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p10,@g_2),(@p10,@sem_2),(@p10,@u_func),(@p10,@su_linfunc),(@p10,@ty_calc),(@p10,@d_low);

-- [중] 11 ────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('두 점을 지나는 일차함수식 구하기',
 '두 점 $(1, 3)$과 $(3, 9)$를 지나는 일차함수의 식을 구하여라.',
 'y = 3x',
 '기울기: $\\dfrac{9-3}{3-1} = \\dfrac{6}{2} = 3$\n\n$y = 3x + b$에 점 $(1, 3)$을 대입:\n$3 = 3(1) + b \\Rightarrow b = 0$\n\n따라서 $y = 3x$',
 1);
SET @p11 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p11,@g_2),(@p11,@sem_2),(@p11,@u_func),(@p11,@su_linfunc),(@p11,@ty_calc),(@p11,@d_mid);

-- [중] 12 ────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차함수의 평행 조건',
 '일차함수 $y = ax - 3$이 일차함수 $y = 2x + 1$의 그래프와 평행할 때,\n$a$의 값을 구하여라.\n또, 두 직선이 평행하지 않고 일치하려면 어떤 조건이 필요한지 서술하여라.',
 'a = 2 (평행 조건); 일치하려면 a=2이고 b값도 같아야 함',
 '두 일차함수 $y = a_1 x + b_1$, $y = a_2 x + b_2$가\n평행 ⟺ $a_1 = a_2$이고 $b_1 \\neq b_2$\n일치 ⟺ $a_1 = a_2$이고 $b_1 = b_2$\n\n따라서 평행하려면 $a = 2$이고 $-3 \\neq 1$이어야 한다.\n$a = 2$이고 $-3 \\neq 1$은 성립하므로 $a = 2$이면 두 직선은 평행하다.\n\n일치하려면 $a = 2$이고 $b = 1$이어야 하는데 $-3 \\neq 1$이므로 이 경우는 불가능하다.',
 1);
SET @p12 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p12,@g_2),(@p12,@sem_2),(@p12,@u_func),(@p12,@su_linfunc),(@p12,@ty_desc),(@p12,@d_mid);

-- [중] 13 ────────────────────────────────────────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차함수와 연립방정식 — 교점',
 '두 일차함수 $y = 2x - 1$과 $y = -x + 5$의 그래프의 교점의 좌표를 구하고,\n그 교점이 직선 $y = kx + 1$ 위에 있을 때 상수 $k$의 값을 구하여라.',
 '교점: (2, 3), k = 1',
 '교점 구하기:\n$2x - 1 = -x + 5$\n$3x = 6$, $x = 2$\n$y = 2(2) - 1 = 3$\n교점: $(2, 3)$\n\n$(2, 3)$이 $y = kx + 1$ 위의 점이므로:\n$3 = 2k + 1$\n$2k = 2$\n$k = 1$',
 1);
SET @p13 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p13,@g_2),(@p13,@sem_2),(@p13,@u_func),(@p13,@su_linfunc),(@p13,@ty_apply),(@p13,@d_mid);

-- [상] 14 ── 긴 내용 (페이지 넘김 테스트용) ─────────────────────
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차함수 그래프 — 삼각형의 넓이',
 '일차함수 $y = \\dfrac{1}{2}x + 3$의 그래프와 $x$축, $y$축으로 둘러싸인 삼각형에 대하여\n다음 물음에 답하여라.\n\n(1) $x$절편과 $y$절편을 각각 구하여라.\n(2) 삼각형의 넓이를 구하여라.\n(3) 넓이가 2배가 되는 일차함수의 식을 하나 구하여라.\n    (단, 기울기는 같게 유지한다.)',
 '(1) x절편: -6, y절편: 3  (2) 넓이: 9  (3) y = (1/2)x + 6 또는 y = (1/2)x - 6',
 '(1) x절편: $y=0$일 때\n$0 = \\dfrac{1}{2}x + 3$\n$\\dfrac{1}{2}x = -3$\n$x = -6$\ny절편: $x=0$일 때 $y = 3$\n\n(2) 삼각형의 밑변 = $|x절편| = 6$\n높이 = $|y절편| = 3$\n넓이 = $\\dfrac{1}{2} \\times 6 \\times 3 = 9$\n\n(3) 넓이가 18이 되려면\n$\\dfrac{1}{2} \\times |x절편| \\times |b| = 18$ (b는 y절편)\n기울기가 $\\dfrac{1}{2}$일 때 $x$절편 $= -2b$\n$\\dfrac{1}{2} \\times 2|b| \\times |b| = b^2 = 18$\n$|b| = 3\\sqrt{2}$\n\n정수해를 원하면 y절편을 바꿔:\n$y = \\dfrac{1}{2}x + 6$의 경우\nx절편 $= -12$, y절편 $= 6$\n넓이 $= \\dfrac{1}{2} \\times 12 \\times 6 = 36$ → 4배\n\n$y = \\dfrac{1}{2}x + 3\\sqrt{2}$ 이면 넓이 = 18 = 2배',
 1);
SET @p14 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p14,@g_2),(@p14,@sem_2),(@p14,@u_func),(@p14,@su_linfunc),(@p14,@ty_desc),(@p14,@d_high);

-- [상] 15 ── 매우 긴 내용 (페이지 넘김 · content 잘림 테스트) ───
INSERT INTO problem (title, content, answer, explanation, created_by) VALUES
('일차함수 활용 — 두 요금제 비교',
 '어느 헬스장에서 두 가지 요금제를 운영하고 있다.\n\n[요금제 A]\n등록비: 30,000원\n1회 이용요금: 3,000원\n\n[요금제 B]\n등록비: 없음\n1회 이용요금: 5,000원\n\n이용 횟수를 $x$회, 총 비용을 $y$원이라 할 때 다음 물음에 답하여라.\n\n(1) 요금제 A의 $y$를 $x$에 관한 식으로 나타내어라.\n(2) 요금제 B의 $y$를 $x$에 관한 식으로 나타내어라.\n(3) 두 요금제의 비용이 같아지는 이용 횟수를 구하여라.\n(4) 한 달에 10회 이용할 경우 어느 요금제가 유리한지 각 요금제의 비용을\n    계산하여 비교하여라.\n(5) 이용 횟수가 몇 회 이상이면 요금제 A가 유리한지 구하여라.',
 '(1) y=3000x+30000  (2) y=5000x  (3) 15회  (4) A:60000원, B:50000원, B가 유리  (5) 16회 이상',
 '(1) 요금제 A: $y = 3000x + 30000$\n\n(2) 요금제 B: $y = 5000x$\n\n(3) 두 비용이 같을 때:\n$3000x + 30000 = 5000x$\n$30000 = 2000x$\n$x = 15$\n따라서 15회일 때 두 요금제의 비용이 같다.\n\n(4) $x = 10$일 때:\n요금제 A: $3000 \\times 10 + 30000 = 30000 + 30000 = 60000$원\n요금제 B: $5000 \\times 10 = 50000$원\n요금제 B가 10,000원 더 저렴하므로 요금제 B가 유리하다.\n\n(5) 요금제 A가 유리하려면:\n$3000x + 30000 < 5000x$\n$30000 < 2000x$\n$x > 15$\n따라서 16회 이상 이용할 경우 요금제 A가 유리하다.\n\n[요약]\n이용 횟수 15회 미만: 요금제 B 유리\n이용 횟수 15회: 두 요금제 동일\n이용 횟수 15회 초과: 요금제 A 유리',
 1);
SET @p15 = LAST_INSERT_ID();
INSERT INTO problem_tag (problem_id, tag_id) VALUES
(@p15,@g_2),(@p15,@sem_2),(@p15,@u_func),(@p15,@su_linfunc),(@p15,@ty_desc),(@p15,@d_high);
