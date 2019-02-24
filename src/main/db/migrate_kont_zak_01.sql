
-- !!!!  Do BEFORE tables are created:
SET COLLATION CZECH STRENGTH SECONDARY;



DROP SEQUENCE VIZMAN.VIZMAN_GLOBAL_SEQ IF EXISTS;

CREATE SEQUENCE IF NOT EXISTS VIZMAN.VIZMAN_GLOBAL_SEQ
  START WITH 1000001
  INCREMENT BY 10
  CACHE 50
;

COMMIT;

-- =============================================

DROP TABLE VIZMAN.CFGPROP IF EXISTS;

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


DROP TABLE VIZMAN.CIN IF EXISTS;

CREATE TABLE VIZMAN.CIN (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	PORADI SMALLINT NOT NULL,
	AKCE_TYP VARCHAR(2),
	AKCE VARCHAR(32),
	CIN_KOD VARCHAR(2),
	CINNOST VARCHAR(24),
	CALCPRAC BOOLEAN,
	TMP VARCHAR(1),
	CONSTRAINT PK_CIN PRIMARY KEY (ID)
);

CREATE UNIQUE INDEX VIZMAN.IDXQ_CIN_PORADI ON VIZMAN."CIN" (PORADI)
;


DROP TABLE VIZMAN.PERSON IF EXISTS;

CREATE TABLE VIZMAN.PERSON (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	STATE VARCHAR(16),
	USERNAME VARCHAR(32),
	PASSWORD VARCHAR(32),
	JMENO VARCHAR(64),
	PRIJMENI VARCHAR(64),
	NASTUP DATE,
	VYSTUP DATE,
	SAZBA DECIMAL(19,2),
	CONSTRAINT PK_PERSON PRIMARY KEY (ID)
);



DROP TABLE VIZMAN.ROLE IF EXISTS;

CREATE TABLE VIZMAN."ROLE" (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	DESCRIPTION VARCHAR(255),
	NAME VARCHAR(255),
	CONSTRAINT PK_ROLE PRIMARY KEY (ID)
);

CREATE UNIQUE INDEX VIZMAN.IDXQ_ROLE_NAME ON VIZMAN."ROLE" (NAME)
;


DROP TABLE VIZMAN.PERSON_ROLE IF EXISTS;

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


DROP TABLE VIZMAN.ROLE_PERM IF EXISTS;

