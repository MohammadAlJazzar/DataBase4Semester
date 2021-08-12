SET search_path TO netzwerkDB2;

-- 2.b.
CREATE VIEW pkp_symmetric AS
SELECT super_personid,sub_personid, creationdate FROM person_knows_person
UNION ALL 
SELECT sub_personid,super_personid, creationdate FROM person_knows_person;

SELECT * FROM pkp_symmetric;


-- 1)
SELECT COUNT(uni.uniid)
FROM university as uni 
JOIN city on uni.cityid=city.cityID
JOIN country on city.ispartof=country.countryID
JOIN continent on country.ispartof = continent.continentid
JOIN place on place.placeid = continent.continentid
WHERE place."name"='Africa';


-- 2)
SELECT person.firstname, person.lastname, post.postid FROM post  
JOIN "message" ON post.postid = "message".messageid
JOIN person ON "message".creator = person.personid
WHERE person.birthday = (SELECT MAX(person.birthday)FROM person );


-- 3)
SELECT  place.name , COUNT(COALESCE(post.postid,0)) as anzahl 
FROM post 
join "message" 
on post.postid = "message".messageid
join country
on "message".countryid= country.countryid
join place on country.countryid=place.placeid
GROUP BY place.name 
ORDER BY anzahl ;


--4)
SELECT place."name", sum(person.cityid) AS Einwohnerzahl from person
JOIN city ON city.cityid = person.cityid
JOIN place ON place.placeid = city.cityid
GROUP BY place."name"
order by Einwohnerzahl DESC
LIMIT 1;


--5)
SELECT person.personid, person.firstname,person.lastname FROM person
WHERE person.personid in (
SELECT person_knows_person.sub_personid FROM person 
JOIN person_knows_person ON person_knows_person.super_personid = person.personid
WHERE person.lastname = 'Johansson' And person.firstname = 'Hans');


--6)
SELECT  DISTINCT person.personid,person.firstname,person.lastname FROM person WHERE person.personid in (
SELECT person_knows_person.sub_personid FROM person
JOIN person_knows_person ON person_knows_person.super_personid = person.personid
	WHERE person.personid in (
		SELECT person_knows_person.sub_personid  FROM person 
		JOIN person_knows_person ON person_knows_person.super_personid = person.personid
		WHERE person.lastname = 'Johansson' And person.firstname = 'Hans'
	)
) And person.personid not in (
	SELECT person_knows_person.sub_personid  FROM person 
	JOIN person_knows_person ON person_knows_person.super_personid = person.personid
	WHERE person.lastname = 'Johansson' And person.firstname = 'Hans'
) order by person.lastname;


-- 7)
DROP TABLE IF EXISTS forumIdtemp;
CREATE TABLE forumIdtemp (
	id serial PRIMARY KEY,
	forumID BIGINT 
);

INSERT INTO forumIdtemp(forumID) (
	SELECT forum_hasmember.forumid 
	FROM forum_hasmember
	WHERE forum_hasmember.personid =(
		SELECT person.personid FROM person WHERE person.firstname='Mehmet' AND person.lastname='Koksal'
	)
);

SELECT person.firstname,person.lastname
 FROM person join forum_hasmember on forum_hasmember.personid = person.personid
 WHERE forum_hasmember.forumid=(SELECT forumIdtemp.forumID FROM forumIdtemp WHERE forumIdtemp.id=1)
INTERSECT
SELECT person.firstname,person.lastname
 FROM person join forum_hasmember on forum_hasmember.personid = person.personid
 WHERE forum_hasmember.forumid=(SELECT forumIdtemp.forumID FROM forumIdtemp WHERE forumIdtemp.id=2)
 INTERSECT
 SELECT person.firstname,person.lastname
 FROM person join forum_hasmember on forum_hasmember.personid = person.personid
 WHERE forum_hasmember.forumid=(SELECT forumIdtemp.forumID FROM forumIdtemp WHERE forumIdtemp.id=3);
 
--8) 
SELECT place."name",
COUNT (person.personid)*100/
(SELECT COUNT(person.personid)  FROM person) AS Anteil From person 
JOIN city ON city.cityid = person.cityid
JOIN country ON country.countryid = city.ispartof
JOIN continent ON continent.continentid = country.ispartof
JOIN place ON place.placeid = continentid
GROUP BY place."name";


--9)
SELECT forum.title, COUNT(post.postid) AS Anzahl
FROM forum 
join post on forum.forumid=post.forumid
GROUP BY forum.title
HAVING COUNT(post.postid) > (
	(SELECT COUNT(post.postid) FROM post )/(SELECT COUNT (forum.forumid) FROM forum)
) ORDER BY forum.title ;

--SELECT COUNT(post.postid) FROM post;  -- 7213
--SELECT COUNT (forum.forumid) FROM forum ;  -- 799


--10)
SELECT  person.firstname,person.lastname FROM person
WHERE person.personid in (
	SELECT person_knows_person.sub_personid FROM person
	JOIN person_knows_person ON person_knows_person.super_personid = person.personid
	WHERE person.personid =(
		SELECT creator FROM "message" Where "message".messageid=(
			SELECT messageid FROM(
				SELECT messageid, Count(person_likes_message.personid) AS likes  FROM person_likes_message
				join post on person_likes_message.messageid=post.postid 
				GROUP BY messageid
				ORDER BY likes DESC LIMIT 1
			)AS variabel
		)
	)
)ORDER BY person.lastname


--C)
DROP TABLE IF EXISTS Former_employees_table;
CREATE TABLE Former_employees_table (
	employeeID BIGINT ,
	companyID INT,
	firstname VARCHAR(55) ,
	lastname VARCHAR(55) ,
	expirationOfContract DATE,
	PRIMARY KEY (employeeID,companyID)
);

CREATE OR REPLACE FUNCTION insert_log_entry()
RETURNS trigger AS $$
BEGIN
INSERT INTO Former_employees_table(employeeID,companyID,expirationOfContract,firstname,lastname)
SELECT workAT.personID,companyID,now(),person.firstname,person.lastname FROM workAT  JOIN 
person on OLD.personID=person.personID
WHERE workAT.companyID=OLD.companyID;
RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS Former_employee ON workAt;

CREATE TRIGGER Former_employee
AFTER DELETE ON workAt
FOR EACH ROW
EXECUTE PROCEDURE insert_log_entry();
END;

SELECT * FROM workAt; 
DELETE FROM workAt 
WHERE workAt.personid=12094627905604 AND workAt.companyid=897 ;
SELECT * FROM Former_employees_table;

