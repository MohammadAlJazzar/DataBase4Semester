package netzwerk;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Person_commentID implements Serializable {

    private long personId;
    private long commentId;

    public Person_commentID(long personId, long commentId) {
        this.personId = personId;
        this.commentId = commentId;
    }

    public Person_commentID() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}