CREATE TABLE VIZMAN.ROLE_PERM (
	ROLE_ID BIGINT NOT NULL,
	PERM VARCHAR(255),
	CONSTRAINT FK_ROLE_PERM_ROLE FOREIGN KEY (ROLE_ID) REFERENCES VIZMAN."ROLE"(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE INDEX VIZMAN.IDX_ROLE_PERM_ROLE_ID ON VIZMAN.ROLE_PERM (ROLE_ID)
;


--  =============================================



INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(1, 1, 'app.locale', 'cs_CZ', 110, 'N�rodn� t��d�n� a form�ty', 'N�rodn� t��d�n� a form�ty', true)
;


INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(2, 1, 'app.document.root.local', 'L:\VizMan', 130, 'Ko�enov� adres�� pro dokumeny (stanice)', 'Ko�enov� adres�� pro dokumeny z pohledu pracovn�ch stanic', false)
;

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(3, 1, 'app.document.root.server', '\\pc-server7\VizMan', 130, 'Ko�enov� adres�� pro dokumeny (server)', 'Ko�enov� adres�� pro dokumeny z pohledu serveru', false)
;


INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(4, 1, 'app.project.root.local', 'S:\PROJEKT', 120, 'Ko�enov� adres�� pro projekty (stanice)', 'Ko�enov� adres�� pro projekty z pohledu pracovn�ch stanic', false)
;

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(5, 1, 'app.project.root.server', '\\pc-server7\PROJEKT', 120, 'Ko�enov� adres�� pro projekty (server)', 'Ko�enov� adres�� pro projekty z pohledu serveru', false)
;


INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(6, 1, 'app.koef.pojist', '0.35', 140, 'Koeficient poji�t�n�', 'Pou��v� se p�i v�po�tech vyhodnocovac�ch tabulek', false)
;

INSERT INTO VIZMAN.CFGPROP (ID, VERSION, NAME, VALUE, ORD, LABEL, DESCRIPTION, RO)
VALUES(7, 1, 'app.koef.rezie', '0.8', 150, 'Koeficient re�ie', 'Pou��v� se p�i v�po�tech vyhodnocovac�ch tabulek', false)
;


COMMIT;

-- ---------------------------------------------


INSERT INTO VIZMAN.CIN
(ID, VERSION, PORADI, AKCE_TYP, CIN_KOD, AKCE, CINNOST, CALCPRAC, TMP)
SELECT CIN_ID, 1, PORADI, CIN_T1, CIN_T2, AKCE, CINNOST, CALCPRAC, TMP 
FROM ZAVIN.CIN_;

COMMIT;

UPDATE VIZMAN.CIN
	SET CIN_KOD = 'KP'
	WHERE PORADI = 45
;

COMMIT;

UPDATE VIZMAN.CIN
	SET CIN_KOD = 'XD'
	WHERE PORADI = 50
;

COMMIT;

UPDATE VIZMAN.CIN
	SET AKCE_TYP = 'A'
	WHERE PORADI = 100
;

COMMIT;

UPDATE VIZMAN.CIN
	SET CIN_KOD = 'dc'
	WHERE PORADI = 80
;

UPDATE VIZMAN.CIN
	SET CIN_KOD = 'dp'
	WHERE PORADI = 90
;

COMMIT;

UPDATE VIZMAN.CIN
	SET AKCE_TYP = 'F'
	WHERE CIN_KOD = 'do' OR CIN_KOD = 'dp' OR CIN_KOD = 'ne' OR CIN_KOD = 'nv'  
;

COMMIT;

UPDATE VIZMAN.CIN
	SET CINNOST = 'Ob�d oprav. (zru�eno)'
	WHERE PORADI = 105
;

COMMIT;

CREATE UNIQUE INDEX VIZMAN.IDXQ_CIN_CINKOD ON VIZMAN."CIN" (CIN_KOD)
;

COMMIT;

-- ---------------------------------------------


INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATE, JMENO, PRIJMENI, PASSWORD, USERNAME, NASTUP, VYSTUP, SAZBA)
VALUES(1001, 0, 'HIDDEN', 'Admin', 'Systemak', 'admin', 'admin', NULL, NULL, 0);

INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATE, JMENO, PRIJMENI, PASSWORD, USERNAME, NASTUP, VYSTUP, SAZBA)
VALUES(1002, 0, 'HIDDEN', 'User', 'B�n� user', 'user', 'user', NULL, NULL, 0);

INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATE, USERNAME, PASSWORD, JMENO, PRIJMENI, NASTUP, VYSTUP, SAZBA)
VALUES(1003, 0, 'HIDDEN', 'manag', 'manag', 'Manager', 'Zku�en�', NULL, NULL, 0);

INSERT INTO VIZMAN.PERSON
(ID, VERSION, STATE, JMENO, PRIJMENI, PASSWORD, USERNAME, NASTUP, VYSTUP, SAZBA)
SELECT USER_ID, 1,
CASE
    WHEN USER_NAME = 'Studentova' THEN 'ACTIVE'
    WHEN USER_LEVEL = 1 THEN 'DISABLED'
    ELSE 'ACTIVE'
END,
USER_FIRST, USER_LAST, USER_PWD, USER_LOGIN, USER_NASTUP, USER_VYSTUP, USER_SAZBA 
FROM ZAVIN.USR_;

COMMIT;


