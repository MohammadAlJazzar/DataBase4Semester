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
 * Entity implementation class for Entity: StudyAT
 */
@Entity
@Table(name = "studyAT")

public class StudyAt {

    @EmbeddedId
    private StudyAtId studyAtId = new StudyAtId();

    @ManyToOne
    @MapsId("personId")
    private Person person;

    @ManyToOne
    @MapsId("uniID")
    private University university;

    @Transient
    @CsvBindByName(column = "Person.id")
    private Long personId;

    @Transient
    @CsvBindByName(column = "Organisation.id")
    private int uniId;

    @CsvBindByName(column = "classYear")
    private long classYear;


    public StudyAt(Person person, University university, long classYear) {
        this.person = person;
        this.university = university;
        this.classYear = classYear;
    }

    public StudyAt(long classYear, Long personId, int uniId) {
        this.classYear = classYear;
        this.personId = personId;
        this.uniId = uniId;
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        List<StudyAt> beans = new CsvToBeanBuilder<StudyAt>(new FileReader(path)).withSeparator(splitBy)
                .withType(StudyAt.class)
                .build()
                .parse();

        beans.forEach(StudyAt::add);
    }

    public void add() {
        Session session = null;
        try {
            session = MainExecutor.getSessionFactory().openSession();
            Person person = session.load(Person.class, this.personId);
            University university = session.load(University.class, this.uniId);
            StudyAt studyAt = new StudyAt(person, university,this.classYear);
            org.hibernate.Transaction tr = session.beginTransaction();
            session.save(studyAt);
            tr.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public StudyAt() {
    }

    public StudyAtId getStudyAtId() {
        return studyAtId;
    }

    public void setStudyAtId(StudyAtId workAtId) {
        this.studyAtId = workAtId;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public long getClassYear() {
        return classYear;
    }

    public void setClassYear(long classYear) {
        this.classYear = classYear;
    }
}
