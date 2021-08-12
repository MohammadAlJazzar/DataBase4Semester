package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class WorkAtIdClass implements Serializable {

    private long personId;
    private int companyId;

    public WorkAtIdClass(long personId, int companyId) {
        this.personId = personId;
        this.companyId = companyId;
    }

    public WorkAtIdClass() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
}