UPDATE VIZMAN.PERSON p
SET STATUS = 
(SELECT 
  CASE
    WHEN u.USER_LOGIN = 'Studentova' THEN 'ACTIVE'
    WHEN u.USER_LEVEL = 1 THEN 'DISABLED'
    ELSE 'ACTIVE'
  END
  FROM ZAVIN.USR_ u
  WHERE u.USER_LOGIN = p.USERNAME
)  
;
 

-- ---------------------------------------------


INSERT INTO VIZMANDB.VIZMAN."ROLE" (ID, VERSION, DESCRIPTION, NAME)
VALUES(10001, 1, 'Administr�tor - v�echna existuj�c� opr�vn�n�', 'ROLE_ADMIN');

INSERT INTO VIZMANDB.VIZMAN."ROLE" (ID, VERSION, DESCRIPTION, NAME)
VALUES(10002, 1, 'U�ivatel - b�n� opr�vn�n�', 'ROLE_USER');

INSERT INTO VIZMANDB.VIZMAN."ROLE" (ID, VERSION, DESCRIPTION, NAME)
VALUES(10003, 1, 'Manager - jako ROLE_USER plus fakturace a honor��e', 'ROLE_MANAGER');

COMMIT;


INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10001, 'MODIFY_ALL');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10001, 'VIEW_ALL');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10002, 'ZAK_BASIC_READ');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10002, 'DOCH_USE');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10003, 'ZAK_BASIC_MODIFY');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10003, 'ZAK_EXT_MODIFY');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10003, 'ZAK_EXT_READ');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10003, 'ZAK_BASIC_READ');
INSERT INTO VIZMAN.ROLE_PERM
(ROLE_ID, PERM)
VALUES(10003, 'DOCH_USE');

COMMIT;

INSERT INTO VIZMAN.PERSON_ROLE
(PERSON_ID, ROLE_ID)
VALUES(1001, 10001);
INSERT INTO VIZMAN.PERSON_ROLE
(PERSON_ID, ROLE_ID)
VALUES(1002, 10002);
INSERT INTO VIZMAN.PERSON_ROLE
(PERSON_ID, ROLE_ID)
VALUES(1003, 10003);

COMMIT;

--  =============================================
--    CREATE TABLES - KONT, ZAK, FAKT...
--  =============================================


-- TRUNCATE TABLE vizman.kont;

DROP TABLE VIZMAN.KONTDOC IF EXISTS;
DROP TABLE VIZMAN.ZAKDOC IF EXISTS;
DROP TABLE VIZMAN.FAKT IF EXISTS;
DROP TABLE VIZMAN.ZAK IF EXISTS;
DROP TABLE VIZMAN.KONT IF EXISTS;
DROP TABLE VIZMAN.KLIENT IF EXISTS;

DROP SEQUENCE VIZMAN.KONT_SEQ IF EXISTS;
DROP SEQUENCE VIZMAN.ZAK_SEQ IF EXISTS;


DROP SEQUENCE VIZMAN.KONT_SEQ_IMP IF EXISTS;

CREATE SEQUENCE VIZMAN.KONT_SEQ_IMP
  START WITH 10001
  INCREMENT BY 1
  CACHE 50
;


DROP SEQUENCE VIZMAN.ZAK_SEQ_IMP IF EXISTS;

CREATE SEQUENCE VIZMAN.ZAK_SEQ_IMP
  START WITH 20001
  INCREMENT BY 1
  CACHE 50
;

COMMIT;


DROP SEQUENCE VIZMAN.KLIENT_SEQ_IMP IF EXISTS;

CREATE SEQUENCE VIZMAN.KLIENT_SEQ_IMP
  START WITH 30001
  INCREMENT BY 1
  CACHE 50
;

COMMIT;


DROP TABLE VIZMAN.KLIENT IF EXISTS;

CREATE TABLE VIZMAN.KLIENT (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	TYP VARCHAR(5) NOT NULL,
	NAME VARCHAR(127),
	NOTE VARCHAR(127),
	ID_ZAK BIGINT,
	DATE_CREATE DATE,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	CONSTRAINT PK_KLIENT PRIMARY KEY (ID)
);



