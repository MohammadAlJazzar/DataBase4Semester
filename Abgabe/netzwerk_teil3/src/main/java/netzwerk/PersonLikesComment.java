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
 * Entity implementation class for Entity: Person_likes_Message
 */
@Entity
public class PersonLikesComment {

    @EmbeddedId
    private Person_commentID id = new Person_commentID();

    @ManyToOne
    @MapsId("personId")
    private Person person;

    @ManyToOne
    @MapsId("commentId")
    private Comment comment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

	@Transient
	@CsvBindByName(column = "Comment.id")
	private Long commetId;

	@Transient
	@CsvBindByName(column = "Person.id")
	private Long personId;

	@Transient
	@CsvBindByName(column = "creationDate")
	@CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date creationDateTemp;

    public PersonLikesComment(Person person, Comment comment, Date creationDate) {
        this.person = person;
        this.comment = comment;
        this.creationDate = creationDate;
    }

	public PersonLikesComment(Long commetId, Long personId, Date creationDateTemp) {
		this.commetId = commetId;
		this.personId = personId;
		this.creationDateTemp = creationDateTemp;
	}

	public PersonLikesComment() {
    }

	public void insertFromCSV(String path, char splitBy) throws IOException {
		List<PersonLikesComment> beans = new CsvToBeanBuilder<PersonLikesComment>(new FileReader(path)).withSeparator(splitBy)
				.withType(PersonLikesComment.class)
				.build()
				.parse();
		beans.forEach(PersonLikesComment::add);
	}

	public void add() {
		Session session = null;
		try {
			session = MainExecutor.getSession();
			Person person = session.load(Person.class, this.personId);
			Comment comment = session.load(Comment.class, this.commetId);
			this.person = person;
			this.comment = comment;
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

    public Person_commentID getId() {
        return id;
    }

    public void setId(Person_commentID id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}


