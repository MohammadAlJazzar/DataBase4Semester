package netzwerk;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 * Entity implementation class for Entity: Country
 */
@Entity
public class Country extends Place {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "isPartOf", nullable = false)
    private Continent continent;

    public Country(Integer placeID, String name, String url, Continent continent) {
        super(placeID, name, url);
        this.continent = continent;
    }

    public Country(String name, String url, Continent continent) {
        super(name, url);
        this.continent = continent;
    }

    public Country() {
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }
}
