-- V1__init.sql
-- Initial schema generated from Hibernate / JPA (cleaned from pg_dump)

-- ============================================================
-- TABLES
-- ============================================================

CREATE TABLE public.user_roles (
                                   read_only boolean,
                                   visible boolean,
                                   changed_at timestamp without time zone,
                                   created_at timestamp without time zone,
                                   id bigint NOT NULL,
                                   user_id bigint NOT NULL,
                                   version bigint,
                                   user_role_type character varying(255),
                                   CONSTRAINT user_roles_user_role_type_check CHECK (
                                       ((user_role_type)::text = ANY (
                                       (ARRAY['ADMIN'::character varying, 'USER'::character varying])::text[]
                                       ))
    )
);

CREATE TABLE public.users (
                              read_only boolean,
                              visible boolean,
                              changed_at timestamp without time zone,
                              created_at timestamp without time zone,
                              id bigint NOT NULL,
                              version bigint,
                              password character varying(255),
                              user_name character varying(255)
);

-- ============================================================
-- SEQUENCES
-- ============================================================

CREATE SEQUENCE public.user_roles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ============================================================
-- CONSTRAINTS
-- ============================================================

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT ux_user_roles_user_role UNIQUE (user_id, user_role_type);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ux_users_user_name UNIQUE (user_name);

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f
    FOREIGN KEY (user_id) REFERENCES public.users(id);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX ix_user_roles_role_type
    ON public.user_roles USING btree (user_role_type);

CREATE INDEX ix_user_roles_user_id
    ON public.user_roles USING btree (user_id);
