package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import jakarta.validation.constraints.Email;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Entity implementation class for Entity: Emails
 */
@Entity
@Table(name = "emails")
public class Emails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "personID", nullable = false)
    private Person person;

    @Column(name = "email", nullable = false,unique = true)
    @Email
    @CsvBindByName(column = "email")
    private String email;

    @Transient
    @CsvBindByName(column = "Person.id")
    private long personId;

    public Emails(Person person, String email) {
        this.person = person;
        this.email = email;
    }

    public Emails(long id, Person person, String email) {
        this.id = id;
        this.person = person;
        this.email = email;
    }

    public Emails(@Email String email, long personId) {
        this.email = email;
        this.personId = personId;
    }

    public Emails() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Emails> beans = new CsvToBeanBuilder<Emails>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Emails.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            for (int i = 0; i < beans.size(); i++) {
                Emails emails = beans.get(i);
                Person person = session.load(Person.class, emails.personId);
                emails.setPerson(person);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(emails);
                tr.commit();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

}
