CREATE TABLE IF NOT EXISTS loan_calculation
(
    id                      INT    NOT NULL AUTO_INCREMENT,
    amount                  DOUBLE NOT NULL,
    annual_interest_percent DOUBLE NOT NULL,
    number_of_months        INT NOT NULL,
    monthly_payment         DOUBLE NOT NULL,
    total_interest_paid     DOUBLE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS payment
(
    id              INT    NOT NULL AUTO_INCREMENT,
    calculation_id  INT    NOT NULL,
    payment_order   INT    NOT NULL,
    payment_amount  DOUBLE NOT NULL,
    principal_amount DOUBLE NOT NULL,
    interest_amount  DOUBLE NOT NULL,
    balance_owed     DOUBLE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (calculation_id) REFERENCES loan_calculation (id) ON DELETE CASCADE
);
