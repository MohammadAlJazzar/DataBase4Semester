package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvBindByName;
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

@Entity
public class Post extends Message {

    @Id
    @Column(columnDefinition = "bigserial")
    @CsvBindByName(column = "id")
    private long postId;

    @CsvBindByName(column = "imageFile")
    private String imageFile;

    @CsvBindByName(column = "language")
    private String language;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private Set<PersonLikePost> personLikePostSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "post_hasTag")
    private Set<Tag> tagSet = new HashSet<>();

    @Transient
    @CsvBindByName(column = "Forum.id")
    private long forumId;


    public Post(String content, int length, Date creationDate, String browserused, String locationIP, Country isLocatedIn, Person person, long postId, String imageFile, String language) {
        super(content, length, creationDate, browserused, locationIP, isLocatedIn, person);
        this.postId = postId;
        this.imageFile = imageFile;
        this.language = language;
    }

    public Post(String content, int length, Date creationDate, String browserused, String locationIP, long creatorId, int countryId, long postId, String imageFile, String language, long forumId) {
        super(content, length, creationDate, browserused, locationIP, creatorId, countryId);
        this.postId = postId;
        this.imageFile = imageFile;
        this.language = language;
        this.forumId = forumId;
    }

    public Post() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Post> beans = new CsvToBeanBuilder<Post>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Post.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            for (int i = 0; i < beans.size(); i++) {
                Post post = beans.get(i);
                Person person = session.load(Person.class, post.creatorId);
                Country country = session.load(Country.class, post.countryId);
                Forum forum = session.load(Forum.class, post.forumId);
                post.setPerson(person);
                post.setIsLocatedIn(country);
                forum.getPostSet().add(post);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(post);
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
                    Long postId = Long.parseLong(lineInArray[0].split("\\|")[0]);
                    int tagId = Integer.parseInt(lineInArray[0].split("\\|")[1]);
                    addTag(postId,tagId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTag(Long postId, int tagId) {
        Session session = null;
        try {
            session = MainExecutor.getSession();Tag tag = session.load(Tag.class,tagId);
            Post post = session.load(Post.class,postId);
            post.getTagSet().add(tag);
            org.hibernate.Transaction tr = session.beginTransaction();
            session.update(post);
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Set<PersonLikePost> getPersonLikePostSet() {
        return personLikePostSet;
    }

    public void setPersonLikePostSet(Set<PersonLikePost> personLikePostSet) {
        this.personLikePostSet = personLikePostSet;
    }

    public Set<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }
}
