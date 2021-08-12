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
public class PersonLikePost {

    @EmbeddedId
    private personLikePostId id=new personLikePostId();

    @ManyToOne
    @MapsId("personId")
    private Person person;

    @ManyToOne
    @MapsId("postId")
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @Transient
    @CsvBindByName(column = "Post.id")
    private Long postId;

    @Transient
    @CsvBindByName(column = "Person.id")
    private Long personId;

    @Transient
    @CsvBindByName(column = "creationDate")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date creationDateTemp;


    public PersonLikePost(Person person, Post post, Date creationDate) {
        this.person = person;
        this.post = post;
        this.creationDate = creationDate;
    }

    public PersonLikePost(Long postId, Long personId, Date creationDateTemp) {
        this.postId = postId;
        this.personId = personId;
        this.creationDateTemp = creationDateTemp;
    }

    public PersonLikePost() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        List<PersonLikePost> beans = new CsvToBeanBuilder<PersonLikePost>(new FileReader(path)).withSeparator(splitBy)
                .withType(PersonLikePost.class)
                .build()
                .parse();
        beans.forEach(PersonLikePost::add);
    }

    public void add() {
        Session session = null;
        try {
            session = MainExecutor.getSession();
            Person person = session.load(Person.class, this.personId);
            Post post = session.load(Post.class, this.postId);
            this.person = person;
            this.post = post;
            this.creationDate = creationDateTemp;
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

    public personLikePostId getId() {
        return id;
    }

    public void setId(personLikePostId id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}



