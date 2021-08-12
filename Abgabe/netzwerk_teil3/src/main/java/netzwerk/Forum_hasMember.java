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

@Entity
public class Forum_hasMember {

    @EmbeddedId
    private Forum_MemberId id = new Forum_MemberId();

    @ManyToOne
    @MapsId("personId")
    private Person person;

    @ManyToOne
    @MapsId("forumId")
    private Forum forum;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date joinDate;

    @Transient
    @CsvBindByName(column = "Forum.id")
    private Long forumId;

    @Transient
    @CsvBindByName(column = "Person.id")
    private Long personId;

    @Transient
    @CsvBindByName(column = "joinDate")
    @CsvDate(value = "yyyy-MM-dd")
    private Date joinDateTemp;

    public Forum_hasMember(Person person, Forum forum, Date joinDate) {
        this.person = person;
        this.forum = forum;
        this.joinDate = joinDate;
    }

    public Forum_hasMember(Long personId, Long forumId, Date joinDateTemp) {
        this.forumId = forumId;
        this.personId = personId;
        this.joinDateTemp = joinDateTemp;
    }

    public Forum_hasMember() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        List<Forum_hasMember> beans = new CsvToBeanBuilder<Forum_hasMember>(new FileReader(path)).withSeparator(splitBy)
                .withType(Forum_hasMember.class)
                .build()
                .parse();
        beans.forEach(Forum_hasMember::add);
    }

    public void add() {
        Session session = null;
        try {
            session = MainExecutor.getSession();
            Person person = session.load(Person.class, this.personId);
            Forum forum = session.load(Forum.class, this.forumId);
            this.person = person;
            this.forum = forum;
            this.joinDate = joinDateTemp;
            Transaction transaction = session.beginTransaction();
            session.save(this);
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

    public void setPerson(Person person) {
        this.person = person;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }


}

