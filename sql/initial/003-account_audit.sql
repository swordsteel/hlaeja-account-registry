-- Table: public.accounts_audit
-- DROP TABLE IF EXISTS public.accounts_audit;

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

ALTER TABLE IF EXISTS public.accounts_audit
  OWNER to role_administrator;


-- Revoke all permissions from existing roles
REVOKE ALL ON TABLE public.accounts_audit FROM role_administrator, role_maintainer, role_support, services;


-- Grant appropriate permissions to each role
GRANT ALL ON TABLE public.accounts_audit TO role_administrator;
GRANT SELECT, INSERT ON TABLE public.accounts_audit TO services;
GRANT SELECT ON TABLE public.accounts_audit TO role_maintainer, role_support;
