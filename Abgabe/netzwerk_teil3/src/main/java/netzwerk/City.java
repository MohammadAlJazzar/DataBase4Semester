package netzwerk;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: City
 *
 */
@Entity
@Table(name="city")
public class City extends Place {


	@ManyToOne
	@JoinColumn(name="isPartOf",nullable = false)
	private Country country;

	public City(Integer placeID, String name, String url, Country country) {
		super(placeID, name, url);
		this.country = country;
	}

	public City(String name, String url, Country country) {
		super(name, url);
		this.country = country;
	}

	public City() {
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
