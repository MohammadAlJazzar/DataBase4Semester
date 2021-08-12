package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sun.istack.NotNull;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity

public class Person {
    @Id
    @Column(columnDefinition = "bigserial")
    @CsvBindByName(column = "id")
    private long personID;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    @CsvBindByName(column = "firstName")
    private String firstName;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    @CsvBindByName(column = "lastName")
    private String lastName;

    @Column(columnDefinition = "varchar(25)")
    @CsvBindByName(column = "gender")
    private String gender;

    @NotNull
    @Temporal(TemporalType.DATE)
    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "birthday")
    private Date birthday;

    @Temporal(TemporalType.TIMESTAMP)
    @CsvBindByName(column = "creationDate")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date creationDate;

    @CsvBindByName(column = "locationIP")
    private String locationIP;

    @CsvBindByName(column = "browserUsed")
    private String browserUsed;

    //TODO: bestimme cascade in ALL TABLE
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cityId")
    private City city;

    @OneToMany(mappedBy = "person")
    private Set<Emails> emailsSet = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "person")
    private Set<Speaks> speaksSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "person_hasinterest")
    private Set<Tag> tagSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Set<PersonLikesComment> person_likes_commentSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Set<PersonLikePost> personLikePostSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Set<PersonKnowsPerson> personKnowsPersonSet = new HashSet<>();

    @OneToMany(mappedBy = "person", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<WorkAt> workAtSet = new HashSet<>();

    @OneToMany(mappedBy = "person")
    private Set<StudyAt> studyAtSet = new HashSet<>();

    @Transient
    @CsvBindByName(column = "place")
    private Integer cityID;

    public Person(long personID,
                  String firstName,
                  String lastName,
                  String gender,
                  Date birthday,
                  Date creationDate,
                  String locationIP,
                  String browserUsed,
                  City city) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.creationDate = creationDate;
        this.locationIP = locationIP;
        this.browserUsed = browserUsed;
        this.city = city;
    }

    public Person(long personID,
                  String firstName,
                  String lastName,
                  String gender,
                  Date birthday,
                  Date creationDate,
                  String locationIP,
                  String browserUsed,
                  Integer cityID) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.creationDate = creationDate;
        this.locationIP = locationIP;
        this.browserUsed = browserUsed;
        this.cityID = cityID;
    }

    public Person() {
    }

    public void insertHasInterestFromCSV(String path, char splitBy) throws IOException {
        try {
            try (CSVReader reader = new CSVReader(new FileReader(path))) {
                reader.skip(1);
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    Long personId = Long.parseLong(lineInArray[0].split("\\|")[0]);
                    Integer tagId = Integer.parseInt(lineInArray[0].split("\\|")[1]);
                    addInterest(personId, tagId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addInterest(Long personId, Integer tagId) {
        Session session = null;
        try {
            session = MainExecutor.getSession();
            Person p = session.load(Person.class,personId);
            Tag tag = session.load(Tag.class,tagId);
            p.getTagSet().add(tag);
            org.hibernate.Transaction tr = session.beginTransaction();
            session.update(p);
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Person> beans = new CsvToBeanBuilder<Person>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Person.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            org.hibernate.Transaction tr = session.beginTransaction();
            for (int i = 0; i < beans.size(); i++) {
                Person person = beans.get(i);
                City city = session.load(City.class, person.cityID);
                person.setCity(city);
                session.save(person);
            }
            tr.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public long getPersonID() {
        return personID;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLocationIP() {
        return locationIP;
    }

    public void setLocationIP(String locationIP) {
        this.locationIP = locationIP;
    }

    public String getBrowserUsed() {
        return browserUsed;
    }

    public void setBrowserUsed(String browserUsed) {
        this.browserUsed = browserUsed;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Set<Emails> getEmailsSet() {
        return emailsSet;
    }

    public void setEmailsSet(Set<Emails> emailsSet) {
        this.emailsSet = emailsSet;
    }

    public Set<Speaks> getSpeaksSet() {
        return speaksSet;
    }

    public void setSpeaksSet(Set<Speaks> speaksSet) {
        this.speaksSet = speaksSet;
    }

    public Set<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public Set<PersonLikesComment> getPerson_likes_commentSet() {
        return person_likes_commentSet;
    }

    public void setPerson_likes_commentSet(Set<PersonLikesComment> person_likes_commentSet) {
        this.person_likes_commentSet = person_likes_commentSet;
    }

    public Set<PersonLikePost> getPersonLikePostSet() {
        return personLikePostSet;
    }

    public void setPersonLikePostSet(Set<PersonLikePost> personLikePostSet) {
        this.personLikePostSet = personLikePostSet;
    }

    public Set<PersonKnowsPerson> getPersonKnowsPersonSet() {
        return personKnowsPersonSet;
    }

    public void setPersonKnowsPersonSet(Set<PersonKnowsPerson> personKnowsPersonSet) {
        this.personKnowsPersonSet = personKnowsPersonSet;
    }

    public Set<WorkAt> getWorkAtSet() {
        return workAtSet;
    }

    public void setWorkAtSet(Set<WorkAt> workAtSet) {
        this.workAtSet = workAtSet;
    }

    public Set<StudyAt> getStudyAtSet() {
        return studyAtSet;
    }

    public void setStudyAtSet(Set<StudyAt> studyAtSet) {
        this.studyAtSet = studyAtSet;
    }
}
