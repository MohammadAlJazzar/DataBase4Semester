package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Entity
public class IsSubClassOF {

    @EmbeddedId
    private IssubClassOfId id = new IssubClassOfId();

    @ManyToOne
    @JoinColumn(name = "supertagclass")
    @MapsId("superClassId")
    private TagClass supClass;

    @ManyToOne
    @JoinColumn(name = "subtagclass")
    @MapsId("subClassId")
    private TagClass subClass;

    @Transient
    @CsvBindByName(column = "supertagclass")
    private int spI;

    @Transient
    @CsvBindByName(column = "subtagclass")
    private int sbI;

    public IsSubClassOF(int spI, int sbI) {
        this.spI = spI;
        this.sbI = sbI;
    }

    public IsSubClassOF(TagClass supClass, TagClass subClass) {
        this.supClass = supClass;
        this.subClass = subClass;
    }

    public IsSubClassOF() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        List<IsSubClassOF> beans = new CsvToBeanBuilder<IsSubClassOF>(new FileReader(path)).withSeparator(splitBy)
                .withType(IsSubClassOF.class)
                .build()
                .parse();

        beans.forEach(IsSubClassOF::add);
    }

    public void add() {
        Session session = null;
        try {
            session = MainExecutor.getSessionFactory().openSession();
            TagClass spc = session.load(TagClass.class, this.spI);
            TagClass sbc = session.load(TagClass.class, this.sbI);
            Hibernate.initialize(spc);
            Hibernate.initialize(sbc);
            IsSubClassOF isSubClassOF = new IsSubClassOF(spc, sbc);
            org.hibernate.Transaction tr = session.beginTransaction();
            session.save(isSubClassOF);
            tr.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        //        TagClass sp = new TagClass(349,"3","3");
//        TagClass sb = new TagClass(211,"2","2");
//        IsSubClassOF isSubClassOF = new IsSubClassOF(sp,sb);
//        session.save(isSubClassOF);
    }

    public TagClass getSupClass() {
        return supClass;
    }

    public void setSupClass(TagClass supClass) {
        this.supClass = supClass;
    }

    public TagClass getSubClass() {
        return subClass;
    }

    public void setSubClass(TagClass subClass) {
        this.subClass = subClass;
    }
}
