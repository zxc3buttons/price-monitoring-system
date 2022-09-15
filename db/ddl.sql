CREATE
DATABASE price_monitoring_system
WITH 
OWNER = postgres
ENCODING = 'UTF8'
CONNECTION LIMIT = -1;

\c price_monitoring_system;

create sequence user_id_seq start with 100 increment by 1;
create sequence role_id_seq start with 100 increment by 1;
create sequence product_seq_id start with 100 increment by 1;
create sequence category_id_seq start with 100 increment by 1;
create sequence market_place_id_seq start with 100 increment by 1;

CREATE TABLE "role"
(
    id   bigint            NOT NULL DEFAULT nextval('role_id_seq'),
    name character varying NOT NULL,
    PRIMARY KEY (id)
);

ALTER SEQUENCE role_id_seq OWNED BY role.id;
ALTER TABLE IF EXISTS "role"
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS "users"
(
    id bigint NOT NULL DEFAULT nextval
(
    'user_id_seq'
),
    username character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name character varying COLLATE pg_catalog."default" NOT NULL,
    email character varying COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    created timestamp without time zone NOT NULL,
    updated timestamp
                      without time zone NOT NULL,
    role_id bigint not null,
    PRIMARY KEY
(
    id
),
    CONSTRAINT user_role_id_fk FOREIGN KEY
(
    role_id
)
    REFERENCES role
(
    id
) MATCH SIMPLE
                      ON UPDATE NO ACTION
                      ON DELETE NO ACTION
    NOT VALID
    );

ALTER SEQUENCE user_id_seq OWNED BY "users".id;
ALTER TABLE IF EXISTS "users"
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS category
(
    id bigint NOT NULL DEFAULT nextval
(
    'category_id_seq'
),
    name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Category_pkey" PRIMARY KEY
(
    id
)
    );

ALTER SEQUENCE category_id_seq OWNED BY category.id;
ALTER TABLE IF EXISTS category
    OWNER to postgres;

CREATE TABLE marketplace
(
    id   bigint            NOT NULL DEFAULT nextval('market_place_id_seq'),
    name character varying NOT NULL,
    PRIMARY KEY (id)
);

ALTER SEQUENCE market_place_id_seq OWNED BY marketplace.id;
ALTER TABLE IF EXISTS marketplace
    OWNER to postgres;

CREATE TABLE product
(
    id          bigint            NOT NULL DEFAULT nextval('product_seq_id'),
    name        character varying NOT NULL,
    category_id bigserial         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT product_category_id_fk FOREIGN KEY (category_id)
        REFERENCES category (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

ALTER SEQUENCE product_seq_id OWNED BY product.id;
ALTER TABLE IF EXISTS product
    OWNER to postgres;

create table item
(
    product_id     bigint    not null
        constraint item_product_id_fk
            references product,
    price          bigint    not null,
    date_start     timestamp not null,
    marketplace_id bigint    not null
        constraint item_marketplace_id_fk
            references marketplace,
    date_end       timestamp not null,
    serial_number  bigserial,
    constraint item_pk
        unique (price, date_start, date_end, product_id, marketplace_id)
);

alter table IF EXISTS item
    owner to postgres;

create unique index item_serial_number_uindex
    on item (serial_number);

INSERT INTO "role"
values (101, 'ROLE_ADMIN');
INSERT INTO "role"
values (102, 'ROLE_USER');
INSERT INTO "role"
values (103, 'ROLE_UNDEFINED');

INSERT INTO "users"
values (1, 'oleg', 'Oleg', 'Tokarev', 'oleg@mail.ru', '$2a$12$jegcrhOb9pDtXdIGKpYB1ervHNx6yiXoNSIL0bBrhfEOVJhbXx31i',
        current_timestamp, current_timestamp, 101);
INSERT INTO "users"
values (2, 'misha', 'Misha', 'Voronkov', 'misha@mail.ru',
        '$2a$12$jegcrhOb9pDtXdIGKpYB1ervHNx6yiXoNSIL0bBrhfEOVJhbXx31i', current_timestamp, current_timestamp, 102);