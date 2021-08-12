package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity implementation class for Entity: Organisation
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Table(name = "Organisation")

public class Organisation {

    @Id
    @Column(columnDefinition = "serial")
    @CsvBindByName(column = "id")
    private int OrganisationID;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @CsvBindByName(column = "name")
    private String name;

    @Column(columnDefinition = "varchar(2048)")
    @CsvBindByName(column = "url")
    private String url;

    @Transient
    @CsvBindByName(column = "type")
    private String type;

    @Transient
    @CsvBindByName(column = "place")
    private Integer place;

    @Transient
    private static final String UNI = "university";

    @Transient
    private static final String COMPANY = "company";

    public Organisation(int organisationID, String name, String url, String type, Integer place) {
        OrganisationID = organisationID;
        this.name = name;
        this.url = url;
        this.type = type;
        this.place = place;
    }

    public Organisation(int organisationID, String name, String url) {
        OrganisationID = organisationID;
        this.name = name;
        this.url = url;
    }

    public Organisation() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Organisation> beans = new CsvToBeanBuilder<Organisation>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Organisation.class)
                    .build()
                    .parse();

            List<Organisation> companies = beans.stream().filter(organisation -> organisation.type.equals(COMPANY)).collect(Collectors.toList());
            List<Organisation> universities = beans.stream().filter(organisation -> organisation.type.equals(UNI)).collect(Collectors.toList());

            session = MainExecutor.getSession();

            for (int i = 0; i < companies.size(); i++) {
                Organisation organisation = companies.get(i);
                Country country = session.load(Country.class, organisation.place);
                Company company = new Company(organisation.OrganisationID, organisation.name, organisation.url);
                company.setCountry(country);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(company);
                tr.commit();
            }

            for (int i = 0; i < universities.size(); i++) {
                Organisation organisation = universities.get(i);
                City city = session.load(City.class, organisation.place);
                University university = new University(organisation.OrganisationID, organisation.name, organisation.url);
                university.setCity(city);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(university);
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


    public int getOrganisationID() {
        return OrganisationID;
    }

    public void setOrganisationID(int organisationID) {
        OrganisationID = organisationID;
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
}
