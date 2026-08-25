INSERT INTO book (title, author, year)
SELECT 'Clean Code', 'Robert C. Martin', 2008
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'Clean Code');

INSERT INTO book (title, author, year)
SELECT 'The Pragmatic Programmer', 'David Thomas', 1999
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'The Pragmatic Programmer');

INSERT INTO book (title, author, year)
SELECT 'Design Patterns', 'Gang of Four', 1994
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'Design Patterns');

INSERT INTO book (title, author, year)
SELECT 'Refactoring', 'Martin Fowler', 2018
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'Refactoring');

INSERT INTO book (title, author, year)
SELECT 'The Clean Coder', 'Robert C. Martin', 2011
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'The Clean Coder');
