CREATE SEQUENCE loan_calculation_seq;

CREATE TABLE IF NOT EXISTS loan_calculation
(
    id                      INT    NOT NULL DEFAULT NEXTVAL ('loan_calculation_seq'),
    amount                  DOUBLE PRECISION NOT NULL,
    annual_interest_percent DOUBLE PRECISION NOT NULL,
    number_of_months        INT NOT NULL,
    monthly_payment         DOUBLE PRECISION NOT NULL,
    total_interest_paid     DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (id)
);

CREATE SEQUENCE payment_seq;

CREATE TABLE IF NOT EXISTS payment
(
    id              INT    NOT NULL DEFAULT NEXTVAL ('payment_seq'),
    calculation_id  INT    NOT NULL,
    payment_order   INT    NOT NULL,
    payment_amount  DOUBLE PRECISION NOT NULL,
    principal_amount DOUBLE PRECISION NOT NULL,
    interest_amount  DOUBLE PRECISION NOT NULL,
    balance_owed     DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (calculation_id) REFERENCES loan_calculation (id) ON DELETE CASCADE
);
