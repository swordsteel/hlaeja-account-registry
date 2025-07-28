-- FUNCTION: public.gen_uuid_v7(timestamp with time zone)
CREATE OR REPLACE FUNCTION public.gen_uuid_v7(p_timestamp timestamp with time zone)
  RETURNS uuid
  LANGUAGE 'sql'
  COST 100
  VOLATILE PARALLEL UNSAFE
AS
$BODY$
-- Replace the first 48 bits of a uuid v4 with the provided timestamp (in milliseconds) since 1970-01-01 UTC, and set the version to 7
SELECT encode(set_bit(set_bit(overlay(uuid_send(gen_random_uuid()) PLACING substring(int8send((extract(EPOCH FROM p_timestamp) * 1000):: BIGINT) FROM 3) FROM 1 FOR 6), 52, 1), 53, 1), 'hex') ::uuid;
$BODY$;

-- FUNCTION: public.gen_uuid_v7()
CREATE OR REPLACE FUNCTION public.gen_uuid_v7()
  RETURNS uuid
  LANGUAGE 'sql'
  COST 100
  VOLATILE PARALLEL UNSAFE
AS
$BODY$
SELECT gen_uuid_v7(clock_timestamp());
$BODY$;

-- Table: public.accounts
CREATE TABLE IF NOT EXISTS public.accounts
(
  id         UUID                              DEFAULT gen_uuid_v7(),
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enabled    boolean                  NOT NULL DEFAULT true,
  username   VARCHAR(50) UNIQUE       NOT NULL,
  password   VARCHAR(255)             NOT NULL,
  roles      VARCHAR(255)             NOT NULL,
  CONSTRAINT pk_contact_types PRIMARY KEY (id)
);

-- Index: idx_accounts_username
CREATE INDEX IF NOT EXISTS idx_accounts_username
  ON public.accounts USING btree (username COLLATE pg_catalog."default" ASC NULLS LAST)
  TABLESPACE pg_default;

-- Table: public.accounts_audit
CREATE TABLE IF NOT EXISTS public.accounts_audit
(
  id        uuid                     NOT NULL,
  timestamp timestamp with time zone NOT NULL,
  enabled   boolean                  NOT NULL,
  username  VARCHAR(50)              NOT NULL,
  password  VARCHAR(255)             NOT NULL,
  roles     VARCHAR(255)             NOT NULL,
  CONSTRAINT pk_accounts_audit PRIMARY KEY (id, timestamp)
) TABLESPACE pg_default;

-- FUNCTION: public.accounts_audit()
CREATE OR REPLACE FUNCTION public.accounts_audit()
  RETURNS trigger
  LANGUAGE 'plpgsql'
  COST 100
  VOLATILE NOT LEAKPROOF
AS
$BODY$
BEGIN
  INSERT INTO accounts_audit (id, timestamp, enabled, username, password, roles)
  VALUES (NEW.id, NEW.updated_at, NEW.enabled, NEW.username, NEW.password, NEW.roles);
  RETURN NULL; -- result is ignored since this is an AFTER trigger
END;
$BODY$;

-- Trigger: accounts_audit_trigger
CREATE OR REPLACE TRIGGER accounts_audit_trigger
  AFTER INSERT OR UPDATE
  ON public.accounts
  FOR EACH ROW
EXECUTE FUNCTION public.accounts_audit();
