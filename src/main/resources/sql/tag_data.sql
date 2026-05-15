INSERT IGNORE INTO tag (tag_type, tag_value, sort_order) VALUES
-- GRADE
('GRADE', '중1', 1),
('GRADE', '중2', 2),
('GRADE', '중3', 3),
('GRADE', '고1', 4),
('GRADE', '고2', 5),
('GRADE', '고3', 6),
-- SEMESTER
('SEMESTER', '1학기', 1),
('SEMESTER', '2학기', 2),
-- UNIT
('UNIT', '수와 연산',    1),
('UNIT', '문자와 식',    2),
('UNIT', '함수',         3),
('UNIT', '기하',         4),
('UNIT', '확률과 통계',  5),
-- SUB_UNIT
('SUB_UNIT', '정수와 유리수', 1),
('SUB_UNIT', '일차방정식',   2),
('SUB_UNIT', '일차함수',     3),
('SUB_UNIT', '이차방정식',   4),
('SUB_UNIT', '이차함수',     5),
-- TYPE
('TYPE', '개념확인', 1),
('TYPE', '계산',    2),
('TYPE', '서술',    3),
('TYPE', '응용',    4),
-- DIFFICULTY
('DIFFICULTY', '하', 1),
('DIFFICULTY', '중', 2),
('DIFFICULTY', '상', 3);
