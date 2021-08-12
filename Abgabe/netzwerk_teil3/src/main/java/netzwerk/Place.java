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
 * Entity implementation class for Entity: Place
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Place {

    @Id
    @Column(columnDefinition = "serial")
    @CsvBindByName(column = "id")
    private Integer placeID;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "url")
    @Column(name = "url", columnDefinition = "varchar(2048)")
    private String url;

    @Transient
    @CsvBindByName(column = "type")
    private String type;

    @Transient
    @CsvBindByName(column = "isPartOf")
    private Integer isPartOf;

    @Transient
    private static final String COUNTRY = "country";

    @Transient
    private static final String CITY = "city";

    @Transient
    private static final String CONTINENT = "continent";


    public Place(Integer placeID, String name, String url, String type, Integer isPartOf) {
        this.placeID = placeID;
        this.name = name;
        this.url = url;
        this.type = type;
        this.isPartOf = isPartOf;
    }

    public Place(Integer placeID, String name, String url) {
        this.placeID = placeID;
        this.name = name;
        this.url = url;
    }

    public Place(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Place() {
    }

    public Integer getPlaceID() {
        return placeID;
    }

    public void setPlaceID(Integer placeID) {
        this.placeID = placeID;
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

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Place> beans = new CsvToBeanBuilder<Place>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Place.class)
                    .build()
                    .parse();

            List<Place> continents = beans.stream().filter(place -> place.type.equals(CONTINENT)).collect(Collectors.toList());
            List<Place> countries = beans.stream().filter(place -> place.type.equals(COUNTRY)).collect(Collectors.toList());
            List<Place> cities = beans.stream().filter(place -> place.type.equals(CITY)).collect(Collectors.toList());

            session = MainExecutor.getSession();
            for (int i = 0; i < continents.size(); i++) {
                Place place = continents.get(i);
                Continent continent = new Continent(place.placeID, place.name, place.url);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(continent);
                tr.commit();
            }

            for (int i = 0; i < countries.size(); i++) {
                Place place = countries.get(i);
                Continent continent = session.load(Continent.class, place.isPartOf);
                Country country = new Country(place.placeID, place.name, place.url, continent);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(country);
                tr.commit();
            }

            for (int i = 0; i < cities.size(); i++) {
                Place place = cities.get(i);
                Country country = session.load(Country.class, place.isPartOf);
                City city = new City(place.placeID, place.name, place.url, country);
                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(city);
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


}
