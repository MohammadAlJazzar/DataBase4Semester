package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@SuppressWarnings("bigserial")
@Embeddable
public class PersonKnowsPersonId implements Serializable {

    private long superPersonId;
    private long subPersonId;



}
