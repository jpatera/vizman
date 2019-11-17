

INSERT INTO VIZMAN.CIN
(ID, VERSION, PORADI, AKCE_TYP, AKCE, CIN_KOD, CINNOST, CALCPRAC, TMP)
VALUES(14, 1, 35, 'F', 'Služebka (celý den)', 'SC', 'Služebka', true, NULL)
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

ALTER TABLE VIZMAN.KONT
	MODIFY COLUMN ROK INTEGER
;

ALTER TABLE VIZMAN.ZAK
	ADD COLUMN POZNAMKA Varchar(127)
;

SELECT username, DOCH_DATE
FROM VIZMAN.DOCH
WHERE DOCH_DATE >= '2019-01-01'
AND CIN_CIN_KOD = 'L'
;

ALTER TABLE VIZMAN.KONT
	MODIFY COLUMN FOLDER Varchar(140)
;

ALTER TABLE VIZMAN.ZAK
	MODIFY COLUMN FOLDER Varchar(140)
;



ALTER TABLE VIZMAN.KONT
	MODIFY COLUMN FOLDER NOT NULL
;

ALTER TABLE VIZMAN.ZAK
	MODIFY COLUMN FOLDER NOT NULL
;


ALTER TABLE VIZMAN.ZAK
	MODIFY COLUMN ROK INTEGER
;


CREATE OR REPLACE VIEW VIZMAN.ZAK_ROZPRAC_VIEW AS
	SELECT zak.ID, zak.TYP, kont.CKONT, zak.CZAK, zak.ROK, zak.SKUPINA, kont.TEXT AS TEXT_KONT, zak.TEXT AS TEXT_ZAK, klient.NAME AS OBJEDNATEL, zak.ARCH, zak.ID_KONT,
	       zak.ROZPRAC as R0, zak.R1, zak.R2, zak.R3, zak.R4
	FROM VIZMAN.ZAK zak
	LEFT JOIN VIZMAN.KONT kont ON zak.ID_KONT = kont.ID
	LEFT JOIN VIZMAN.KLIENT klient ON kont.ID_KLIENT = klient.ID
;

COMMIT;

------------------------------------------------------------------------

-- Changed:

DROP VIEW VIZMAN.ZAK_ROZPRAC_VIEW IF EXISTS;

CREATE OR REPLACE FORCE VIEW VIZMAN.ZAK_ROZPRAC_VIEW (
	ID, TYP, CKONT, CZAK, ROK, SKUPINA, TEXT_KONT, TEXT_ZAK, MENA, OBJEDNATEL, ARCH, ID_KONT,
	R0, R1, R2, R3, R4,
	HONOR_CISTY, HONOR_FAKT, HONOR_SUB
) AS
SELECT zak.ID,
    zak.TYP,
    KONT.CKONT,
    zak.CZAK,
    zak.ROK,
    zak.SKUPINA,
    KONT.TEXT AS TEXT_KONT,
    zak.TEXT AS TEXT_ZAK,
    KONT.MENA AS MENA,
    KLIENT.NAME AS OBJEDNATEL,
    zak.ARCH,
    zak.ID_KONT,
    zak.ROZPRAC AS R0,
    zak.R1,
    zak.R2,
    zak.R3,
    zak.R4,
	SUM(fakta.CASTKA) AS HONOR_CISTY,
	SUM(faktb.CASTKA) AS HONOR_FAKT,
	SUM(faktc.CASTKA) AS HONOR_SUB	
FROM vizman.ZAK zak
LEFT OUTER JOIN vizman.FAKT fakta
	on fakta.id_zak = zak.id
LEFT OUTER JOIN vizman.FAKT faktb
	on faktb.id = fakta.id AND faktb.typ = 'FAKT'
LEFT OUTER JOIN vizman.FAKT faktc
	on faktc.id = fakta.id AND faktc.typ = 'SUB'
LEFT OUTER JOIN VIZMAN.KONT KONT
    ON zak.ID_KONT = KONT.ID
LEFT OUTER JOIN VIZMAN.KLIENT KLIENT
    ON KONT.ID_KLIENT = KLIENT.ID
GROUP BY zak.id
ORDER BY zak.id desc
;


----------------------------------------------------------------

COMMIT;

DROP VIEW VIZMAN.DOCH_MES_VIEW IF EXISTS;

CREATE OR REPLACE FORCE VIEW VIZMAN.DOCH_MES_VIEW (
	ID,
	PERSON_ID,
	DOCH_WEEK,
	DOCH_DATE,
	FROM_PRACE_START,
	FROM_MANUAL,
	TO_PRACE_END,
	TO_MANUAL,
	DUR_OBED,
	OBED_AUTO,
	DUR_PRACE_CELK,
	DUR_PRACE_WEND,
	DUR_LEK,
	DUR_DOV,
	DUR_NEM,
	DUR_VOLNO,
	DUR_SLUZ
) AS
SELECT
	ROWNUM() AS ID,
	aa.person_id,
	aa.doch_week,
	aa.doch_date,
	aa.from_prace_start,
	aa.from_manual,
	aa.to_prace_end,
	aa.to_manual,
	aa.dur_obed,
	aa.obed_auto,
	aa.dur_prace_celk,
	aa.dur_prace_wend,
	aa.dur_lek,
	aa.dur_dov,
	aa.dur_nem,
	aa.dur_volno,
	aa.dur_sluz