DROP TABLE VIZMAN.KONT IF EXISTS;

CREATE TABLE VIZMAN.KONT (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	UUID UUID NOT NULL
				COMMENT 'Inicialni duvod pro UUID je potreba unikatniho identifikatoru nezavisleho na autoamtickem DB ID, potrebneho pro tree grid',
	TYP VARCHAR(5) NOT NULL,
	CKONT VARCHAR(16),
	ROK SMALLINT,
	ARCH BOOLEAN,
	INVESTOR VARCHAR(127),
	OBJEDNATEL VARCHAR(127),
	MENA VARCHAR(5) NOT NULL,
	TEXT VARCHAR(127),
	FOLDER VARCHAR(127),
	ID_KLIENT BIGINT,	
	TMP VARCHAR(8),	-- Mozna uplne vypustit?	
	DATE_CREATE DATE,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	CONSTRAINT PK_KONT PRIMARY KEY (ID)
);


CREATE INDEX VIZMAN.IDX_KONT_OBJEDNATEL ON VIZMAN.KONT (OBJEDNATEL)
;
CREATE UNIQUE INDEX VIZMAN.IDXQ_KONT_CKONT ON VIZMAN.KONT (CKONT)
;

COMMIT;


-- DROP SEQUENCE VIZMAN.KONT_SEQ;

-- CREATE SEQUENCE IF NOT EXISTS VIZMAN.KONT_SEQ
--   START WITH 1
--   INCREMENT BY 1
--   CACHE 1
--;


DROP TABLE VIZMAN.ZAK IF EXISTS;

