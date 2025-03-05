-- FUNCTION: public.gen_uuid_v7(timestamp with time zone)

-- DROP FUNCTION IF EXISTS public.gen_uuid_v7(timestamp with time zone);

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

ALTER FUNCTION public.gen_uuid_v7(timestamp with time zone)
  OWNER TO role_administrator;

COMMENT
  ON FUNCTION public.gen_uuid_v7(timestamp with time zone)
  IS 'Generate a UUIDv7 value using a provided timestamp (in milliseconds since 1970-01-01 UTC) with 74 bits of randomness.';


-- FUNCTION: public.gen_uuid_v7()

-- DROP FUNCTION IF EXISTS public.gen_uuid_v7();

CREATE OR REPLACE FUNCTION public.gen_uuid_v7()
  RETURNS uuid
  LANGUAGE 'sql'
  COST 100
  VOLATILE PARALLEL UNSAFE
AS
$BODY$
SELECT gen_uuid_v7(clock_timestamp());
$BODY$;

ALTER FUNCTION public.gen_uuid_v7()
  OWNER TO role_administrator;

COMMENT
  ON FUNCTION public.gen_uuid_v7()
  IS 'Generate a UUIDv7 value with a 48-bit timestamp (millisecond precision) and 74 bits of randomness.';
