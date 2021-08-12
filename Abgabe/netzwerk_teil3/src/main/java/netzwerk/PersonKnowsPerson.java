package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Entity implementation class for Entity: person_knows_Person
 */
@Entity
public class PersonKnowsPerson {

    @EmbeddedId
    private PersonKnowsPersonId personKnowsPersonId = new PersonKnowsPersonId();

    @ManyToOne
    @MapsId("superPersonId")
    private Person person;

    @ManyToOne()
    @MapsId("subPersonId")
    private Person subPerson;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @Transient
    @CsvBindByName(column = "PersonF.id")
    private Long personFId;

    @Transient
    @CsvBindByName(column = "PersonL.id")
    private Long personLId;

    @Transient
    @CsvBindByName(column = "creationDate")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date creationDateTemp;

    public PersonKnowsPerson(Person superPerson, Person subPerson, Date creationDate) {
        this.person = superPerson;
        this.subPerson = subPerson;
        this.creationDate = creationDate;
    }

    public PersonKnowsPerson(Long personFId, Long personLId, Date creationDateTemp) {
        this.personFId = personFId;
        this.personLId = personLId;
        this.creationDateTemp = creationDateTemp;
    }

    public PersonKnowsPerson() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        List<PersonKnowsPerson> beans = new CsvToBeanBuilder<PersonKnowsPerson>(new FileReader(path)).withSeparator(splitBy)
                .withType(PersonKnowsPerson.class)
                .build()
                .parse();
        beans.forEach(PersonKnowsPerson::add);
    }

    public void add() {
        Session session = null;
        try {
            session = MainExecutor.getSession();
            Person personF = session.load(Person.class, this.personFId);
            Person personL = session.load(Person.class, this.personLId);
            this.subPerson = personF;
            this.person = personL;
            this.creationDate = creationDateTemp;
            Transaction transaction = session.beginTransaction();
            PersonKnowsPerson personKnowsPerson = new PersonKnowsPerson();
            personKnowsPerson.setSubPerson(personL);
            personKnowsPerson.setPerson(personF);
            personKnowsPerson.setCreationDate(creationDateTemp);
            session.save(this);
            session.save(personKnowsPerson);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person superPerson) {
        this.person = superPerson;
    }

    public Person getSubPerson() {
        return subPerson;
    }

    public void setSubPerson(Person subPerson) {
        this.subPerson = subPerson;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}


