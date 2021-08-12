
DROP  SCHEMA if exists netzwerkdb2 CASCADE;
CREATE SCHEMA netzwerkDB2;
SET search_path TO netzwerkDB2;

DROP TABLE IF EXISTS place_temp CASCADE;
DROP TABLE IF EXISTS post_temp CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS tagclass CASCADE;
DROP TABLE IF EXISTS tag CASCADE;
DROP TABLE IF EXISTS tagClass_isSubclassof CASCADE;
DROP TABLE IF EXISTS tag_has_Type CASCADE;
DROP TABLE IF EXISTS place CASCADE;
DROP TABLE IF EXISTS continent CASCADE;
DROP TABLE IF EXISTS country CASCADE;
DROP TABLE IF EXISTS city CASCADE;
DROP TABLE IF EXISTS message_hasTag CASCADE;
DROP TABLE IF EXISTS forum_hasTag CASCADE;
DROP TABLE IF EXISTS forum CASCADE;
DROP TABLE IF EXISTS forum_hasMember CASCADE;
DROP TABLE IF EXISTS "message" CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS commentOF CASCADE;
DROP TABLE IF EXISTS Person_likes_Message CASCADE;
DROP TABLE IF EXISTS person_knows_Person CASCADE;
DROP TABLE IF EXISTS Person_hasinterest CASCADE;
DROP TABLE IF EXISTS studyAT CASCADE;
DROP TABLE IF EXISTS workAT CASCADE;
DROP TABLE IF EXISTS organisation CASCADE;
DROP TABLE IF EXISTS company CASCADE;
DROP TABLE IF EXISTS university CASCADE;
DROP TABLE IF EXISTS email CASCADE;
DROP TABLE IF EXISTS speaks CASCADE;
DROP TRIGGER IF EXISTS auto_fill_post_table ON message_temp;
DROP TRIGGER IF EXISTS auto_fill_comment_table ON comment_temp;
DROP TRIGGER IF EXISTS auto_fill_places_tables ON place_temp;
DROP TRIGGER IF EXISTS auto_fill_organisation_tables ON organisation_temp;
DROP TRIGGER IF EXISTS Former_employee ON workAT;

CREATE TABLE tagClass(
	tagClassID int PRIMARY KEY ,
	tClassName varchar(255) not null, 
	url varchar(2048)  
);
		
copy netzwerkdb2.tagClass (tagClassID, tClassName,url)
FROM 'D:/social_network\tagclass_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
Create TABLE tag(
	tagID SERIAL PRIMARY KEY, 
	name VARCHAR(255) NOT NULL, 
	url VARCHAR (2048) 
);
	
copy netzwerkdb2.tag (tagID, name, url)
FROM 'D:/social_network\tag_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

