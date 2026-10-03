CREATE TABLE IF NOT EXISTS payment_voucher_serial_counter (
    voucher_year INT NOT NULL,
    next_number BIGINT NOT NULL,
    PRIMARY KEY (voucher_year)
);

CREATE TABLE IF NOT EXISTS payment_voucher (
    id BIGINT NOT NULL AUTO_INCREMENT,
    serial_number VARCHAR(32) NOT NULL,
    voucher_date DATE NOT NULL,
    approved_amount BIGINT NOT NULL,
    amount_in_words VARCHAR(500) NOT NULL,
    purpose VARCHAR(500) NOT NULL,
    payee VARCHAR(255) NOT NULL,
    bill_number VARCHAR(100),
    bill_date DATE,
    payment_method VARCHAR(20) NOT NULL,
    payment_reference_number VARCHAR(100),
    payment_reference_date DATE,
    total_bills_amount BIGINT NOT NULL,
    advance_amount BIGINT NOT NULL,
    net_amount BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    receipt_method VARCHAR(20),
    receipt_reference_number VARCHAR(100),
    receipt_date DATE,
    receipt_amount BIGINT,
    paid_by VARCHAR(255),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX ux_payment_voucher_serial (serial_number),
    INDEX idx_payment_voucher_created (created_at, id)
);