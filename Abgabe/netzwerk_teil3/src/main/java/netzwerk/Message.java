package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity implementation class for Entity: Message
 */
@MappedSuperclass
public abstract class Message {

    @Column(columnDefinition = "varchar(255)")
    @CsvBindByName(column = "content")
    protected String content;

    @CsvBindByName(column = "length")
    protected int length;

    @Temporal(TemporalType.DATE)
    @CsvBindByName(column = "creationDate")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    protected Date creationDate;

    @Column(columnDefinition = "varchar(55)")
    @CsvBindByName(column = "browserUsed")
    protected String browserused;

    @Column(columnDefinition = "varchar(55)")
    @CsvBindByName(column = "locationIP")
    protected String locationIP;

    @ManyToOne(cascade = CascadeType.ALL)
    protected Country isLocatedIn;

    @ManyToOne
    @JoinColumn(name = "hasCreator")
    protected Person person;

    @Transient
    @CsvBindByName(column = "creator")
    protected long creatorId;

    @Transient
    @CsvBindByName(column = "place")
    protected int countryId;

    public Message(String content, int length, Date creationDate, String browserused, String locationIP, Country isLocatedIn, Person person) {
        this.content = content;
        this.length = length;
        this.creationDate = creationDate;
        this.browserused = browserused;
        this.locationIP = locationIP;
        this.isLocatedIn = isLocatedIn;
        this.person = person;
    }

    public Message(String content, int length, Date creationDate, String browserused, String locationIP, long creatorId, int countryId) {
        this.content = content;
        this.length = length;
        this.creationDate = creationDate;
        this.browserused = browserused;
        this.locationIP = locationIP;
        this.creatorId = creatorId;
        this.countryId = countryId;
    }

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getBrowserused() {
        return browserused;
    }

    public void setBrowserused(String browserused) {
        this.browserused = browserused;
    }

    public String getLocationIP() {
        return locationIP;
    }

    public void setLocationIP(String locationIP) {
        this.locationIP = locationIP;
    }

    public Country getIsLocatedIn() {
        return isLocatedIn;
    }

    public void setIsLocatedIn(Country isLocatedIn) {
        this.isLocatedIn = isLocatedIn;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
