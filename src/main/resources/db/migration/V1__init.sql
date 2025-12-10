-- V1__init.sql
-- Initial schema for vaaskel_dev created from the JPA-generated database schema.
-- All comments are written in English by convention.

-- ===================================================================
-- SEQUENCES
-- ===================================================================

CREATE SEQUENCE public.user_roles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE CACHE 1;

-- ===================================================================
-- TABLES
-- ===================================================================

CREATE TABLE public.user_roles
(
    read_only      boolean,
    visible        boolean,
    changed_at     timestamp without time zone,
    created_at     timestamp without time zone,
    id             bigint NOT NULL,
    user_id        bigint NOT NULL,
    version        bigint,
    user_role_type character varying(255),
    CONSTRAINT user_roles_user_role_type_check CHECK (
        (user_role_type)::text = ANY (
        (ARRAY['ADMIN':: character varying, 'USER':: character varying])::text[]
        )
)
    );

CREATE TABLE public.users
(
    read_only  boolean,
    visible    boolean,
    changed_at timestamp without time zone,
    created_at timestamp without time zone,
    id         bigint NOT NULL,
    version    bigint,
    password   character varying(255),
    user_name  character varying(255)
);

-- ===================================================================
-- CONSTRAINTS
-- ===================================================================

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f
    FOREIGN KEY (user_id) REFERENCES public.users(id);
