# netwerk_teil3 

Dies Projekt ist die Lösung des 3.Teil. 

***
# Die benutzte Abhängigkeiten sind:
 * PostgreSQL JDBC Driver mit der Version 42.2.18: https://mvnrepository.com/artifact/org.postgresql/postgresql.
 
 * Hibernate Core Relocation mit der Version 5.4.23.Final: https://mvnrepository.com/artifact/org.hibernate/hibernate-core.
 
 * Hibernate Validator Engine mit der Version 7.0.0.Final: https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator.
 
 * Javax Persistence API mit der Version 2.2: https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api.
 
 * OpenCSV mit der Version 2.2: https://mvnrepository.com/artifact/com.opencsv/opencsv.
    - Diese Abhängigkeit dient zu Mapping von Csv-Datein mit den Java-Klassen

    
***

# Java-Klassen in netzwerk package:
* City: Mapping mit City Tabelle

* Comment: Mapping mit comment Tabelle 

* Company: Mapping mit company Tabelle 

* Continent: Mapping mit continent Tabelle 

* Country: Mapping mit country Tabelle 

* Email: Mapping mit emails Tabelle. Dient zu speichern von mehere Email für eine Person 

* Forum: Mapping mit Forum Tabelle 

* Comment: Mapping mit Comment Tabelle 

* Comment: Mapping mit Comment Tabelle 

* Forum_hasMember: Realisiert die Beziehung zwischen forum und person Tabelle

* Forum_MemberId: Embeddable id durch diese Klasse für die Tabelle forum_hasmember 

* IsSubClassOF: Realisiert die Beziehung zwischen einer tagclass und anderer tagclass

* IssubClassOfId: Embeddable id durch diese Klasse für die Tabelle issubclassof

* Message: Abstrakte Klasse enthält alle gemeinsame Attribute der Klass Post und Comment 

* Organisation: Mapping mit organisation Tabelle

* Person: Mapping mit person Tabelle

* PersonKnowsPersonId: Realisiert die Freundschaftsbeziehung zwischen zwei Personen

* PersonLikePost: Enthält, welche Person welche Post gelikt hat und Mapping mit personlikepost Tabelle

* PersonLikeComment: Enthält, welche Person welche Comment gelikt hat und Mapping mit personlikecomment Tabelle

* Place: Mapping mit place Tabelle und ist der superclass von Continent, City, und Country

* Post: Mapping mit Post Tabelle und erbt von Message

* Speaks: Mapping mit speaks Tabelle. Enthält, welche person welche Sprache spricht

* StudyAt: Mapping mit studyAt Tabelle. Enthält, welche person an welcher uni studiert

* Tag: Mapping mit Tag Tabelle.

* TagClass: Mapping mit TagClass Tabelle.

* University: Mapping mit university Tabelle.

* WorkAt: Mapping mit workat Tabelle. Enthält, welche person an welcher Firma arbeitet

***

## Mitwirkende:
1. Mohammad Ghaith Albaba; Matrikel-Nr: 4068638
2. Mohammad Al Jazzar; Matrikel-Nr: 4068905
3. Mohammad Hashem Sarwari; Matrikel-Nr: 4069473

***

# Link der Repository
https://gitlab.hs-anhalt.de/db/db-lnw

# Link des Projektsordners
https://gitlab.hs-anhalt.de/db/db-lnw/-/tree/master/Abgabe/netzwerk_teil3



