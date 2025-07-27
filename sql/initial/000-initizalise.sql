-- Role: role_administrator
-- DROP ROLE IF EXISTS role_administrator;

CREATE ROLE role_administrator;


-- Role: role_service
-- DROP ROLE IF EXISTS role_service;

CREATE ROLE role_service;


-- Role: role_maintainer
-- DROP ROLE IF EXISTS role_maintainer;

CREATE ROLE role_maintainer;


-- Role: support_role
-- DROP ROLE IF EXISTS support_role;

CREATE ROLE role_support;


-- User: services
-- DROP USER IF EXISTS services;

CREATE USER services WITH PASSWORD 'password';

-- Assign role to the user
GRANT role_service TO services;


-- User: user_maintainer
-- DROP USER IF EXISTS user_maintainer;

CREATE USER user_maintainer WITH PASSWORD 'password';

-- Assign role to the user
GRANT role_maintainer TO user_maintainer;


-- User: user_support
-- DROP USER IF EXISTS user_support;

CREATE USER user_support WITH PASSWORD 'password';

-- Assign role to the user
GRANT role_support TO user_support;


-- Database: device_registry
-- DROP DATABASE IF EXISTS device_registry;

CREATE DATABASE account_registry
  WITH
  OWNER = role_administrator
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.UTF-8'
  LC_CTYPE = 'en_US.UTF-8'
  LOCALE_PROVIDER = 'libc'
  TABLESPACE = pg_default
  CONNECTION LIMIT = -1
  IS_TEMPLATE = False;

COMMENT ON DATABASE account_registry
  IS 'Primary database for user account registration and identity management';