----------------------
CREATE TABLE tagClass_isSubclassof(
	superTagClassID INT,
	subTagClass INT,
	PRIMARY KEY(superTagClassID,subTagClass),
	FOREIGN KEY (superTagClassID) REFERENCES tagClass(tagClassID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (subTagClass) REFERENCES tag(tagID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.tagClass_isSubclassof (superTagClassID, subTagClass)  
FROM 'D:/social_network\tagclass_isSubclassOf_tagclass_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE tag_has_Type(
	tagID INT , 
	tagClassID INT ,
	PRIMARY KEY(tagID,tagClassID),
	FOREIGN KEY (tagClassID) REFERENCES tagClass(tagClassID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (tagID) REFERENCES tag(tagID) ON UPDATE CASCADE ON DELETE CASCADE
);
	
copy netzwerkdb2.tag_has_Type (tagID, tagClassID)  
FROM 'D:/social_network\tag_hasType_tagclass_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE place_temp(
	placeID SERIAL PRIMARY KEY,
	name VARCHAR (255),
	url VARCHAR (2048),
	type VARCHAR (255),
	isPartOf INT 
);

CREATE TABLE place(
	placeID SERIAL PRIMARY KEY,
	name VARCHAR (255),
	url VARCHAR (2048)
);


CREATE TABLE continent(
	continentID int,
	primary key (continentID),
    FOREIGN KEY (continentID) REFERENCES place(placeID) ON UPDATE NO ACTION ON DELETE CASCADE	
);
	

CREATE TABLE country( 
	countryID int PRIMARY KEY,
	isPartOf int not null,
	FOREIGN KEY (countryID) REFERENCES place(placeID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (isPartOf) REFERENCES continent(continentID) ON UPDATE CASCADE ON DELETE CASCADE
) ;


CREATE TABLE city(
	cityID int PRIMARY KEY,
	isPartOf int not null,
	FOREIGN KEY (cityID)REFERENCES place(placeID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (isPartOf) REFERENCES country(countryID) ON UPDATE CASCADE ON DELETE CASCADE	
);


create or replace function fill_places_tables()
returns trigger 
as $$
begin
insert into place(placeID,name,url) values (NEW.placeID,NEW.name,NEW.url) ON CONFLICT DO NOTHING ;
if(NEW.type='country') then
insert into place(placeID) VALUES (NEW.isPartOf) ON CONFLICT DO NOTHING ;
insert into continent(continentID) values (NEW.isPartOf) ON CONFLICT DO NOTHING ;
insert into country(countryID,isPartOf) values (NEW.placeID,NEW.isPartOf);
elseif 	(NEW.type='city') then
INSERT INTO city values (NEW.placeID,NEW.isPartOf);
elseif (NEW.type='continent') then
UPDATE place SET name = NEW.name, url = NEW.url
WHERE place.placeID=New.placeid;
insert into continent values (NEW.placeID) ON CONFLICT DO NOTHING;
end if ;
return null;
end;
$$ language plpgsql;

CREATE  trigger auto_fill_places_tables
before insert on place_temp
for each row execute PROCEDURE fill_places_tables(); 

copy netzwerkdb2.place_temp (placeID, name, url, type, isPartOf)
FROM 'D:/social_network\place_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE person (
	
	personID BIGSERIAL PRIMARY KEY,
	firstName VARCHAR(25) NOT NULL,
	lastName VARCHAR(25)  NOT NULL,
	gender VARCHAR(25),
	birthday DATE ,
	creationDate TIMESTAMP check ( NOW()::timestamp > creationDate),
	locationIP  VARCHAR(255),
	browserUsed VARCHAR(255),
	cityID INT,
	FOREIGN KEY (cityID) REFERENCES city(cityID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.person (personID, firstName, lastName, gender, birthday, creationDate, locationIP, browserUsed, cityID)  
FROM 'D:/social_network\person_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE emails(
	 personID bigint,
	 email VARCHAR(255) UNIQUE NOT NULL CHECK ( email ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' ),
	 PRIMARY KEY (personID,email),
	 FOREIGN KEY (personID) REFERENCES PERSON(PERSONID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.emails (personID, email)  
FROM 'D:/social_network\person_email_emailaddress_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE speaks (
	 speaksID bigint,
	 speaks VARCHAR(255) NOT NULL,
	 PRIMARY KEY (speaksID, speaks),
	 FOREIGN KEY (speaksID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.speaks (speaksID, speaks)  
FROM 'D:/social_network\person_speaks_language_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';
	 
------------------------
CREATE TABLE forum(
	forumID BIGSERIAL PRIMARY KEY ,
	title VARCHAR(55),
	creationDate TIMESTAMP ,
	moderatorID BIGINT ,
	FOREIGN KEY (moderatorID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE
	);
	
copy netzwerkdb2.forum 
FROM 'D:/social_network\forum_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';
	
------------------------
CREATE TABLE "message"(
	messageID BIGSERIAL PRIMARY KEY,
	"content" VARCHAR(255),
	"length" INT, 
	creator BIGINT ,
	countryID BIGINT,
	creationDate TIMESTAMP,
	browserused  VARCHAR(55),
	locationIP   VARCHAR(55),
	FOREIGN KEY (creator) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (countryID) REFERENCES country(countryID) ON UPDATE CASCADE ON DELETE CASCADE	
);

CREATE TABLE post_temp(
	messageID BIGSERIAL PRIMARY KEY,
	imageFile varchar(255),
	creationDate TIMESTAMP,
	locationIP   VARCHAR(55),
	browserused  VARCHAR(55),
	"language" VARCHAR(55),
	"content" VARCHAR(255),
	"length" INT, 
	creator BIGINT ,
	forumID BIGINT ,
	countryID INT,
	FOREIGN KEY (creator) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (countryID) REFERENCES country(countryID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (forumID) REFERENCES forum(forumID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE post(
	postID BIGSERIAL PRIMARY KEY,
	imageFile varchar(255),
	forumID BIGINT ,
	FOREIGN KEY (postID) REFERENCES "message"(messageID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (forumID) REFERENCES forum(forumID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE comment_temp(
	messageID BIGSERIAL PRIMARY KEY,
	creationDate TIMESTAMP,
	locationIP   VARCHAR(55),
	browserused  VARCHAR(55),
	"content" VARCHAR(255),
	"length" INT, 
	creator BIGINT ,
	countryID INT,
	replyOfPost BIGINT,
	replyOfComment BIGINT,
	FOREIGN KEY (creator) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (countryID) REFERENCES country(countryID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (replyOfPost) REFERENCES "post"(postid) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (replyOfComment) REFERENCES "comment_temp"(messageID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "comment"(
	commentID BIGSERIAL PRIMARY KEY,
	replyOfPost BIGINT ,
	replyOfComment BigINT,
	FOREIGN KEY (replyOfPost) REFERENCES post(postID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (replyOfComment) REFERENCES COMMENT(commentID) ON UPDATE CASCADE ON DELETE CASCADE

	);

create or replace function fill_post_tables()
returns trigger 
as $$
begin

insert into "message"(messageid,content,length,creator,countryid,creationdate,browserused,locationip)
values (NEW.messageid,NEW.content,NEW.length,NEW.creator,NEW.countryid,NEW.creationdate,NEW.browserused
		,NEW.locationip) ON CONFLICT DO NOTHING ;

insert into "post"(postID,imagefile,forumid)
values (NEW.messageid,NEW.imagefile,NEW.forumid) ON CONFLICT DO NOTHING ;

return null;
end;
$$ language plpgsql;

CREATE trigger auto_fill_post_table
before insert on post_temp
for each row execute PROCEDURE fill_post_tables();

copy netzwerkdb2.post_temp(messageID, imageFile, creationDate, locationIP, browserused, 
							  "language", "content", "length", creator,forumID,countryID)
FROM 'D:/social_network\post_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';


-------------------------------------
create or replace function fill_comment_tables()
returns trigger 
as $$
begin

insert into "message"(messageid,content,length,creator,countryid,creationdate,browserused,locationip)
values (NEW.messageid,NEW.content,NEW.length,NEW.creator,NEW.countryid,NEW.creationdate,NEW.browserused
		,NEW.locationip) ON CONFLICT DO NOTHING ;

insert into "comment"(commentid,replyOfPost,replyOfComment)
values (NEW.messageID,NEW.replyOfPost,NEW.replyOfComment) ON CONFLICT DO NOTHING ;

return null;
end;
$$ language plpgsql;

CREATE trigger auto_fill_comment_table
before insert on comment_temp
for each row execute PROCEDURE fill_comment_tables();


copy netzwerkdb2.comment_temp(messageID, creationDate, locationIP, browserused, 
							  "content", "length", creator,countryID,replyOfPost,replyOfComment)
FROM 'D:/social_network\comment_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';


------------------------
CREATE TABLE message_hasTag(
	messageID BIGINT ,
	tagID INT ,
	PRIMARY KEY (messageID, tagID),
	FOREIGN KEY (messageID) REFERENCES "message"(messageID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (tagID) REFERENCES tag(tagID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.message_hastag(messageID,tagID)
FROM 'D:/social_network\post_hasTag_tag_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

copy netzwerkdb2.message_hastag(messageID,tagID)
FROM 'D:/social_network\comment_hasTag_tag_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';


------------------------
CREATE TABLE forum_hasTag(
	forumID BIGINT ,
	tagID INT ,
	PRIMARY KEY (forumID, tagID),
	FOREIGN KEY (forumID) REFERENCES forum(forumID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (tagID) REFERENCES tag(tagID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.forum_hasTag(forumID,tagID)
FROM 'D:/social_network\forum_hasTag_tag_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE forum_hasMember(
	personID BIGINT ,
	forumID  BIGINT ,
	joinDate TIMESTAMP , 
	PRIMARY KEY (forumID, personID),
	FOREIGN KEY (forumID) REFERENCES forum(forumID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (personID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.forum_hasMember(forumID,personID,joinDate)
FROM 'D:/social_network\forum_hasMember_person_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------

CREATE TABLE person_likes_Message(
	personID BIGINT,
	messageID BIGINT,
	creationDate TIMESTAMP,
	PRIMARY KEY (personID, messageID),
	FOREIGN KEY (messageID) REFERENCES "message"(messageID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (personID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.person_likes_Message(personID,messageID,creationDate)
FROM 'D:/social_network\person_likes_post_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

copy netzwerkdb2.person_likes_Message(personID,messageID,creationDate)
FROM 'D:/social_network\person_likes_comment_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE person_knows_Person(
	super_personID BIGINT NOT NULL,
	sub_personID BIGINT NOT NULL,
	creationDate TIMESTAMP NOT NULL,
	PRIMARY KEY (super_personID, sub_personID),
	FOREIGN KEY (sub_personID) REFERENCES "person"(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (super_personID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.person_knows_Person(super_personID,sub_personID,creationDate)
FROM 'D:/social_network\person_knows_person_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE Person_hasinterest(
	personID BIGINT ,
	tagID INT,    
	PRIMARY KEY (personID , tagID),
	FOREIGN KEY (personID) REFERENCES "person"(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (tagID) REFERENCES tag(tagID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.Person_hasinterest(personID,tagID)
FROM 'D:/social_network\person_hasInterest_tag_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE organisation(
	OrganisationID SERIAL PRIMARY KEY,
	name VARCHAR(255) not null ,
	url VARCHAR(2048)	
);

CREATE TABLE organisation_temp(
	OrganisationID SERIAL PRIMARY KEY,
	"type" VARCHAR(55),
	"name" VARCHAR(255) not null ,
	url VARCHAR(2048),
	placeID INT
);

CREATE TABLE university(
	uniID INT PRIMARY KEY,
	cityID INT ,
	FOREIGN KEY (uniID) REFERENCES Organisation(OrganisationID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cityID) REFERENCES city(cityID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE company(
	companyID INT PRIMARY KEY,
	countryID INT ,
	FOREIGN KEY (companyID) REFERENCES Organisation(OrganisationID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (countryID) REFERENCES country(countryID) ON UPDATE CASCADE ON DELETE CASCADE
);


create or replace function fill_organisation_tables()
returns trigger 
as $$
begin

insert into organisation(OrganisationID,"name",url) values (NEW.OrganisationID,NEW.name,NEW.url) ON CONFLICT DO NOTHING ;

if(NEW.type='company') then
insert into company(companyid,countryID) VALUES (NEW.OrganisationID,NEW.placeID) ON CONFLICT DO NOTHING ;

elseif 	(NEW.type='university') then
insert into university(uniID,cityid) VALUES (NEW.OrganisationID,NEW.placeID) ON CONFLICT DO NOTHING ;

end if ;
return null;
end;
$$ language plpgsql;


CREATE  trigger auto_fill_organisation_tables
before insert on organisation_temp
for each row execute PROCEDURE fill_organisation_tables(); 

copy netzwerkdb2.organisation_temp (OrganisationID,"type","name",url,placeID)
FROM 'D:/social_network\organisation_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE studyAT(
	personID BIGINT,
	uniID INT,
	classYear SMALLINT ,
	PRIMARY KEY (uniID, personID),
	FOREIGN KEY (personID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (uniID) REFERENCES university(uniID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.studyAT (personID,uniID,classYear)
FROM 'D:/social_network\person_studyAt_organisation_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';

------------------------
CREATE TABLE workAT(
	personID BIGINT   ,
	companyID INT  ,
	workFrom SMALLINT   ,
	PRIMARY KEY (companyID, personID),
	FOREIGN KEY (personID) REFERENCES person(personID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (companyID) REFERENCES company(companyID) ON UPDATE CASCADE ON DELETE CASCADE
);

copy netzwerkdb2.workAT (personID,companyID,workFrom)
FROM 'D:/social_network\person_workAt_organisation_0_0.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8' QUOTE '\' ESCAPE '''';


------------------------
Drop TABLE IF EXISTS post_temp CASCADE;
Drop TABLE IF EXISTS comment_temp CASCADE;
Drop TABLE IF EXISTS organisation_temp CASCADE;
Drop table IF EXISTS place_temp CASCADE;



-------------------------------------------------------------------
