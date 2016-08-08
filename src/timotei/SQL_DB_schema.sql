.header on
.mode column
.timer on
.open harkkakanta.db --name of used database
PRAGMA foreign_keys = ON;

CREATE TABLE "smartPost" (
	"smartPostID"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
	"longitude"	FLOAT NOT NULL,
	"latitude"	FLOAT NOT NULL,
	"smartPostName"	VARCHAR(30) NOT NULL,
	"colorOnMap"	VARCHAR(20) NOT NULL DEFAULT 'Red',
	"postalCode"	CHAR(5) NOT NULL,
	"streetAddress"	VARCHAR(50) NOT NULL,
	"info"	VARCHAR(60),

	FOREIGN KEY("postalCode") REFERENCES "city"("postalCode")
);

CREATE TABLE "city" (
	"postalCode"	CHAR(5) PRIMARY KEY ON CONFLICT IGNORE NOT NULL,
	"cityName"	VARCHAR(35) NOT NULL
);

CREATE TABLE "breakTypes" (
	"breakTypeID" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"btype" 	VARCHAR(20) NOT NULL DEFAULT 'normal',
	"percent" INT DEFAULT NULL

	CHECK ("btype" IN ('normal', 'special', 'breaker', 'protective'))
);

CREATE TABLE "itemType" (
	"itemTypeID" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"itemName"	VARCHAR(30) NOT NULL,
	"breakable"	BOOLEAN NOT NULL DEFAULT FALSE,
	"itemSize"	REAL NOT NULL,
	"itemWeight" REAL NOT NULL,
	"breakType" INT NOT NULL DEFAULT 1,

	FOREIGN KEY("breakType") REFERENCES "breakTypes"("typeID"),
	CHECK ("itemWeight" > 0),
	CHECK ("itemSize" > 0)
);

CREATE TABLE "itemInPackage" (
	"itemID" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"itemTypeID" INT NOT NULL,
	"packageID" INT NOT NULL,
	
	FOREIGN KEY("packageID") REFERENCES "package"("packageID")
	ON DELETE CASCADE,
	FOREIGN KEY("itemTypeID") REFERENCES "itemType"("itemTypeID")
);

CREATE TABLE "package" (
	"packageID"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"deliveryID"	INT NOT NULL,
	"packageSize"	FLOAT NOT NULL,

	FOREIGN KEY("deliveryID") REFERENCES "packageDelivery"("deliveryID")
	ON DELETE CASCADE
);

CREATE TABLE "packageDelivery" (
	"deliveryID"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"deliveryType"	INT NOT NULL,
	"fromSP"	INT NOT NULL,
	"toSP"		INT NOT NULL,


	FOREIGN KEY("deliveryType") REFERENCES "deliveryClass"("deliveryType"),
	FOREIGN KEY("fromSP") REFERENCES "smartPost"("smartPostID"),	
	FOREIGN KEY("toSP") REFERENCES "smartPost"("smartPostID"),

	CHECK ("toSp" <> "fromSP")
);

CREATE TABLE "deliveryClass" (
	"deliveryType"			INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"distanceLimit"			INT DEFAULT NULL,
	"emptySizeLimit"		INT DEFAULT NULL,
	"amountOfTimoteiMen"	INT NOT NULL DEFAULT 1,
	
	CHECK ("amoutOfTimoteiMen" > 0)
);

CREATE TABLE "delivers" (
	"deliveryID"	INT NOT NULL,
	"TIMOTEI_ID"	INT NOT NULL,
	"distance"	REAL NOT NULL,

	PRIMARY KEY("deliveryID", "TIMOTEi_ID"),
	FOREIGN KEY("deliveryID") REFERENCES "packageDelivery"("deliveryID"),
	FOREIGN KEY("TIMOTEi_ID") REFERENCES "TIMOTEi_man"("TIMOTEi_ID")
);

CREATE TABLE "TIMOTEI_man" (
	"TIMOTEI_ID"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"familyName"	VARCHAR(30) NOT NULL,
	"firstName"	VARCHAR(20) NOT NULL,
	"stressLimit"	INT DEFAULT 100,
	"stressLevel"	INT NOT NULL
);

CREATE TABLE "stressActions" (
	"TIMOTEI_ID"	INT NOT NULL UNIQUE,
	"action1"	VARCHAR(20) NOT NULL,
	"action2"	VARCHAR(20),
	"action3"	VARCHAR(20),

	PRIMARY KEY("action1", "TIMOTEi_ID")
);

--Used views

CREATE VIEW "smartPostView" AS
	SELECT smartPostID, longitude, latitude, smartPostName, colorOnMap, sp.postalCode, cityName, streetAddress, info 
	FROM smartPost sp 
	JOIN city c ON sp.postalCode = c.postalCode; 
	
