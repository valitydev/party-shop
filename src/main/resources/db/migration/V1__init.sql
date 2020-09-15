CREATE SCHEMA IF NOT EXISTS ps;

CREATE TABLE ps.party_shop_reference(
  shop_id                                         CHARACTER VARYING           NOT NULL,
  party_id                                        CHARACTER VARYING           NOT NULL,
  category_id                                     INT                         NOT NULL,
  CONSTRAINT party_shop_reference_pkey PRIMARY KEY (shop_id)
);

CREATE INDEX party_shop_reference_id on ps.party_shop_reference(party_id);