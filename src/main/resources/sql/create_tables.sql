DROP TABLE IF EXISTS declarants;

CREATE TABLE declarants (
    id bigint,
    date_naissance integer,
    code_postal integer,
    sit_fam character varying(1),
    nombre_enfants smallint,
    salaires integer,
    codes_revenu character varying(1000),
    montant_ir integer,
    cluster smallint
);
