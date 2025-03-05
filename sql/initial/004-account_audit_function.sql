-- FUNCTION: public.accounts_audit()
-- DROP FUNCTION IF EXISTS public.accounts_audit();

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

ALTER FUNCTION public.accounts_audit()
  OWNER TO role_administrator;


-- Trigger: accounts_audit_trigger
-- DROP TRIGGER IF EXISTS accounts_audit_trigger ON public.accounts;

CREATE OR REPLACE TRIGGER accounts_audit_trigger
  AFTER INSERT OR UPDATE
  ON public.accounts
  FOR EACH ROW
EXECUTE FUNCTION public.accounts_audit();
