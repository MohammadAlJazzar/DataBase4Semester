package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Entity implementation class for Entity: Speaks
 */
@Entity
@Table(name = "speaks")

public class Speaks {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long speaksId;

    @Column(columnDefinition = "varchar(255)", nullable = false)
    @CsvBindByName(column = "language")
    private String speaks;

    @ManyToOne
    @JoinColumn(name = "person", nullable = false)
    private Person person;

    @Transient
    @CsvBindByName(column = "Person.id")
    private long personId;

    public Speaks(String speaks, Person person) {
        this.speaks = speaks;
        this.person = person;
    }

    public Speaks(long speaksId, String speaks, Person person) {
        this.speaksId = speaksId;
        this.speaks = speaks;
        this.person = person;
    }

    public Speaks(String speaks, long personId) {
        this.speaks = speaks;
        this.personId = personId;
    }

    public Speaks() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Speaks> beans = new CsvToBeanBuilder<Speaks>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Speaks.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            for (int i = 0; i < beans.size(); i++) {
                Speaks speaks = beans.get(i);
                Person person = session.load(Person.class, speaks.personId);
                speaks.setPerson(person);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(speaks);
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

    public long getSpeaksId() {
        return speaksId;
    }

    public void setSpeaksId(long speaksId) {
        this.speaksId = speaksId;
    }

    public String getSpeaks() {
        return speaks;
    }

    public void setSpeaks(String speaks) {
        this.speaks = speaks;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
