package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class personLikePostId implements Serializable {

    private long personId;
    private long postId;

    public personLikePostId(long personId, long postIdId) {
        this.personId = personId;
        this.postId = postId;
    }

    public personLikePostId() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getPostIdId() {
        return postId;
    }

    public void setPostIdId(long messageId) {
        this.postId= messageId;
    }
}
