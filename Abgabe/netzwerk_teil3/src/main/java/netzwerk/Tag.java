package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Tag {
    @Id
    @Column(columnDefinition = "serial")
    @CsvBindByName(column = "id")
    private int tagID;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @CsvBindByName(column = "name")
    private String name;

    @Column(columnDefinition = "varchar(2048)")
    @CsvBindByName(column = "url")
    private String url;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER, targetEntity = TagClass.class)
    @JoinTable(name = "tag_has_type")
    private Set<TagClass> tagsClasses = new HashSet<TagClass>();

    @ManyToMany(mappedBy = "tagSet", cascade = CascadeType.ALL)
    private Set<Post> postSet = new HashSet<>();

    public Tag(int tagID, String name, String url, Set<TagClass> tagsClasses, Set<Post> postSet) {
        this.tagID = tagID;
        this.name = name;
        this.url = url;
        this.tagsClasses = tagsClasses;
        this.postSet = postSet;
    }

    public Tag(int tagID, String name, String url) {
        this.tagID = tagID;
        this.name = name;
        this.url = url;
    }

    public Tag() {
    }

    public Set<TagClass> getTagsClasses() {
        return tagsClasses;
    }

    public void setPostSet(Set<Post> postSet) {
        this.postSet = postSet;
    }

    public void setOnePost(Post post) {
        this.getPostSet().add(post);
    }

    public void rmovePost(Post post) {
        this.getPostSet().remove(post);
    }

    public void clearPostSet() {
        this.getPostSet().clear();
    }

    public void setTagsClasses(Set<TagClass> tagsClasses) {
        this.tagsClasses = tagsClasses;
    }

    public void clearTagsClasses() {
        tagsClasses.clear();
    }

    public void addTagClass(TagClass t) {
        this.tagsClasses.add(t);
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Post> getPostSet() {
        return postSet;
    }

    public Tag getObject(int id) {
        Session session = null;
        Tag result = null;
        try {
            session = MainExecutor.getSession();
            result = session.load(Tag.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return result;
    }


    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Tag> beans = new CsvToBeanBuilder<Tag>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Tag.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            org.hibernate.Transaction tr = session.beginTransaction();
            beans.forEach(session::save);
            tr.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    public void insertHasTypeFromCSV(String path, char splitBy) throws IOException {
        try {
            try (CSVReader reader = new CSVReader(new FileReader(path))) {
                reader.skip(1);
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    int tagI = Integer.parseInt(lineInArray[0].split("\\|")[0]);
                    int tagClassI = Integer.parseInt(lineInArray[0].split("\\|")[1]);
                    addType(tagI, tagClassI);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addType(int tagId, int tagClassID) {
        Session session = null;
        Tag tag = getObject(tagId);
        TagClass tagClass = new TagClass().getObject(tagClassID);
        try {
            tag.getTagsClasses().add(tagClass);
            session = MainExecutor.getSession();
            org.hibernate.Transaction tr = session.beginTransaction();
            session.update(tag);
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