FROM (
	SELECT
		person_ID,
		DOCH_DATE,
		extract(WEEK FROM doch_date) AS DOCH_WEEK,
		min(FROM_TIME) AS FROM_PRACE_START,
		max(FROM_MANUAL) AS FROM_MANUAL,
		max(TO_TIME) AS TO_PRACE_END,
		max(TO_MANUAL) AS TO_MANUAL,
		sum(CASE WHEN (cin_cin_kod = 'MO') THEN doch_dur WHEN (cin_cin_kod = 'OA') THEN -doch_dur ELSE 0 END) AS dur_obed,
		max(cin_cin_kod = 'OA') AS obed_auto,
		sum(CASE WHEN calcprac THEN doch_dur ELSE 0 END) AS dur_prace_celk,
		sum(CASE WHEN calcprac AND ISO_DAY_OF_WEEK(DOCH_DATE) >= 6 THEN doch_dur ELSE 0 END) AS dur_prace_wend,
		sum(CASE WHEN cin_cin_kod = 'L' THEN doch_dur ELSE 0 END) AS dur_lek,
		sum(CASE WHEN cin_cin_kod = 'dc' OR cin_cin_kod = 'dp' THEN doch_dur ELSE 0 END) AS dur_dov,
		sum(CASE WHEN cin_cin_kod = 'ne' THEN doch_dur ELSE 0 END) AS dur_nem,
		sum(CASE WHEN cin_cin_kod = 'nv' THEN doch_dur ELSE 0 END) AS dur_volno,
		sum(CASE WHEN cin_cin_kod = 'SC' THEN doch_dur ELSE 0 END) AS dur_sluz
	FROM VIZMAN.DOCH dd
	GROUP BY PERSON_ID, DOCH_DATE
) AS aa
ORDER BY PERSON_ID ASC, DOCH_DATE ASC
;

----------------------------------------------------------------

COMMIT;

DROP VIEW VIZMAN.DOCH_ROK_VIEW IF EXISTS;

CREATE OR REPLACE FORCE VIEW VIZMAN.DOCH_ROK_VIEW (
	ID,
	PERSON_ID,
	DOCH_YM,
	DUR_PRACE_CELK,
	DUR_PRACE_WEND,
	DUR_LEK,
	DUR_DOV,
	DUR_NEM,
	DUR_VOLNO,
	DUR_SLUZ
) AS
SELECT
	ROWNUM() AS ID,
	aa.person_id,
	aa.doch_ym,
	aa.dur_prace_celk,
	aa.dur_prace_wend,
	aa.dur_lek,
	aa.dur_dov,
	aa.dur_nem,
	aa.dur_volno,
	aa.dur_sluz
FROM (
	SELECT
		person_ID,
		(EXTRACT(YEAR FROM DOCH_DATE) * 100) + EXTRACT(MONTH FROM DOCH_DATE) AS DOCH_YM,
		sum(CASE WHEN calcprac THEN doch_dur ELSE 0 END) AS dur_prace_celk,
		sum(CASE WHEN calcprac AND ISO_DAY_OF_WEEK(DOCH_DATE) >= 6 THEN doch_dur ELSE 0 END) AS dur_prace_wend,
		sum(CASE WHEN cin_cin_kod = 'L' THEN doch_dur ELSE 0 END) AS dur_lek,
		sum(CASE WHEN cin_cin_kod = 'dc' OR cin_cin_kod = 'dp' THEN doch_dur ELSE 0 END) AS dur_dov,
		sum(CASE WHEN cin_cin_kod = 'ne' THEN doch_dur ELSE 0 END) AS dur_nem,
		sum(CASE WHEN cin_cin_kod = 'nv' THEN doch_dur ELSE 0 END) AS dur_volno,
		sum(CASE WHEN cin_cin_kod = 'SC' THEN doch_dur ELSE 0 END) AS dur_sluz
	FROM VIZMAN.DOCH dd
	GROUP BY PERSON_ID, DOCH_YM
) AS aa
ORDER BY PERSON_ID ASC, DOCH_YM ASC
;

select
(EXTRACT(YEAR FROM DOCH_DATE) * 100) + EXTRACT(MONTH FROM DOCH_DATE)
-- + EXTRACT(MONTH FROM DOCH_DATE)) AS DOCH_YM
FROM vizman.DOCH
;

