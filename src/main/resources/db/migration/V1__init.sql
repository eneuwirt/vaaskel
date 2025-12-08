-- Initial schema migration for Vaaskel
-- This migration creates the core user and user role tables and their sequences.

-- Sequences
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

-- Users table
CREATE TABLE public.users (
    read_only boolean,
    version integer NOT NULL,
    visible boolean,
    changed_at timestamp without time zone,
    created_at timestamp without time zone,
    id bigint NOT NULL,
    password character varying(255),
    user_name character varying(255)
);

-- User roles table
CREATE TABLE public.user_roles (
    read_only boolean,
    version integer NOT NULL,
    visible boolean,
    changed_at timestamp without time zone,
    created_at timestamp without time zone,
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    user_role_type character varying(255),
    CONSTRAINT user_roles_user_role_type_check CHECK (
        (user_role_type)::text = ANY (
            (ARRAY['ADMIN'::character varying, 'USER'::character varying])::text[]
        )
    )
);

-- Primary keys
ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

-- Foreign key constraints
ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f
        FOREIGN KEY (user_id) REFERENCES public.users(id);
