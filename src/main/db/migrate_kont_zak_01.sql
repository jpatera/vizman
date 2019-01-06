
-- !!!!  Do BEFORE tables are created:
SET COLLATION CZECH STRENGTH SECONDARY;


-- =============================================

DROP TABLE VIZMAN.CFGPROP;

CREATE TABLE VIZMAN.CFGPROP (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	VALUE VARCHAR(255),
	ORD INTEGER NOT NULL,
	LABEL VARCHAR(64) NOT NULL,
	DESCRIPTION VARCHAR(255),
	RO BOOLEAN,
	CONSTRAINT PK_CFGPROP PRIMARY KEY (NAME)
);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(1, 1, 'app.locale', 'cs_CZ', 110, 'N�rodn� t��d�n� a form�ty', 'N�rodn� t��d�n� a form�ty', true);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(2, 1, 'app.project.root.local', 'L:\\projet-root-dir', 120, 'Ko�enov� adres�� pro projekty (stanice)', 'Ko�enov� adres�� pro projekty z pohledu pracovn�ch stanic', false);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(2, 1, 'app.project.root.server', '\\proj_path\projet-root-dir', 120, 'Ko�enov� adres�� pro projekty (server)', 'Ko�enov� adres�� pro projekty z pohledu serveru', false);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(3, 1, 'app.document.root.local', 'T:\\document-root-dir', 130, 'Ko�enov� adres�� pro dokumeny (stanice)', 'Ko�enov� adres�� pro dokumeny z pohledu pracovn�ch stanic', false);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(3, 1, 'app.document.root.server', '\\doc_path\document-root-dir', 130, 'Ko�enov� adres�� pro dokumeny (server)', 'Ko�enov� adres�� pro dokumeny z pohledu serveru', false);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(4, 1, 'app.koef.pojist', '0.35', 140, 'Koeficient poji�t�n�', 'Pou��v� se p�i v�po�tech vyhodnocovac�ch tabulek', false);

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(5, 1, 'app.koef.rezie', '0.8', 150, 'Koeficient re�ie', 'Pou��v� se p�i v�po�tech vyhodnocovac�ch tabulek', false);


COMMIT;

-- =============================================

DROP TABLE VIZMAN.CIN;

CREATE TABLE VIZMAN.CIN (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	PORADI SMALLINT NOT NULL,
	CINT1 VARCHAR(2),
	CINT2 VARCHAR(2),
	AKCE VARCHAR(32),
	CINNOST VARCHAR(24),
	CALCPRAC BOOLEAN,
	TMP VARCHAR(1),
	CONSTRAINT PK_CIN PRIMARY KEY (PORADI)
);

INSERT INTO VIZMAN.CIN
(ID, VERSION, PORADI, CINT1, CINT2, AKCE, CINNOST, CALCPRAC, TMP)
SELECT CIN_ID, 1, PORADI, CIN_T1, CIN_T2, AKCE, CINNOST, CALCPRAC, TMP 
FROM ZAVIN.CIN_;

COMMIT;

-- =============================================

-- DROP TABLE VIZMAN.PERSON

CREATE TABLE VIZMAN.PERSON (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	STATUS VARCHAR(255),
	USERNAME VARCHAR(255),
	PASSWORD VARCHAR(255),
	JMENO VARCHAR(255),
	PRIJMENI VARCHAR(255),
	NASTUP DATE,
	VYSTUP DATE,
	SAZBA DECIMAL(19,2),
	CONSTRAINT PK_PERSON PRIMARY KEY (ID)
);

INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATUS, JMENO, PRIJMENI, PASSWORD, USERNAME, NASTUP, VYSTUP, SAZBA)
VALUES(1001, 0, 'ACTIVE', 'Admin', 'Systemak', 'admin', 'admin', NULL, NULL, 0);
INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATUS, JMENO, PRIJMENI, PASSWORD, USERNAME, NASTUP, VYSTUP, SAZBA)
VALUES(1002, 0, 'ACTIVE', 'User', 'B�n� user', 'user', 'user', NULL, NULL, 0);

INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATUS, JMENO, PRIJMENI, PASSWORD, USERNAME, NASTUP, VYSTUP, SAZBA, STATUS)
SELECT USER_ID, 1, 'ACTIVE, 'USER_FIRST, USER_LAST, USER_PWD, USER_LOGIN, USER_NASTUP, USER_VYSTUP, USER_SAZBA, USER_STATUS 
FROM ZAVIN.USR_;

