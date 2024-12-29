-- Table: public.accounts
-- DROP TABLE IF EXISTS public.accounts;

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

ALTER TABLE IF EXISTS public.accounts
  OWNER to role_administrator;


-- Index: idx_accounts_username
-- DROP INDEX IF EXISTS public.idx_accounts_username;

CREATE INDEX IF NOT EXISTS idx_accounts_username
  ON public.accounts USING btree (username COLLATE pg_catalog."default" ASC NULLS LAST)
  TABLESPACE pg_default;


-- Revoke all permissions from existing roles
REVOKE ALL ON TABLE public.accounts FROM role_administrator, role_maintainer, role_support, services;


-- Grant appropriate permissions
GRANT ALL ON TABLE public.accounts TO role_administrator;
GRANT SELECT, INSERT, UPDATE ON TABLE public.accounts TO role_maintainer, services;
GRANT SELECT ON TABLE public.accounts TO role_support;
