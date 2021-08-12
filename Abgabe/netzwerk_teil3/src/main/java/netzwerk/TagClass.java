package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Entity
public class TagClass {
    @Id
    @Column(columnDefinition = "serial")
    @CsvBindByName(column = "id")
    private int tagClassID;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @CsvBindByName(column = "name")
    private String tClassName;

    @Column(nullable = false, columnDefinition = "varchar(2048)")
    @CsvBindByName(column = "url")
    private String url;

    @OneToMany(mappedBy = "supClass")
    private Set<IsSubClassOF> isSubClassOFSet = new HashSet<>();

    public TagClass(int tagClassID, String tClassName, String url) {
        this.tagClassID = tagClassID;
        this.tClassName = tClassName;
        this.url = url;
    }

    public TagClass() {
    }

    public String toTree() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "0", "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix, String x) {
        buffer.append(prefix);
        buffer.append(" " + this.gettClassName());
        buffer.append('\n');
        for (Iterator<IsSubClassOF> it = isSubClassOFSet.iterator(); it.hasNext(); ) {
            List<IsSubClassOF> list = new ArrayList<>(this.isSubClassOFSet);

            IsSubClassOF isSubClassOF = it.next();
            int i = list.indexOf(isSubClassOF) + 1;

            TagClass next = isSubClassOF.getSubClass();
            if (it.hasNext()) {
                x = "0." + i;
                next.print(buffer, x, "", x);
            } else {
                x = x + "." + i;
                next.print(buffer, x, x + ".1", x);
            }
        }
    }

//    private void print(StringBuilder buffer, String prefix, String childrenPrefix, String x) {
//        buffer.append("0" + x + " " + this.gettClassName());
//        buffer.append('\n');
//        for (Iterator<IsSubClassOF> it = isSubClassOFSet.iterator(); it.hasNext(); ) {
//            List<IsSubClassOF> list = new ArrayList<>(this.isSubClassOFSet);
//            IsSubClassOF isSubClassOF = it.next();
//            int i = list.indexOf(isSubClassOF) + 1;
//            TagClass next = isSubClassOF.getSubClass();
//            if (it.hasNext()) {
//                x = "." + i;
//                next.print(buffer, childrenPrefix, childrenPrefix, x);
//            } else {
//                x = x + "." + i;
//                next.print(buffer, childrenPrefix, childrenPrefix, x);
//            }
//        }
//    }

    public int getTagClassID() {
        return tagClassID;
    }

    public void setTagClassID(int tagClassID) {
        this.tagClassID = tagClassID;
    }

    public String gettClassName() {
        return tClassName;
    }

    public void settClassName(String tClassName) {
        this.tClassName = tClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<IsSubClassOF> getIsSubClassOFSet() {
        return isSubClassOFSet;
    }

    public void setIsSubClassOFSet(Set<IsSubClassOF> isSubClassOFSet) {
        this.isSubClassOFSet = isSubClassOFSet;
    }

    /**
     * @param path
     * String: Das Pfad von der Csv-Datei
     *
     * @param splitBy char: Bestimmt welches Zeichen wurde benutzt um zwischen den Spalten zu unterscheiden
     *
     */
    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<TagClass> beans = new CsvToBeanBuilder<TagClass>(new FileReader(path)).withSeparator(splitBy)
                    .withType(TagClass.class)
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

    public TagClass getObject(int id) {
        Session session = null;
        TagClass result = null;
        try {
            session = MainExecutor.getSession();
            result = session.load(TagClass.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return result;
    }
}
