package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class StudyAtId implements Serializable {

    private long personId;
    private int uniID;

}
