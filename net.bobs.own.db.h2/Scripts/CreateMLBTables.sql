CREATE SCHEMA IF NOT EXISTS mlbdb;

--DROP TABLE mlbdb.PEOPLE;
--
--CREATE TABLE mlbdb.people
--(playerID       CHARACTER(9) 	NOT NULL,
-- birthYear      CHARACTER(4),
-- birthMonth     CHARACTER(2),
-- birthDay       CHARACTER(2),
-- birthCountry   varchar(14),
-- birthState     varchar(22),
-- birthCity      varchar(28),
-- deathYear      CHARACTER(4),
-- deathMonth     CHARACTER(2),
-- deathDay       CHARACTER(2),
-- deathCountry   varchar(14),
-- deathState     varchar(20),
-- deathCity      varchar(28),
-- nameFirst      varchar(12),
-- nameLast       varchar(14)	NOT NULL,
-- nameGiven      varchar(44),
-- weight         SMALLINT,
-- height         TINYINT,
-- bats           CHARACTER(1),         
-- throws         CHARACTER(1),
-- debut          Date,
-- finalGame      Date,
-- retroID        CHARACTER(8),
-- bbrefID        CHARACTER(9),
-- ) 
-- AS SELECT * 
-- FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\People.csv')
-- ;

--DROP TABLE MLBDB.teams;
--
--CREATE TABLE mlbdb.teams
--(yearID         CHARACTER(4)        NOT NULL,
--lgID            CHARACTER(2)        NOT NULL,
--teamID          CHARACTER(3)        NOT NULL,
--franchID        CHARACTER(3)        NOT NULL,
--divID           CHARACTER(1),
--Rank            tinyint             NOT NULL,
--G               SMALLINT            NOT NULL,
--GHome           tinyint,
--W               tinyint             NOT NULL,
--L               SMALLINT            NOT NULL,
--DivWin          CHARACTER(1),
--WCWin           CHARACTER(1),
--LgWin           CHARACTER(1),
--WSWin           CHARACTER(1),
--R               SMALLINT            NOT NULL,
--AB              SMALLINT            NOT NULL,
--H               SMALLINT            NOT NULL,
--double          SMALLINT            NOT NULL,
--triples         SMALLINT            NOT NULL,    
--HR              SMALLINT            NOT NULL,
--BB              Decimal(4,1),
--SO              SMALLINT,
--SB              Decimal(4,1),
--CS              Decimal(4,1),
--HBP             Decimal(4,1),
--SF              SMALLINT,
--RA              SMALLINT            NOT NULL,
--ER              SMALLINT            NOT NULL,
--ERA             decimal(3,2)        NOT NULL,
--CG              SMALLINT            NOT NULL,
--SHO             tinyint             NOT NULL,
--SV              tinyint             NOT NULL,
--IPOuts          SMALLINT            NOT NULL,
--HA              SMALLINT            NOT NULL,
--HRA             SMALLINT            NOT NULL,
--BBA             SMALLINT            NOT NULL,
--SOA             SMALLINT            NOT NULL,
--E               SMALLINT            NOT NULL,
--DP              SMALLINT            NOT NULL,
--FP              decimal(4,3)        NOT NULL,
--name            varchar(33)         NOT NULL,
--park            varchar(70),
--attendance      integer,
--BPF             SMALLINT,
--PPF             SMALLINT,
--teamIDBR        char(3),
--teamIDlahman45  char(3),
--teamIDretro     char(3)
--)
-- AS SELECT * 
-- FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\Teams.csv')
--;


--CREATE TABLE mlbdb.batting 
--(
--playerID		CHARACTER(9)		NOT NULL,
--yearID          CHARACTER(4)		NOT NULL,
--stint          	Tinyint				NOT NULL,
--teamID         	CHARACTER(3)		NOT NULL,
--lgID           	CHARACTER(2)		NOT NULL,
--games           SMALLINT			NOT NULL,
--atBats          SMALLINT			NOT NULL,
--runs			SMALLINT			NOT NULL,
--hits			SMALLINT			NOT NULL,
--doubles			tinyint				NOT NULL,
--triples			tinyint				NOT NULL,
--homeruns		tinyint				NOT NULL,
--RBI            	SMALLINT,
--SB              SMALLINT,
--CS              tinyint,
--BB              SMALLINT,
--SO             	SMALLINT,
--IBB             tinyint,
--HBP             tinyint,
--SH             	tinyint,
--SF             	tinyint,
--GIDP            tinyint
--) 
-- AS SELECT * 
-- FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\Batting.csv')
-- ;