CREATE VIEW "packageView" AS
	SELECT packageID, pd.deliveryID, packageSize, deliveryType, s.smartPostID[from_ID], s.smartPostName[from_smartPost], s.streetAddress[from_Address], s.postalCode[from_ZIP], s.cityName[from_City], sp.smartPostID[to_ID], sp.smartPostName[to_smartPost], sp.streetAddress[to_Address], sp.postalCode[to_ZIP], sp.cityName[to_City]
	FROM package p
	JOIN packageDelivery pd ON p.deliveryID = pd.deliveryID 
	JOIN smartPostView s ON s.smartPostID = pd.fromSP
	JOIN smartPostView sp ON sp.smartPostID = pd.toSP;
	
CREATE VIEW "itemTypeView" AS
	SELECT itemTypeID, itemName, breakable, itemSize, itemWeight, btype, percent
	FROM itemType i
	JOIN breakTypes b ON i.breakType = b.breakTypeID;
	
CREATE VIEW "inPackagesView" AS
	SELECT ip.packageID, itemID, itemName, btype[breakType], percent, breakable, itemSize, itemWeight, from_ID, from_smartPost, to_ID, to_smartPost, deliveryType
	FROM itemInPackage ip
	JOIN itemTypeView it ON ip.itemTypeID = it.itemTypeID
	JOIN packageView p ON ip.packageID = p.packageID
	ORDER BY ip.packageID, itemName;
	
CREATE VIEW "timoteiManView" AS
	SELECT T.TIMOTEI_ID, firstName, familyName, stressLimit, stressLevel, action1, action2, action3
	FROM TIMOTEI_man T
	JOIN stressActions s ON T.TIMOTEI_ID = s.TIMOTEI_ID;
	
--INITIALIZE inserts 
--DeliveryClasses
	
INSERT INTO "deliveryClass" ("deliveryType", "distanceLimit")
	VALUES (1, 150);

INSERT INTO "deliveryClass" ("deliveryType", "amountOfTimoteiMen", "emptySizeLimit")
	VALUES (2, 2, 10);

INSERT INTO "deliveryClass" ("deliveryType")
	VALUES (3);

--BreakTypes
	
INSERT INTO "breakTypes" ("btype", "percent")
	VALUES ('normal', 100);

INSERT INTO "breakTypes" ("btype", "percent")
	VALUES ('breaker', 100);
	
INSERT INTO "breakTypes" ("btype", "percent")
	VALUES ('protective', 50);

INSERT INTO "breakTypes" ("btype", "percent")
	VALUES ('special', 50);
	
--TIMOTEI men
	
INSERT INTO "TIMOTEI_man" ("familyName", "firstName", "stressLimit", "stressLevel")
	VALUES ('Postimies', 'Pate', 9001, 0);
	
INSERT INTO "TIMOTEI_man" ("familyName", "firstName", "stressLimit", "stressLevel")
	VALUES ('Muumi', 'Posteljooni', 100, 0);
	
INSERT INTO "TIMOTEI_man" ("familyName", "firstName", "stressLimit", "stressLevel")
	VALUES ('Postinkantaja', 'Pirkko', 100, 0);
	
INSERT INTO "TIMOTEI_man" ("familyName", "firstName", "stressLimit", "stressLevel")
	VALUES ('Hiiri', 'Mikki', 150, 0);
	
INSERT INTO "TIMOTEI_man" ("familyName", "firstName", "stressLimit", "stressLevel")
	VALUES ('Ankka', 'Aku', 50, 50);

--Stress Actions

INSERT INTO "stressActions" ("TIMOTEI_ID", "action1", "action2", "action3")
	VALUES (1, 'Kissan tappo :(', null, null);
	
INSERT INTO "stressActions" ("TIMOTEI_ID", "action1", "action2", "action3")
	VALUES (2, 'Paketin pudotus Yksinäisiltä vuorilta ', 'Paketin antaminen Haisulille', null);

INSERT INTO "stressActions" ("TIMOTEI_ID", "action1", "action2", "action3")
	VALUES (3, 'Paketin aukaisu', 'Paketin pudotus', 'Paketin heittämisen seinään');
	
INSERT INTO "stressActions" ("TIMOTEI_ID", "action1", "action2", "action3")
	VALUES (4, 'Paketin potkaisu', null, null);

INSERT INTO "stressActions" ("TIMOTEI_ID", "action1", "action2", "action3")
	VALUES (5, 'Paketin räjäytys', 'Paketin flippaaminen', 'Ankan raivon käyttäminen pakettiin');
	