COMMIT;


--  =============================================


CREATE TABLE VIZMAN."ROLE" (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	DESCRIPTION VARCHAR(255),
	NAME VARCHAR(255),
	CONSTRAINT PK_ROLE PRIMARY KEY (ID)
);

CREATE UNIQUE INDEX IDXQ_PK_ROLE ON VIZMAN."ROLE" (ID);

-- INSERT INTO VIZMANDB.VIZMAN."ROLE" (ID, VERSION, DESCRIPTION, NAME)
-- VALUES(1, 1, 'Administr�tor - v�echna existuj�c� opr�vn�n�', 'ROLE_ADMIN');

-- INSERT INTO VIZMANDB.VIZMAN."ROLE" (ID, VERSION, DESCRIPTION, NAME)
-- VALUES(2, 1, 'U�ivatel - b�n� opr�vn�n�', 'ROLE_USER');

-- INSERT INTO VIZMANDB.VIZMAN."ROLE" (ID, VERSION, DESCRIPTION, NAME)
-- VALUES(3, 1, 'Manager - jako ROLE_USER plus fakturace a honor��e', 'ROLE_MANAGER');


--  =============================================

-- DROP TABLE VIZMAN.PERSON_ROLE

CREATE TABLE VIZMAN.PERSON_ROLE (
	PERSON_ID BIGINT NOT NULL,
	ROLE_ID BIGINT NOT NULL,
	CONSTRAINT PK_PERSON_ROLE PRIMARY KEY (PERSON_ID,ROLE_ID),
	CONSTRAINT FK_PERSON_ROLE_ROLE FOREIGN KEY (ROLE_ID) REFERENCES VIZMAN."ROLE"(ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT FK_PERSON_ROLE_PERSON FOREIGN KEY (PERSON_ID) REFERENCES VIZMAN.PERSON(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);
-- CREATE UNIQUE INDEX IDXQ_PK_PERSON_ROLE ON VIZMAN.PERSON_ROLE (PERSON_ID,ROLE_ID);
-- CREATE INDEX IDX_FK_PERSON_ROLE_ROLE ON VIZMAN.PERSON_ROLE (ROLE_ID);
-- CREATE INDEX IDX_FK_PERSON_ROLE_PERSON ON VIZMAN.PERSON_ROLE (PERSON_ID);

--  =============================================


DROP TABLE VIZMAN.DOCH

CREATE TABLE VIZMAN.DOCH (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	PERSON_ID BIGINT NOT NULL,
	USER_LOGIN VARCHAR(15),
--	USER_EDIT_AD VARCHAR(20),

--	D_YEAR SMALLINT,
--	D_MONTH SMALLINT,
--	D_DAY SMALLINT,
--	D_ROKMES VARCHAR(7),
	D_DATE DATE,
--	D_DK VARCHAR(2),
	Z_DATCASOD TIMESTAMP,
	D_CAS_OD TIME,
	R_OD SMALLINT,
	Z_DATCASDO TIMESTAMP,
	D_CAS_DO TIME,
	R_DO SMALLINT,
	D_HODIN TIME,
	CIN_ID INTEGER,
	CIN_POL SMALLINT,
	CIN_ST VARCHAR(2),
	
	CIN_T1 VARCHAR(2),
	CIN_T2 VARCHAR(2),
	CINNOST VARCHAR(24),
	CALCPRAC BOOLEAN,
	POZNAMKA VARCHAR(120),
	TMP VARCHAR(1),
--	M_DATCAS TIMESTAMP,
	CONSTRAINT PK_DOCH PRIMARY KEY (ID)
);

COMMIT;

-- (DOCH_ID, USER_LOGIN, USER_EDIT_AD, D_YEAR, D_MONTH, D_DAY, D_ROKMES, D_DATUM, D_DK, Z_DATCASOD, D_CAS_OD, R_OD, Z_DATCASDO, D_CAS_DO, R_DO, D_HODIN, CIN_ID, CIN_POL, CIN_ST, CIN_T1, CIN_T2, CINNOST, CALCPRAC, POZNAMKA, TMP, M_DATCAS)

INSERT INTO VIZMAN.DOCH
  (ID, VERSION, PERSON_ID, USER_LOGIN, D_DATE,
  Z_DATCASOD, D_CAS_OD, R_OD,
  Z_DATCASDO, D_CAS_DO, R_DO,
  D_HODIN,
  CIN_ID, CIN_POL, CIN_ST,
  CIN_T1, CIN_T2, CINNOST, CALCPRAC, POZNAMKA, TMP)
SELECT
  DOCH_ID, 1, 0, USER_LOGIN, D_DATUM,
  Z_DATCASOD, D_CAS_OD, R_OD,
  Z_DATCASDO, D_CAS_DO, R_DO,
  D_HODIN,
  CIN_ID, CIN_POL, CIN_ST,
  CIN_T1, CIN_T2, CINNOST, CALCPRAC, POZNAMKA, TMP
FROM ZAVIN.DOCH_
;

SELECT count (DOCH_ID) FROM ZAVIN.DOCH_;
SELECT count (ID) FROM VIZMAN.DOCH;


-- Set PERSON IDs:
UPDATE VIZMAN.DOCH AS doch
SET doch.PERSON_ID = (SELECT top 1 PERSON.ID FROM VIZMAN.PERSON person
		WHERE DOCH.USER_LOGIN = PERSON.USERNAME)
WHERE doch.USER_LOGIN IN (SELECT person.USERNAME from VIZMAN.PERSON person where person.USERNAME = doch.USER_LOGIN)
;

COMMIT;


DROP SEQUENCE VIZMAN.DOCH_SEQ;
CREATE SEQUENCE IF NOT EXISTS VIZMAN.DOCH_SEQ
  START WITH 100001
  INCREMENT BY 1
  CACHE 1
;

CREATE UNIQUE INDEX IDXQ_DOCH_PERSONID_DDATE_ ON VIZMAN.DOCH (PERSON_ID, D_DATE, CIN_POL);

--  =============================================


-- Kontrola:

SELECT CISLO_ZAKAZKY, LENGTH(TEXT) AS LEN, TEXT FROM zavin.zak_
ORDER BY LEN DESC
;


-- TRUNCATE TABLE vizman.kont;
DROP TABLE VIZMAN.KONTDOC;
DROP TABLE VIZMAN.ZAKDOC;
DROP TABLE VIZMAN.FAKT;
DROP TABLE VIZMAN.ZAK;
DROP TABLE VIZMAN.KONT;


COMMIT;



--CREATE TABLE VIZMAN.ZAK (
-- 	ID BIGINT DEFAULT NOT NULL AUTO_INCREMENT,
-- 	CZAK VARCHAR(30) NOT NULL,
-- 	TEXT VARCHAR(255),
-- 	FIRMA VARCHAR(30),
---- 	HONORAR DECIMAL(100,4) DEFAULT 0,
---- 	ROZPRAC DECIMAL(100,4) DEFAULT 0,
-- 	DATUMZAD TIMESTAMP,
-- 	ARCH BOOLEAN DEFAULT FALSE
--);
-- 
--CREATE UNIQUE INDEX PRIMARY_KEY_ZAK ON VIZMAN.ZAK (ID);



CREATE TABLE VIZMAN.KONT (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	CKONT VARCHAR(16),
	TYP VARCHAR(5),
	ARCH BOOLEAN,
	INVESTOR VARCHAR(127),
	OBJEDNATEL VARCHAR(127),
	MENA VARCHAR(5),
	TEXT VARCHAR(127),
	DOCDIR VARCHAR(127),
	TMP VARCHAR(8),	-- Mozna uplne vypustit?	
	DATE_CREATE DATE,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	CONSTRAINT PK_KONT PRIMARY KEY (ID)
);


CREATE INDEX IDX_KONT_OBJEDNATEL ON VIZMAN.KONT (OBJEDNATEL);
CREATE UNIQUE INDEX IDXQ_KONT_CKONT ON VIZMAN.KONT (CKONT);

COMMIT;


DROP SEQUENCE VIZMAN.KONT_SEQ;

CREATE SEQUENCE IF NOT EXISTS VIZMAN.KONT_SEQ
  START WITH 1
  INCREMENT BY 1
  CACHE 1
;

-- POZOR, nejdriv zadat ROKMESZAD pro Re�ii v Zavinu !!

INSERT INTO VIZMAN.KONT
	(ID, VERSION, CKONT, TYP, MENA,
	TEXT,
--	OBJEDNATEL, DATE_CREATE, DATETIME_UPDATE, ARCH)
	OBJEDNATEL, DATE_CREATE, ARCH)
	SELECT
		VIZMAN.KONT_SEQ.NEXTVAL, 1, CISLO_ZAKAZKY, 'KONT', 'CZK',
		SUBSTR(
			(CASE WHEN CHARINDEX(CHAR(13), text) = CHARINDEX(CHAR(10), text) - 1 
				THEN SUBSTR(text, 0, CHARINDEX(CHAR(13), text) - 1)
				ELSE text
			END), 0, 127
		),
--		FIRMA, TO_DATE(CONCAT(ROKMESZAD, '-01'), 'YYYY-MM-DD'), DATUMIMP, FALSE FROM ZAVIN.ZAK_
		FIRMA, TO_DATE(CONCAT(ROKMESZAD, '-01'), 'YYYY-MM-DD'), FALSE FROM ZAVIN.ZAK_
	WHERE LENGTH(CISLO_ZAKAZKY) > 5 OR CISLO_ZAKAZKY = '00001'
;

COMMIT;


--CREATE TABLE VIZMAN.PODZAK (
--	ID BIGINT DEFAULT NOT NULL AUTO_INCREMENT,
--	ID_ZAK BIGINT DEFAULT NOT NULL,
--	CZAK VARCHAR(30) NOT NULL,
--	ID_ZAKAZKY INT DEFAULT NOT NULL,
--	TYP_DOKLADU VARCHAR(2),
--	ROKZAK SMALLINT DEFAULT 0,
--	ROKMESZAD VARCHAR(7),
--	TEXT VARCHAR(255),
--	X BOOLEAN,
--	HONORAR DECIMAL(100,4) DEFAULT 0,
--	ROZPRAC DECIMAL(100,4) DEFAULT 0,
--	TMP VARCHAR(1),
--	ARCH BOOLEAN DEFAULT FALSE,
--	R_ZAL INTEGER DEFAULT 0,
--	R1 DECIMAL(5,1) DEFAULT 0,
--	R2 DECIMAL(5,1) DEFAULT 0,
--	R3 DECIMAL(5,1) DEFAULT 0,
--	R4 DECIMAL(5,1) DEFAULT 0,
--	SKUPINA VARCHAR(1),
--	RM DECIMAL(5,1) DEFAULT 0,
--);
--CREATE UNIQUE INDEX PRIMARY_KEY_ZAK ON VIZMAN.ZAK (ID);


-- TRUNCATE TABLE VIZMAN.PODZAK;
-- DROP TABLE VIZMAN.ZAK;

CREATE TABLE VIZMAN.ZAK (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	CKONT VARCHAR(16),
	CZAK INTEGER,
	TYP VARCHAR(5),
	ARCH BOOLEAN,
	HONORAR DECIMAL(19,2),
	R1 DECIMAL(19,2),
	R2 DECIMAL(19,2),
	R3 DECIMAL(19,2),
	R4 DECIMAL(19,2),
	R_ZAL INTEGER,
	RM DECIMAL(19,2),
	ROKMESZAD VARCHAR(8),
	ROKZAK SMALLINT,
	ROZPRAC DECIMAL(19,2),
	SKUPINA VARCHAR(3),
	TEXT VARCHAR(127),
	DOCDIR VARCHAR(127),
	TMP VARCHAR(8),	-- Mozna uplne vypustit?
--	TYP_DOKLADU VARCHAR(5),
	X BOOLEAN,
	ID_KONT BIGINT,
	DATE_CREATE DATE,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	CONSTRAINT PK_ZAK PRIMARY KEY (ID),
	CONSTRAINT FK_ZAK_KONT FOREIGN KEY (ID_KONT) REFERENCES VIZMAN.KONT(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);
-- CREATE INDEX IDX_FK_PODZAK_ZAK ON VIZMAN.PODZAK (ID_ZAK);

COMMIT;

DROP SEQUENCE VIZMAN.ZAK_SEQ;

CREATE SEQUENCE IF NOT EXISTS VIZMAN.ZAK_SEQ
  START WITH 1
  INCREMENT BY 1
  CACHE 1
;


INSERT INTO VIZMAN.ZAK (ID, VERSION, CKONT, CZAK, TYP,
                        ROKZAK, ROKMESZAD,
                        TEXT,
                        HONORAR, ROZPRAC, ARCH,
                        R_ZAL, R1, R2, R3, R4, SKUPINA, RM,
--                        DATE_CREATE, DATETIME_UPDATE)
                        DATE_CREATE)
                        SELECT VIZMAN.ZAK_SEQ.NEXTVAL, 1, CISLO_ZAKAZKY, 1, 'ZAK',
						ROKZAK, ROKMESZAD,
						SUBSTR (
							(CASE WHEN CHARINDEX(CHAR(13), text) = CHARINDEX(CHAR(10), text) - 1 
								THEN SUBSTR(text, CHARINDEX(CHAR(10), text) + 1)
								ELSE ''
							END), 0, 127
							),
						HONORAR, ROZPRAC, ARCH,
						R_ZAL, R1, R2, R3, R4, SKUPINA, RM,
--						TO_DATE(CONCAT(ROKMESZAD, '-01'), 'YYYY-MM-DD'), DATUMIMP
						TO_DATE(CONCAT(ROKMESZAD, '-01'), 'YYYY-MM-DD')
	FROM ZAVIN.ZAK_
	WHERE LENGTH(CISLO_ZAKAZKY) > 5 OR CISLO_ZAKAZKY = '00001'
;

COMMIT;


UPDATE VIZMAN.ZAK
	SET TEXT = REPLACE(TEXT, CONCAT (CHAR(13), CHAR(10)), ' ') 
	WHERE LENGTH(TEXT) > 0
;				


-- Kontrola: 

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT FROM vizman.kont
ORDER BY LEN DESC
;

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT FROM vizman.kont
WHERE CHARINDEX(CHAR(13), text) > 0
ORDER BY LEN DESC
;

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT FROM vizman.kont
WHERE CHARINDEX(CHAR(10), text) > 0
ORDER BY LEN DESC
;


SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT FROM vizman.zak
ORDER BY LEN DESC
;

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT FROM vizman.zak
WHERE CHARINDEX(CHAR(13), text) > 0
ORDER BY LEN DESC
;

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT FROM vizman.zak
WHERE CHARINDEX(CHAR(10), text) > 0
ORDER BY LEN DESC
;




-- POZOR, nastavit rucne TYP rezijni zakazky na REZ !!! 

SELECT count (*) FROM ZAVIN.ZAK_;
SELECT count (*) FROM VIZMAN.KONT;
SELECT count (*) FROM VIZMAN.ZAK;

UPDATE VIZMAN.ZAK AS zak
	SET zak.ID_KONT = (SELECT top 1 kont.ID FROM VIZMAN.KONT kont
		WHERE zak.CKONT = kont.CKONT)
	WHERE zak.CKONT IN (SELECT kont.CKONT from VIZMAN.KONT kont where kont.CKONT = kont.CKONT)
;


CREATE UNIQUE INDEX IDXQ_ZAK ON VIZMAN.ZAK (CKONT, CZAK);


INSERT INTO VIZMAN.ZAK
	(ID, VERSION, CKONT, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROKZAK, ROZPRAC, SKUPINA, TEXT, TMP, X, ID_KONT)
VALUES(1148, 1, '58016.1-1', 2, 'ZAK', false, 200000.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'Ausf�hrungsplanung Fussg�ngerbr�cke II', NULL, NULL, 1146);

INSERT INTO VIZMAN.ZAK
	(ID, VERSION, CKONT, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROKZAK, ROZPRAC, SKUPINA, TEXT, TMP, X, ID_KONT)
VALUES(1149, 1, '58016.1-1', 3, 'AKV', false, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'Ausf�hrungsplanung Fussg�ngerbr�cke II', NULL, NULL, 1146);

INSERT INTO VIZMAN.ZAK
	(ID, VERSION, CKONT, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROKZAK, ROZPRAC, SKUPINA, TEXT, TMP, X, ID_KONT)
VALUES(1150, 1, '58016.1-1', 4, 'ZAK', false, 200000.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'Ausf�hrungsplanung Fussg�ngerbr�cke III', NULL, NULL, 1146);

INSERT INTO VIZMAN.ZAK
	(ID, VERSION, CKONT, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROKZAK, ROZPRAC, SKUPINA, TEXT, TMP, X, ID_KONT)
VALUES(1151, 1, '58016.1-1', 101, 'SUB', false, -30000.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'R�sov�n�', NULL, NULL, 1146);



COMMIT;

--update tlegacy lca set 
--  lca.pr_dato = (select ca.calc_holdings_date ... from tca ca where ...)
--  where lca.id in (select ca.id from tca where ...)


-- ALTER TABLE VIZMAN.ZAK
--	ADD FOREIGN KEY (ID_KONT) REFERENCES VIZMAN.KONT(ID);

COMMIT;


-- ----------------------------------------------------------

-- DROP TABLE VIZMAN.KONTDOC;

CREATE TABLE VIZMAN.KONTDOC (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	FILENAME VARCHAR(255) NOT NULL,
	NOTE VARCHAR(255),
	DATE_CREATE DATE,
 	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,	
	ID_KONT BIGINT,
	CONSTRAINT PK_KONTDOC PRIMARY KEY (ID),
	CONSTRAINT FK_KONDOC_KONT FOREIGN KEY (ID_KONT) REFERENCES VIZMAN.KONT(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE INDEX IDX_KONTDOC ON VIZMAN.KONTDOC (FILENAME);

COMMIT;


-- DROP TABLE VIZMAN.ZAKDOC;

CREATE TABLE VIZMAN.ZAKDOC (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	FILENAME VARCHAR(255) NOT NULL,
	NOTE VARCHAR(255),
	DATE_CREATE DATE,
 	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,	
	ID_ZAK BIGINT,
	CONSTRAINT PK_ZAKDOC PRIMARY KEY (ID),
	CONSTRAINT FK_ZAKDOC_ZAK FOREIGN KEY (ID_ZAK) REFERENCES VIZMAN.ZAK(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE INDEX IDX_ZAKDOC ON VIZMAN.ZAKDOC (FILENAME);

COMMIT;


-- DROP TABLE VIZMAN.FAKT;

CREATE TABLE VIZMAN.FAKT (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	CFAKT INTEGER NOT NULL
			COMMENT 'Cislo fakturace unikatni pouze pro nadrazenou zakazku)',	
	PLNENI DECIMAL(4,1)
			COMMENT 'Fakturovane castecne plneni vyjadrene v procentech z pole ZAKLAD (nelze po vytvoreni zanzmau menit)',	
	ZAKLAD DECIMAL(19,2)
			COMMENT 'Honorar zakazky v okamziku vytvoreni fakturacniho zaznamu (nelze po vytvoreni zanzmau menit)',	
	CASTKA DECIMAL(19,2) AS PLNENI * ZAKLAD / 100
			COMMENT 'Fakturovana castka vypocitana z poli PLNENI a ZAKLAD, zaokrouhlena na 2 destinna mista',	
	TEXT VARCHAR(128),
	DATE_DUZP DATE
			COMMENT 'Datum zdanitelneho plneni',	
	DATE_VYSTAV DATE
			COMMENT 'Datum vystaven� fakturace',	
	DATETIME_EXPORT DATETIME
			COMMENT 'Datum a cas posledniho uspesneho exportu',	
	DATE_CREATE DATE NOT NULL,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	ID_ZAK BIGINT,
	CONSTRAINT PK_FAKT PRIMARY KEY (ID),
	CONSTRAINT FK_FAKT_ZAK FOREIGN KEY (ID_ZAK) REFERENCES VIZMAN.ZAK(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

COMMIT;

INSERT INTO VIZMAN.FAKT
	(ID, VERSION, CFAKT, PLNENI, ZAKLAD, CASTKA, TEXT, DATE_DUZP, DATE_CREATE, DATETIME_UPDATE, ID_ZAK)
VALUES(100001, 1, 1, 31.0, 13333.00, 4133.23, 'Plneni I.', '2019-01-20', '2018-12-29', '2018-12-29', 1148);

INSERT INTO VIZMAN.FAKT
	(ID, VERSION, CFAKT, PLNENI, ZAKLAD, CASTKA, TEXT, DATE_DUZP, DATE_CREATE, DATETIME_UPDATE, ID_ZAK)
VALUES(100002, 1, 2, 79.0, 13333.00, 10533.07, 'Plneni II.', '2019-01-21', '2018-12-28', '2018-12-29', 1148);

COMMIT;
