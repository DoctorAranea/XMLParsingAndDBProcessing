-- Database: XMLPlantCatalogs

-- DROP DATABASE IF EXISTS "XMLPlantCatalogs";

CREATE DATABASE "XMLPlantCatalogs"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Russian_Russia.1251'
    LC_CTYPE = 'Russian_Russia.1251'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- SEQUENCE: public.catalog_id_autoincrement

-- DROP SEQUENCE IF EXISTS public.catalog_id_autoincrement;

CREATE SEQUENCE IF NOT EXISTS public.catalog_id_autoincrement
    INCREMENT 1
    START 0
    MINVALUE 0
    MAXVALUE 9223372036854775807
    CACHE 1
    OWNED BY d_cat_catalog.id;

ALTER SEQUENCE public.catalog_id_autoincrement
    OWNER TO postgres;

-- SEQUENCE: public.plants_id_autoincrement

-- DROP SEQUENCE IF EXISTS public.plants_id_autoincrement;

CREATE SEQUENCE IF NOT EXISTS public.plants_id_autoincrement
    INCREMENT 1
    START 0
    MINVALUE 0
    MAXVALUE 9223372036854775807
    CACHE 1
    OWNED BY f_cat_plants.id;

ALTER SEQUENCE public.plants_id_autoincrement
    OWNER TO postgres;

-- Table: public.d_cat_catalog

-- DROP TABLE IF EXISTS public.d_cat_catalog;

CREATE TABLE IF NOT EXISTS public.d_cat_catalog
(
    id integer NOT NULL DEFAULT nextval('catalog_id_autoincrement'::regclass),
    uuid character varying(2000) COLLATE pg_catalog."default" NOT NULL,
    delivery_date timestamp without time zone,
    company character varying(2000) COLLATE pg_catalog."default",
    CONSTRAINT d_cat_catalog_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.d_cat_catalog
    OWNER to postgres;

-- Table: public.f_cat_plants

-- DROP TABLE IF EXISTS public.f_cat_plants;

CREATE TABLE IF NOT EXISTS public.f_cat_plants
(
    id integer NOT NULL DEFAULT nextval('plants_id_autoincrement'::regclass),
    common character varying(2000) COLLATE pg_catalog."default",
    botanical character varying(2000) COLLATE pg_catalog."default",
    zone character varying(2000) COLLATE pg_catalog."default",
    light character varying(2000) COLLATE pg_catalog."default",
    price numeric(15,2),
    availability integer,
    catalog_id integer NOT NULL,
    CONSTRAINT f_cat_plants_pkey PRIMARY KEY (id),
    CONSTRAINT fk_plants_catalog FOREIGN KEY (catalog_id)
        REFERENCES public.d_cat_catalog (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.f_cat_plants
    OWNER to postgres;