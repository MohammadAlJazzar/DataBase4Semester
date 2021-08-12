package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Forum_MemberId implements Serializable {

    private long personId;
    private long forumId;

    public Forum_MemberId(long personId, long forumId) {
        this.personId = personId;
        this.forumId = forumId;
    }

    public Forum_MemberId() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }
}