--SELECT count(*)
--FROM MLBDB.batting;
--
--CREATE TABLE MLBDB.battingPost
--(
--yearID         CHARACTER(8)             NOT NULL,
--round          varchar(5)               NOT NULL,
--playerID       CHARACTER(9)             NOT NULL,
--teamID         CHARACTER(3)             NOT NULL,
--lgID           CHARACTER(2)             NOT NULL,
--G              tinyint,
--AB             tinyint,
--R              tinyint,
--H              tinyint,
--doubles        tinyint,
--triples        tinyint,
--HR             tinyint,
--RBI            tinyint,
--SB             tinyint,
--CS             tinyint,
--BB             tinyint,
--SO             tinyint,
--IBB            tinyint,
--HBP            tinyint,
--SH             tinyint,
--SF             tinyint,
--GIDP           tinyint
--)
-- AS SELECT * 
-- FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\BattingPost.csv')
-- ;

--SELECT *
--FROM mlbdb.battingpost
--WHERE yearid = '1968'
--;


--CREATE TABLE mlbdb.pitching 
--(
--playerID       CHARACTER(9)         NOT NULL,
--yearID         char(4)              NOT NULL,
--stint          tinyint              NOT NULL,
--teamID         CHARACTER(3)         NOT NULL,
--lgID           CHARACTER(2)         NOT NULL,
--W              tinyint              NOT NULL,
--L              tinyint              NOT NULL,
--G              tinyint              NOT NULL,
--GS             tinyint              NOT NULL,
--CG             tinyint              NOT NULL,
--SHO            tinyint              NOT NULL,
--SV             tinyint              NOT NULL,
--IPOuts         SMALLINT             NOT NULL,
--H              SMALLINT             NOT NULL,
--ER             SMALLINT             NOT NULL,
--HR             tinyint              NOT NULL,
--BB             SMALLINT             NOT NULL,
--SO             SMALLINT             NOT NULL,
--BAOpp          decimal(4,3),
--ERA            decimal(5,2),
--IBB            tinyint,
--WP             tinyint,
--HBP            decimal(3,1),
--BK             tinyint              NOT NULL,
--BFP            SMALLINT,
--GF             tinyint              NOT NULL,
--R              SMALLINT             NOT NULL,
--SH             tinyint,
--SF             tinyint,
--GIDP           tinyint
--)
-- AS SELECT * 
-- FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\Pitching.csv')
-- ;
-- 
-- SELECT * 
-- FROM mlbdb.pitching
-- WHERE yearid = '1982'
-- AND teamid = 'SLN'
-- ORDER BY era
-- ;

DROP TABLE mlbdb.pitchingpost;
CREATE TABLE mlbdb.PitchingPost
(
playerID       CHARACTER(9)         NOT NULL,
yearID         CHARACTER(4)         NOT NULL,
round          CHARACTER(5)         NOT NULL,
teamID         CHARACTER(3)         NOT NULL,
lgID           CHARACTER(2)         NOT NULL,
W              tinyint              NOT NULL,
L              tinyint              NOT NULL,
G              tinyint              NOT NULL,
GS             tinyint              NOT NULL,
CG             tinyint              NOT NULL,
SHO            tinyint              NOT NULL,
SV             tinyint              NOT NULL,
IPOuts         SMALLINT             NOT NULL,
H              tinyint              NOT NULL,
ER             tinyint              NOT NULL,
HR             tinyint              NOT NULL,
BB             tinyint              NOT NULL,
SO             tinyint              NOT NULL,
BAOpp          decimal(4,3),
ERA            decimal(6,3),
IBB            tinyint,
WP             tinyint,
HBP            tinyint,
BK             tinyint,
BFP            SMALLINT,
GF             tinyint,
R              tinyint,
SH             tinyint,
SF             tinyint,
GIDP           tinyint
)
 AS SELECT * 
 FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\PitchingPost_Fix.txt')
;

SELECT *
FROM MLBDB.PITCHINGpost
WHERE yearid = '1982'
ORDER BY era
;

--CREATE TABLE MLBDB.managers
--(
--playerID       character(9)     NOT NULL,
--yearID         CHARACTER(4)     NOT NULL,
--teamID         CHARACTER(3)     NOT NULL,
--lgID           CHARACTER(2)     NOT NULL,
--inseason       tinyint          NOT NULL,
--G              SMALLINT         NOT NULL,
--W              tinyint          NOT NULL,
--L              tinyint          NOT NULL,
--rank           tinyint,
--plyrMgr        CHARACTER(1)     NOT NULL
--)
-- AS SELECT * 
-- FROM csvread('D:\mlb_sam_lehman\2018\baseballdatabank-2019.2\core\Managers.csv')
-- ;
-- 
-- SELECT *
-- FROM MLBDB.managers
-- WHERE playerid LIKE 'gardero%'