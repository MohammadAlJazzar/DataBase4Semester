package netzwerk;

import javax.persistence.Entity;


/**
 * Entity implementation class for Entity: Continent
 */
@Entity
public class Continent extends Place {

    public Continent(Integer placeID, String name, String url) {
        super(placeID, name, url);
    }

    public Continent() {
    }
}
