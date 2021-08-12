package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class IssubClassOfId implements Serializable {

    private int superClassId;

    private int subClassId;

    public IssubClassOfId(int superClassId, int subClassId) {
        this.superClassId = superClassId;
        this.subClassId = subClassId;
    }

    public IssubClassOfId() {
    }

    public int getSuperClassId() {
        return superClassId;
    }

    public void setSuperClassId(int superClassId) {
        this.superClassId = superClassId;
    }

    public int getSubClassId() {
        return subClassId;
    }

    public void setSubClassId(int subClassId) {
        this.subClassId = subClassId;
    }
}