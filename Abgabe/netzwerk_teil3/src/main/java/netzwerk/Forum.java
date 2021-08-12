package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity implementation class for Entity: Forum
 */
@Entity
public class Forum {

    @Id
    @Column(columnDefinition = "bigserial")
    @CsvBindByName(column = "id")
    private long forumID;

    @Column(columnDefinition = "varchar(55)")
    @CsvBindByName(column = "title")
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @CsvBindByName(column = "creationDate")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date creationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moderatorId")
    private Person person;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "forum_hasTag")
    private Set<Tag> tagSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forum")
    private Set<Forum_hasMember> forum_hasMemberSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "forumId", nullable = false)
    private Set<Post> postSet;

    @Transient
    @CsvBindByName(column = "moderator")
    private long moderatorId;

    public Forum(long forumID, String title, Date creationDate, Person person) {
        this.forumID = forumID;
        this.title = title;
        this.creationDate = creationDate;
        this.person = person;
    }

    public Forum(long forumID, String title, Date creationDate, long moderatorId) {
        this.forumID = forumID;
        this.title = title;
        this.creationDate = creationDate;
        this.moderatorId = moderatorId;
    }

    public Forum() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Forum> beans = new CsvToBeanBuilder<Forum>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Forum.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            for (int i = 0; i < beans.size(); i++) {
                Forum forum = beans.get(i);
                Person person = session.load(Person.class, forum.moderatorId);
                forum.setPerson(person);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(forum);
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

    public void insertHasTagFromCSV(String path, char splitBy) throws IOException {
        try {
            try (CSVReader reader = new CSVReader(new FileReader(path))) {
                reader.skip(1);
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    Long forumId = Long.parseLong(lineInArray[0].split("\\|")[0]);
                    int tagId = Integer.parseInt(lineInArray[0].split("\\|")[1]);
                    addTag(forumId, tagId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTag(Long forumId, int tagId) {
        Session session = null;
        try {
            session = MainExecutor.getSession();
            Tag tag = session.load(Tag.class, tagId);
            Forum forum = session.load(Forum.class, forumId);
            forum.getTagSet().add(tag);
            org.hibernate.Transaction tr = session.beginTransaction();
            session.update(forum);
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public long getForumID() {
        return forumID;
    }

    public void setForumID(long forumID) {
        this.forumID = forumID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Set<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public Set<Forum_hasMember> getForum_hasMemberSet() {
        return forum_hasMemberSet;
    }

    public void setForum_hasMemberSet(Set<Forum_hasMember> forum_hasMemberSet) {
        this.forum_hasMemberSet = forum_hasMemberSet;
    }

    public Set<Post> getPostSet() {
        return postSet;
    }

    public void setPostSet(Set<Post> postSet) {
        this.postSet = postSet;
    }
}
