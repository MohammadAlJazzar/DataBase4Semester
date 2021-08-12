package netzwerk;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity implementation class for Entity: Company
 *
 */
@Entity
public class Company extends Organisation  {

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "countryId")
	private Country country;

	public Company(int organisationID, String name, String url, Country country) {
		super(organisationID, name, url);
		this.country = country;
	}

	public Company(int organisationID, String name, String url) {
		super(organisationID, name, url);
	}

	public Company() {
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
