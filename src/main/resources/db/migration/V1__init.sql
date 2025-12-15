-- V1__init.sql
-- Generated from jpa_schema_dump.sql
-- Target: PostgreSQL
-- Note: flyway_schema_history intentionally excluded

BEGIN;

-- ======================================================
-- SEQUENCES
-- ======================================================

CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.user_roles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.user_settings_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ======================================================
-- TABLE: users
-- ======================================================

CREATE TABLE public.users (
                              account_non_expired        boolean NOT NULL,
                              account_non_locked         boolean NOT NULL,
                              credentials_non_expired    boolean NOT NULL,
                              enabled                    boolean NOT NULL,

                              read_only                  boolean,
                              visible                    boolean,
                              changed_at                 timestamp without time zone,
                              created_at                 timestamp without time zone,

                              id                          bigint NOT NULL,
                              version                     bigint,

                              user_name                   character varying(100) NOT NULL,
                              password_hash               character varying(255) NOT NULL
);

ALTER TABLE public.users
    ALTER COLUMN id SET DEFAULT nextval('public.users_seq');

ALTER TABLE public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE public.users
    ADD CONSTRAINT users_user_name_key UNIQUE (user_name);

-- ======================================================
-- TABLE: user_roles
-- ======================================================

CREATE TABLE public.user_roles (
                                   read_only        boolean,
                                   visible          boolean,
                                   changed_at       timestamp without time zone,
                                   created_at       timestamp without time zone,

                                   id               bigint NOT NULL,
                                   user_id          bigint NOT NULL,
                                   version          bigint,

                                   user_role_type   character varying(50) NOT NULL,

                                   CONSTRAINT user_roles_user_role_type_check
                                       CHECK (
                                           (user_role_type)::text = ANY (
                                           (ARRAY['ADMIN', 'USER'])::text[]
                                           )
)
    );

ALTER TABLE public.user_roles
    ALTER COLUMN id SET DEFAULT nextval('public.user_roles_seq');

ALTER TABLE public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

ALTER TABLE public.user_roles
    ADD CONSTRAINT ux_user_roles_user_role UNIQUE (user_id, user_role_type);

-- ======================================================
-- TABLE: user_settings
-- ======================================================

CREATE TABLE public.user_settings (
                                      read_only         boolean,
                                      visible           boolean,
                                      changed_at        timestamp without time zone,
                                      created_at        timestamp without time zone,

                                      id                bigint NOT NULL,
                                      user_id           bigint NOT NULL,
                                      version           bigint,

                                      theme_preference  character varying(16) NOT NULL,

                                      CONSTRAINT user_settings_theme_preference_check
                                          CHECK (
                                              (theme_preference)::text = ANY (
                                              (ARRAY['SYSTEM', 'LIGHT', 'DARK'])::text[]
                                              )
)
    );

ALTER TABLE public.user_settings
    ALTER COLUMN id SET DEFAULT nextval('public.user_settings_seq');

ALTER TABLE public.user_settings
    ADD CONSTRAINT user_settings_pkey PRIMARY KEY (id);

ALTER TABLE public.user_settings
    ADD CONSTRAINT user_settings_user_id_key UNIQUE (user_id);

-- ======================================================
-- INDEXES
-- ======================================================

CREATE INDEX ix_user_roles_role_type
    ON public.user_roles (user_role_type);

CREATE INDEX ix_user_roles_user_id
    ON public.user_roles (user_id);

-- ======================================================
-- FOREIGN KEYS
-- ======================================================

ALTER TABLE public.user_settings
    ADD CONSTRAINT fk_user_settings_user
        FOREIGN KEY (user_id) REFERENCES public.users(id);

ALTER TABLE public.user_roles
    ADD CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES public.users(id);

COMMIT;
