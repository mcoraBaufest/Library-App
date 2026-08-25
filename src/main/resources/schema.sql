CREATE TABLE IF NOT EXISTS book (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    title  VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    year   INT NOT NULL
);

ALTER TABLE book
    ADD CONSTRAINT IF NOT EXISTS uk_book_title UNIQUE (title);

CREATE TABLE IF NOT EXISTS app_user (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS book_loan (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    book_id     INT NOT NULL,
    loan_date   TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    status      VARCHAR(20) NOT NULL,
    CONSTRAINT fk_book_loan_book FOREIGN KEY (book_id) REFERENCES book(id),
    CONSTRAINT fk_book_loan_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);
