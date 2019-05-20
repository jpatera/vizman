

INSERT INTO VIZMAN.CIN
(ID, VERSION, PORADI, AKCE_TYP, AKCE, CIN_KOD, CINNOST, CALCPRAC, TMP)
VALUES(14, 1, 35, 'F', 'Slu�ebka (cel� den)', 'SC', 'Slu�ebka', true, NULL)
;

COMMIT;


-- CREATE OR REPLACE VIEW VIZMAN.ZAK_BASIC_VIEW AS
--	SELECT zak.ID, zak.TYP, kont.CKONT, zak.CZAK, zak.ROK, zak.SKUPINA, kont.TEXT AS TEXT_KONT, zak.TEXT AS TEXT_ZAK, kont.OBJEDNATEL, zak.ARCH, zak.ID_KONT
--	FROM VIZMAN.ZAK zak
--	LEFT JOIN VIZMAN.KONT kont ON zak.ID_KONT = kont.ID
--;


UPDATE vizman.CIN
	SET AKCE_TYP = 'F'
	WHERE CIN_KOD = 'dc'
;

COMMIT;


CREATE OR REPLACE VIEW VIZMAN.ZAK_BASIC_VIEW AS
	SELECT zak.ID, zak.TYP, kont.CKONT, zak.CZAK, zak.ROK, zak.SKUPINA, kont.TEXT AS TEXT_KONT, zak.TEXT AS TEXT_ZAK, klient.NAME AS OBJEDNATEL, zak.ARCH, zak.ID_KONT
	FROM VIZMAN.ZAK zak
	LEFT JOIN VIZMAN.KONT kont ON zak.ID_KONT = kont.ID
	LEFT JOIN VIZMAN.KLIENT klient ON kont.ID_KLIENT = klient.ID
;


ALTER TABLE VIZMAN.FAKT
	ADD COLUMN FAKT_CISLO Varchar(40)
;

ALTER TABLE VIZMAN.FAKT
	MODIFY COLUMN FAKT_CISLO Varchar(40)
;


ALTER TABLE VIZMAN.ZAK
	ADD COLUMN POZNAMKA Varchar(127)
;

SELECT username, DOCH_DATE
FROM VIZMAN.DOCH
WHERE DOCH_DATE >= '2019-01-01'
AND CIN_CIN_KOD = 'L'
;
