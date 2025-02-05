-- Disable triggers on the tables
ALTER TABLE accounts DISABLE TRIGGER ALL;
ALTER TABLE accounts_audit DISABLE TRIGGER ALL;

-- Truncate tables
TRUNCATE TABLE accounts_audit;
TRUNCATE TABLE accounts;

-- Enable triggers on the account table
ALTER TABLE accounts ENABLE TRIGGER ALL;
ALTER TABLE accounts_audit ENABLE TRIGGER ALL;