CREATE TABLE VIZMAN.ZAK (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	UUID UUID NOT NULL
				COMMENT 'Inicialni duvod je potreba unikatniho identifikatoru pro potreby tree grid, nezavisleho na DB ID ',
	TYP VARCHAR(5) NOT NULL,
	CKONT_ORIG VARCHAR(16)
				COMMENT 'Cislo kontraktu - jen pro uvodni import za Zavinu, jinak null',
	CZAK INTEGER,
	ROK SMALLINT,
	ARCH BOOLEAN,
	SKUPINA VARCHAR(3),
	TEXT VARCHAR(127),
	FOLDER VARCHAR(127),
	HONORAR DECIMAL(19,2) NOT NULL DEFAULT 0,
	ID_KONT BIGINT,
	R1 DECIMAL(19,2),
	R2 DECIMAL(19,2),
	R3 DECIMAL(19,2),
	R4 DECIMAL(19,2),
	ROZPRAC DECIMAL(19,2),
	R_ZAL INTEGER,
	RM DECIMAL(19,2),
	TMP VARCHAR(8),	-- Mozna uplne vypustit?
--	TYP_DOKLADU VARCHAR(5),
--	X BOOLEAN,
	ROKMESZAD VARCHAR(8),
	DATE_CREATE DATE,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	CONSTRAINT PK_ZAK PRIMARY KEY (ID),
	CONSTRAINT FK_ZAK_KONT FOREIGN KEY (ID_KONT) REFERENCES VIZMAN.KONT(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- CREATE INDEX IDX_FK_PODZAK_ZAK ON VIZMAN.PODZAK (ID_ZAK);
CREATE UNIQUE INDEX VIZMAN.IDXQ_ZAK_IDKONT_CZAK ON VIZMAN.ZAK (ID_KONT, CZAK)
;

COMMIT;

-- DROP SEQUENCE VIZMAN.ZAK_SEQ;

-- CREATE SEQUENCE IF NOT EXISTS VIZMAN.ZAK_SEQ
--   START WITH 1
--   INCREMENT BY 1
--   CACHE 1
-- ;


DROP TABLE VIZMAN.KONTDOC IF EXISTS;

CREATE TABLE VIZMAN.KONTDOC (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	FILENAME VARCHAR(255) NOT NULL,
	NOTE VARCHAR(255),
	DATE_CREATE DATE,
 	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,	
	ID_KONT BIGINT NOT NULL,
	CONSTRAINT PK_KONTDOC PRIMARY KEY (ID),
	CONSTRAINT FK_KONDOC_KONT FOREIGN KEY (ID_KONT) REFERENCES VIZMAN.KONT(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE INDEX VIZMAN.IDX_KONTDOC ON VIZMAN.KONTDOC (FILENAME)
;

COMMIT;


DROP TABLE VIZMAN.ZAKDOC IF EXISTS;

CREATE TABLE VIZMAN.ZAKDOC (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	FILENAME VARCHAR(255) NOT NULL,
	NOTE VARCHAR(255),
	DATE_CREATE DATE,
 	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,	
	ID_ZAK BIGINT NOT NULL,
	CONSTRAINT PK_ZAKDOC PRIMARY KEY (ID),
	CONSTRAINT FK_ZAKDOC_ZAK FOREIGN KEY (ID_ZAK) REFERENCES VIZMAN.ZAK(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE INDEX VIZMAN.IDX_ZAKDOC ON VIZMAN.ZAKDOC (FILENAME)
;

COMMIT;


DROP TABLE VIZMAN.FAKT IF EXISTS;

CREATE TABLE VIZMAN.FAKT (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	TYP VARCHAR(5) NOT NULL DEFAULT 'FAKT',
	CFAKT INTEGER NOT NULL
			COMMENT 'Cislo fakturace unikatni pouze pro nadrazenou zakazku)',	
	PLNENI DECIMAL(4,1)
			COMMENT 'Fakturovane castecne plneni vyjadrene v procentech z pole ZAKLAD (nelze po vytvoreni zaznamu menit)',	
	DATE_DUZP DATE
			COMMENT 'Datum zdanitelneho plneni',	
	TEXT VARCHAR(128),
	ZAKLAD DECIMAL(19,2)
			COMMENT 'Honorar zakazky v okamziku vytvoreni fakturacniho zaznamu (nelze po vytvoreni zanzmau menit)',	
--	CASTKA DECIMAL(19,2) AS PLNENI * ZAKLAD / 100
	CASTKA DECIMAL(19,2)
	COMMENT 'Fakturovana castka vypocitana z poli PLNENI a ZAKLAD, zaokrouhlena na 2 destinna mista',	
	DATE_VYSTAV DATE
			COMMENT 'Datum vystaven� fakturace',	
	DATETIME_EXPORT DATETIME
			COMMENT 'Datum a cas posledniho uspesneho exportu',	
	DATE_CREATE DATE NOT NULL,
	DATETIME_UPDATE DATETIME AS NOW() NOT NULL,
	ID_ZAK BIGINT NOT NULL,
	CONSTRAINT PK_FAKT PRIMARY KEY (ID),
	CONSTRAINT FK_FAKT_ZAK FOREIGN KEY (ID_ZAK) REFERENCES VIZMAN.ZAK(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX VIZMAN.IDXQ_FAKT_IDZAK_CFAKT ON VIZMAN.FAKT (ID_ZAK, CFAKT)
;

COMMIT;



--  =============================================
--    MIGRATE DATA - KONT, ZAK, KLIENT
--  =============================================


-- !! POZOR, nejdriv zadat ROKMESZAD pro Re�ii v Zavinu !!


INSERT INTO VIZMAN.KLIENT
	(ID, VERSION, TYP, NAME, DATE_CREATE)
	SELECT DISTINCT
		VIZMAN.KLIENT_SEQ_IMP.NEXTVAL, 1, 'KLI', zak.FIRMA, NOW()
		FROM (SELECT DISTINCT firma FROM ZAVIN.ZAK_
				WHERE LENGTH(CISLO_ZAKAZKY) > 5 AND firma IS not NULL
				ORDER BY firma
			 ) zak
--		TO_DATE(CONCAT(ROKMESZAD, '-01'), 'YYYY-MM-DD'), FALSE FROM ZAVIN.ZAK_
--	WHERE LENGTH(CISLO_ZAKAZKY) > 5 AND cislo_zakazky IS not NULL
;

COMMIT;

CREATE UNIQUE INDEX VIZMAN.IDXQ_KLIENT_NAME ON VIZMAN.KLIENT (NAME)
;

COMMIT;



SELECT CISLO_ZAKAZKY, LENGTH(TEXT) AS LEN, TEXT
	FROM zavin.zak_
	ORDER BY LEN DESC
;

INSERT INTO VIZMAN.KONT
	(ID, VERSION, UUID, CKONT, ROK, TYP, MENA,
	TEXT,
--	OBJEDNATEL, DATE_CREATE, DATETIME_UPDATE, ARCH)
	OBJEDNATEL, DATE_CREATE, ARCH)
	SELECT
		VIZMAN.KONT_SEQ_IMP.NEXTVAL, 1, RANDOM_UUID(), CISLO_ZAKAZKY, ROKZAK, 'KONT', 'CZK',
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


UPDATE VIZMAN.KONT ko
	SET ID_KLIENT = (SELECT ID FROM VIZMAN.KLIENT kl WHERE ko.OBJEDNATEL = kl.NAME)
;

COMMIT;


INSERT INTO VIZMAN.ZAK (ID, VERSION, UUID, CKONT_ORIG, CZAK, TYP,
                        ROK, ROKMESZAD,
                        TEXT,
                        HONORAR, ROZPRAC, ARCH,
                        R_ZAL, R1, R2, R3, R4, SKUPINA, RM,
--                        DATE_CREATE, DATETIME_UPDATE)
                        DATE_CREATE)
                        SELECT VIZMAN.ZAK_SEQ_IMP.NEXTVAL, 1, RANDOM_UUID(), CISLO_ZAKAZKY, 1, 'ZAK',
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

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT
	FROM vizman.kont
	ORDER BY LEN DESC
;

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT
	FROM vizman.kont
	WHERE CHARINDEX(CHAR(13), text) > 0
	ORDER BY LEN DESC
;

SELECT CKONT, LENGTH(TEXT) AS LEN, TEXT
	FROM vizman.kont
	WHERE CHARINDEX(CHAR(10), text) > 0
	ORDER BY LEN DESC
;


SELECT CKONT_ORIG, LENGTH(TEXT) AS LEN, TEXT
	FROM vizman.zak
	ORDER BY LEN DESC
;

SELECT CKONT_ORIG, LENGTH(TEXT) AS LEN, TEXT
	FROM vizman.zak
	WHERE CHARINDEX(CHAR(13), text) > 0
	ORDER BY LEN DESC
;

SELECT CKONT_ORIG, LENGTH(TEXT) AS LEN, TEXT
	FROM vizman.zak
	WHERE CHARINDEX(CHAR(10), text) > 0
	ORDER BY LEN DESC
;



-- !!!!!!!!!!!!!!!!!!!!!!!!!!!
-- POZOR, nastavit rucne TYP rezijni zakazky na REZ !!! 

SELECT count (*) FROM ZAVIN.ZAK_;
SELECT count (*) FROM VIZMAN.KONT;
SELECT count (*) FROM VIZMAN.ZAK;

UPDATE VIZMAN.ZAK AS zak
	SET zak.ID_KONT = (SELECT top 1 kont.ID FROM VIZMAN.KONT kont
		WHERE zak.CKONT_ORIG = kont.CKONT)
	WHERE zak.CKONT_ORIG IN (SELECT kont.CKONT from VIZMAN.KONT kont where kont.CKONT = kont.CKONT)
;

COMMIT;


-- CREATE UNIQUE INDEX IDXQ_ZAK ON VIZMAN.ZAK (CKONT, CZAK);

-- '58016.1-1'
INSERT INTO VIZMAN.ZAK
	(ID, VERSION, UUID, CKONT_ORIG, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROK, ROZPRAC, SKUPINA, TEXT, TMP, ID_KONT)
VALUES(21307, 1, RANDOM_UUID(), NULL, 2, 'ZAK', false, 200000.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'Ausf�hrungsplanung Fussg�ngerbr�cke II', NULL, 11305)
;

INSERT INTO VIZMAN.ZAK
	(ID, VERSION, UUID, CKONT_ORIG, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROK, ROZPRAC, SKUPINA, TEXT, TMP, ID_KONT)
VALUES(21308, 1, RANDOM_UUID(), NULL, 3, 'AKV', false, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'Ausf�hrungsplanung Fussg�ngerbr�cke II', NULL, 11305)
;

INSERT INTO VIZMAN.ZAK
	(ID, VERSION, UUID, CKONT_ORIG, CZAK, TYP, ARCH, HONORAR, R1, R2, R3, R4, R_ZAL, RM, DATE_CREATE, DATETIME_UPDATE, ROKMESZAD, ROK, ROZPRAC, SKUPINA, TEXT, TMP, ID_KONT)
VALUES(21309, 1, RANDOM_UUID(), NULL, 5, 'ZAK', false, 15000.00, 0.00, 0.00, 0.00, 0.00, 0, 0.00, '2016-06-01', '2016-06-24', '2016-06', 2016, 0.00, '1', 'Ausf�hrungsplanung Fussg�ngerbr�cke III', NULL, 11305)
;


--update tlegacy lca set 
--  lca.pr_dato = (select ca.calc_holdings_date ... from tca ca where ...)
--  where lca.id in (select ca.id from tca where ...)


-- ALTER TABLE VIZMAN.ZAK
--	ADD FOREIGN KEY (ID_KONT) REFERENCES VIZMAN.KONT(ID);

COMMIT;


-- ----------------------------------------------------------


INSERT INTO VIZMAN.FAKT
	(ID, VERSION, TYP, CFAKT, PLNENI, ZAKLAD, CASTKA, TEXT, DATE_DUZP, DATE_CREATE, DATETIME_UPDATE, ID_ZAK)
VALUES(500001, 1, 'FAKT', 1, 31.0, 200000.00, 62000.00, 'Plneni I.', '2019-01-20', '2018-12-29', '2018-12-29', 21307)
;

INSERT INTO VIZMAN.FAKT
	(ID, VERSION, TYP, CFAKT, PLNENI, ZAKLAD, CASTKA, TEXT, DATE_DUZP, DATE_CREATE, DATETIME_UPDATE, ID_ZAK)
VALUES(500002, 1, 'FAKT', 2, 69.0, 200000.00, 138000.00, 'Plneni II.', '2019-01-21', '2018-12-28', '2018-12-29', 21307)
;

INSERT INTO VIZMAN.FAKT
	(ID, VERSION, TYP, CFAKT, PLNENI, ZAKLAD, CASTKA, TEXT, DATE_DUZP, DATE_CREATE, DATETIME_UPDATE, ID_ZAK)
VALUES(500003, 1, 'SUB', 3, null, NULL,    -30000.00, 'Mont�rung, demont�rung', NULL, '2019-01-20', '22019-01-20', 21307)
;


COMMIT;





--  =============================================
--    CREATE TABLES - DOCH a spol.
--  =============================================


DROP TABLE VIZMAN.DOCH IF EXISTS;

DROP SEQUENCE VIZMAN.DOCH_SEQ IF EXISTS;


CREATE TABLE VIZMAN.DOCH (
	ID BIGINT NOT NULL,
	VERSION INTEGER NOT NULL,
	PERSON_ID BIGINT NOT NULL,
	USER_LOGIN VARCHAR(32),
--	USER_EDIT_AD VARCHAR(20),

--	D_YEAR SMALLINT,
--	D_MONTH SMALLINT,
--	D_DAY SMALLINT,
--	D_ROKMES VARCHAR(7),
	DOCH_DATE DATE,
	DOCH_STATE VARCHAR(2),
--	D_DK VARCHAR(2),
	FROM_TIME TIME,
	FROM_MODIF_DATETIME TIMESTAMP,
	FROM_MANUAL BOOLEAN,
	TO_TIME TIME,
	TO_MODIF_DATETIME TIMESTAMP,
	TO_MANUAL SMALLINT,
	DOCH_DURATION TIME,

	CIN_ID INTEGER,
	CIN_POL SMALLINT,
--	CIN_ST VARCHAR(2),
	CIN_AKCE_TYP VARCHAR(2),
	CIN_CIN_KOD VARCHAR(2),
	CINNOST VARCHAR(24),

	CALCPRAC BOOLEAN,
	POZNAMKA VARCHAR(120),
	TMP VARCHAR(1),
--	M_DATCAS TIMESTAMP,
	CONSTRAINT PK_DOCH PRIMARY KEY (ID)
);

COMMIT;



--  =============================================
--    MIGRATE DATA - DOCH a spol.
--  =============================================

-- (DOCH_ID, USER_LOGIN, USER_EDIT_AD, D_YEAR, D_MONTH, D_DAY, D_ROKMES, D_DATUM, D_DK, Z_DATCASOD, D_CAS_OD, R_OD, Z_DATCASDO, D_CAS_DO, R_DO, D_HODIN, CIN_ID, CIN_POL, CIN_ST, CIN_T1, CIN_T2, CINNOST, CALCPRAC, POZNAMKA, TMP, M_DATCAS)

INSERT INTO VIZMAN.DOCH
  (ID, VERSION, PERSON_ID, USER_LOGIN, DOCH_DATE,
  FROM_MODIF_DATETIME, FROM_TIME, FROM_MANUAL,
  TO_MODIF_DATETIME, TO_TIME, TO_MANUAL,
  DOCH_DURATION,
  DOCH_STATE,
  CIN_ID, CIN_POL,
  CIN_AKCE_TYP, CIN_CIN_KOD, CINNOST, CALCPRAC, POZNAMKA, TMP)
SELECT
  DOCH_ID, 1, 0, USER_LOGIN, D_DATUM,
  Z_DATCASOD, D_CAS_OD,
  CASE
    WHEN R_OD = 1 THEN TRUE
    ELSE FALSE
  END,
  Z_DATCASDO, D_CAS_DO,
  CASE
    WHEN R_DO = 1 THEN TRUE
    ELSE FALSE
  END,
  D_HODIN,
  CASE
    WHEN CIN_ST = 'K' THEN 'K'
    ELSE NULL
  END,
  CIN_ID, CIN_POL,
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


-- Make half day dovolena kod different from unique:
UPDATE VIZMAN.DOCH AS doch
SET doch.CIN_CIN_KOD = 'dp'
	WHERE DOCH.CIN_CIN_KOD = 'do' AND DOCH.DOCH_STATE IS NULL
;

COMMIT;


-- Make half day dovolena kod different from unique:
UPDATE VIZMAN.DOCH AS doch
SET doch.CIN_CIN_KOD = 'dc'
	WHERE DOCH.CIN_CIN_KOD = 'do' AND DOCH.DOCH_STATE IS NULL
;

COMMIT;


UPDATE VIZMAN.DOCH AS doch
SET doch.CIN_AKCE_TYP = 'F'
	WHERE DOCH.CIN_CIN_KOD = 'dc' OR DOCH.CIN_CIN_KOD = 'dp' OR DOCH.CIN_CIN_KOD = 'ne' OR DOCH.CIN_CIN_KOD = 'nv'
;

COMMIT;

UPDATE VIZMAN.DOCH AS doch
SET doch.CIN_AKCE_TYP = 'A'
	WHERE DOCH.CIN_CIN_KOD = 'OA'
;

COMMIT;



-- --------------------------------------------------


CREATE INDEX VIZMAN.IDX_DOCH_PERSONID_DOCHDATE_FROM_TIME ON VIZMAN.DOCH (PERSON_ID, DOCH_DATE, FROM_TIME)
;

COMMIT;

