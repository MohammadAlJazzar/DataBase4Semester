package netzwerk;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity implementation class for Entity: University
 *
 */
@Entity
public class University extends Organisation {

	@ManyToOne
	@JoinColumn(name = "cityID")
	private City city;

	public University(int organisationID, String name, String url, City city) {
		super(organisationID, name, url);
		this.city = city;
	}

	public University(int organisationID, String name, String url) {
		super(organisationID, name, url);
	}

	public University() { }

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
}
