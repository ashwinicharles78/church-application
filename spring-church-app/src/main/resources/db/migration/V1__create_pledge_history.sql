CREATE TABLE IF NOT EXISTS pledge_rate_change (
    id BIGINT NOT NULL AUTO_INCREMENT,
    family_id VARCHAR(255) NOT NULL,
    monthly_amount BIGINT NOT NULL,
    effective_date DATE NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_pledge_rate_family_effective (family_id, effective_date)
);

CREATE TABLE IF NOT EXISTS pledge_opening_balance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    family_id VARCHAR(255) NOT NULL,
    opening_due BIGINT NOT NULL,
    opening_credit BIGINT NOT NULL,
    as_of_date DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX ux_pledge_opening_family (family_id)
);