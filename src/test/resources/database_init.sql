Drop Schema blog;
create schema blog;
use blog;

-- CREAZIONE TABELLA PER I TAG
CREATE TABLE IF NOT EXISTS tags (
	nome varchar(50) primary key
);
-- CREAZIONE TABELLA PER LE CATEGORIE
CREATE TABLE IF NOT EXISTS categorie (
	nome varchar(50) primary key
);
-- CREAZIONE TABELLA PER GLI UTENTI
CREATE TABLE IF NOT EXISTS users (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL unique,
  password varchar(100) NOT NULL,
  PRIMARY KEY (id)
);
-- CREAZIONE TABELLA PER GLI ARTICOLI
CREATE TABLE IF NOT EXISTS articoli (
	id bigint unsigned not null auto_increment,
    stato varchar(10) not null,
    titolo varchar(50) not null,
    sottotitolo varchar(50),
    testo text(500) not null,
    categoria varchar(50),
    autore bigint unsigned not null,
    data_pubblicazione Timestamp,
    data_modifica DateTime,
    data_creazione DateTime,
    constraint fk_users_articoli foreign key (autore) references users(id) on delete cascade,
    constraint fk_articoli_categoria foreign key (categoria) references categorie(nome) on delete cascade,
    primary key (id)
);
-- CREAZIONE TABELLA N-N TRA TAG E ARTICOLI
CREATE TABLE IF NOT EXISTS tags_articoli(
	id_articolo bigint unsigned,
    nome_tag varchar(50),
    constraint fk_articoli_tags foreign key (id_articolo) references articoli(id),
    constraint fk_tags_articoli foreign key (nome_tag) references tags(nome),
    primary key(id_articolo, nome_tag)
);

--
-- ddinuzzo/password02
INSERT INTO users
(username, password)
VALUES('ddinuzzo', '$2a$10$vj3PqvSqQSsLhknZpxU2oOIUOdmm6cpPu1shwcyXHVzba.xBWLe4K');
