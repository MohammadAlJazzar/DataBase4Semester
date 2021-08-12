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
 * Entity implementation class for Entity: WorkAT
 */
@Entity

public class WorkAt {

    @EmbeddedId
    private WorkAtIdClass workAtId = new WorkAtIdClass();

    @ManyToOne
    @MapsId("personId")
    private Person person;

    @ManyToOne
    @MapsId("companyId")
    private Company company;

    @CsvBindByName(column = "workFrom")
    private long workFrom;

    @Transient
    @CsvBindByName(column = "Person.id")
    private Long personId;

    @Transient
    @CsvBindByName(column = "Organisation.id")
    private int comId;

    public WorkAt(Person person, Company company, long workFrom) {
        this.person = person;
        this.company = company;
        this.workFrom = workFrom;
    }

    public WorkAt(Long personId, int comId, long workFrom) {
        this.workFrom = workFrom;
        this.personId = personId;
        this.comId = comId;
    }

    public WorkAt() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        List<WorkAt> beans = new CsvToBeanBuilder<WorkAt>(new FileReader(path)).withSeparator(splitBy)
                .withType(WorkAt.class)
                .build()
                .parse();

        beans.forEach(WorkAt::add);
    }

    public void add() {
        Session session = null;
        try {
            session = MainExecutor.getSessionFactory().openSession();
            Person person = session.load(Person.class, this.personId);
            Company company = session.load(Company.class, this.comId);
            WorkAt workAt = new WorkAt(person, company, this.workFrom);
            org.hibernate.Transaction tr = session.beginTransaction();
            session.save(workAt);
            tr.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public WorkAtIdClass getWorkAtId() {
        return workAtId;
    }

    public Person getPerson() {
        return person;
    }

    public Company getCompany() {
        return company;
    }

    public long getWorkFrom() {
        return workFrom;
    }
}
