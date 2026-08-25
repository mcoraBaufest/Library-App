CREATE TABLE IF NOT EXISTS book (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    title  VARCHAR(255),
    author VARCHAR(255),
    year   INT NOT NULL
);

CREATE TABLE IF NOT EXISTS book_loan (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_name   VARCHAR(255) NOT NULL,
    book_id     INT NOT NULL,
    loan_date   TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    status      VARCHAR(20) NOT NULL,
    CONSTRAINT fk_book_loan_book FOREIGN KEY (book_id) REFERENCES book(id)
);
