DROP DATABASE IF EXISTS Treningsdagbok;
CREATE DATABASE Treningsdagbok;
USE Treningsdagbok;

DROP TABLE IF EXISTS Bruker;
DROP TABLE IF EXISTS Treningsokt;
DROP TABLE IF EXISTS Notat;
DROP TABLE IF EXISTS Apparat;
DROP TABLE IF EXISTS Ovelse;
DROP TABLE IF EXISTS TreningsoktTilOvelse;
DROP TABLE IF EXISTS OvelseGruppe;
DROP TABLE IF EXISTS OvelsesgruppeTilOvelse;

CREATE TABLE Bruker(
	brukernavn VARCHAR(50),
	alder INTEGER,
	CONSTRAINT Bruker_PK PRIMARY KEY(brukernavn)
);

CREATE TABLE Treningsokt(
	treningsoktID INTEGER auto_increment,
	brukernavn VARCHAR(50),
	dato DATE,
	tidspunkt TIME,
	varighet INTEGER, /*Enhet sekunder */
	CONSTRAINT treningsokt_PK PRIMARY KEY(treningsoktID),
	CONSTRAINT treningsokt_FK FOREIGN KEY(brukernavn) REFERENCES bruker(brukernavn)
		ON UPDATE CASCADE ON DELETE CASCADE

);

CREATE TABLE Notat(
	Id INTEGER auto_increment,
	treningsoktID INTEGER,
	Personlig_form INTEGER,
	Prestasjon INTEGER,
	Treningsformaal VARCHAR(500),
	Opplevelse_av_trening VARCHAR(500),
	CHECK (Personlig_form < 11 AND Personlig_form > 0),
	CHECK (Prestasjon < 11 AND Prestasjon > 0),
	CONSTRAINT notat_PK PRIMARY KEY(Id),
	CONSTRAINT notat_FK FOREIGN KEY(treningsoktID) REFERENCES treningsokt(treningsoktID)
		ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Apparat(
	apparatNavn VARCHAR(50),
	beskrivelse VARCHAR(250),
    CONSTRAINT Appart_PK PRIMARY KEY(apparatNavn)
);

CREATE TABLE Ovelse(
	ovelseNavn VARCHAR(50),
	harApparat BOOLEAN,
	apparatNavn VARCHAR(50),
	Bekrivelse VARCHAR(250),
	CONSTRAINT Ovelse_PK PRIMARY KEY(ovelseNavn),
	CONSTRAINT Ovelse_FK FOREIGN KEY(apparatNavn) REFERENCES Apparat(apparatNavn),
	CONSTRAINT subclassUtenApparat CHECK((NOT harApparat AND Beskrivelse IS NOT NULL) OR (harApparat))
);

CREATE TABLE TreningsoktTilOvelse(
	treningsoktID INTEGER,
	ovelseNavn VARCHAR(50),
    Kilo INTEGER,
	Sett INTEGER,
	CONSTRAINT treningsoktTilOvelse_PK PRIMARY KEY(treningsoktID, ovelseNavn),
	CONSTRAINT treningsoktTilOvelse_FK1 FOREIGN KEY(treningsoktID) REFERENCES Treningsokt(treningsoktID)
		ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT treningsoktTilOvelse_FK2 FOREIGN KEY(ovelseNavn) REFERENCES Ovelse(ovelseNavn) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE OvelseGruppe(
	ovelseGruppeNavn VARCHAR(50),
	CONSTRAINT ovelseGruppe_PK PRIMARY KEY(ovelseGruppeNavn)
);

CREATE TABLE OvelsesgruppeTilOvelse(
	ovelseNavn VARCHAR(50),
    ovelseGruppeNavn VARCHAR(50),
    CONSTRAINT OvelsegruppeTilOvelse_PK PRIMARY KEY(ovelseNavn, ovelseGruppeNavn),
    CONSTRAINT OvelsegruppeTilOvelse_FK1 FOREIGN KEY(ovelseNavn) REFERENCES Ovelse(ovelseNavn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT OvelsegruppeTilOvelse_FK2 FOREIGN KEY(ovelseGruppeNavn) REFERENCES OvelseGruppe(ovelseGruppeNavn) ON UPDATE CASCADE ON DELETE CASCADE
);

