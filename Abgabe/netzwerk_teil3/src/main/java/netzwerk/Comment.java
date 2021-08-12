package netzwerk;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import executor.MainExecutor;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//TODO: Nullable bestimmen in dem gasammten Projekt
@Entity
public class Comment extends Message {

    @Id
    @Column(columnDefinition = "bigserial")
    @CsvBindByName(column = "id")
    private Long commentID;

    @ManyToOne
    @JoinColumn(name = "replyOfPost", nullable = true)
    private Post replayOfPost;

    @ManyToOne
    @JoinColumn(name = "replyOfComment", nullable = true)
    private Comment replayOfComment;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Tag_hasComment", joinColumns = {@JoinColumn(name = "commentId")},
            inverseJoinColumns = {@JoinColumn(name = "TagId")})
    private Set<Tag> tagSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private Set<PersonLikesComment> person_likes_commentSet = new HashSet<>();

    @Transient
    @CsvBindByName(column = "replyOfComment")
    private Long replayOfCommitId;

    @Transient
    @CsvBindByName(column = "replyOfPost")
    private Long replayOfPostId;

    public Comment(String content, int length, Date creationDate, String browserused, String locationIP, Country isLocatedIn, Person person, Long commentID, Post replayOfPost, Comment replayOfComment) {
        super(content, length, creationDate, browserused, locationIP, isLocatedIn, person);
        this.commentID = commentID;
        this.replayOfPost = replayOfPost;
        this.replayOfComment = replayOfComment;
    }

    public Comment(String content, int length, Date creationDate, String browserused, String locationIP, long creatorId, int countryId, Long commentID, Long replayOfCommitId, Long replayOfPostId) {
        super(content, length, creationDate, browserused, locationIP, creatorId, countryId);
        this.commentID = commentID;
        this.replayOfCommitId = replayOfCommitId;
        this.replayOfPostId = replayOfPostId;
    }

    public Comment() {
    }

    public void insertFromCSV(String path, char splitBy) throws IOException {
        Session session = null;
        try {
            List<Comment> beans = new CsvToBeanBuilder<Comment>(new FileReader(path)).withSeparator(splitBy)
                    .withType(Comment.class)
                    .build()
                    .parse();
            session = MainExecutor.getSession();
            for (int i = 0; i < beans.size(); i++) {
                Comment comment = beans.get(i);

                Person person = session.load(Person.class, comment.creatorId);
                comment.setPerson(person);

                Country country = session.load(Country.class, comment.countryId);
                comment.setIsLocatedIn(country);

                if (comment.replayOfPostId != null) {
                    Post replayOfPost = session.load(Post.class, comment.replayOfPostId);
                    comment.setReplayOfPost(replayOfPost);
                }

                if (comment.replayOfCommitId != null) {
                    Comment replayOfComment = session.load(Comment.class, comment.replayOfCommitId);
                    comment.setReplayOfComment(replayOfComment);
                }

                org.hibernate.Transaction tr = session.beginTransaction();
                session.save(comment);
                tr.commit();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Long getCommentID() {
        return commentID;
    }

    public void setCommentID(Long commentID) {
        this.commentID = commentID;
    }

    public Post getReplayOfPost() {
        return replayOfPost;
    }

    public void setReplayOfPost(Post replayOfPost) {
        this.replayOfPost = replayOfPost;
    }

    public Comment getReplayOfComment() {
        return replayOfComment;
    }

    public void setReplayOfComment(Comment replayOfComment) {
        this.replayOfComment = replayOfComment;
    }

    public Set<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public Set<PersonLikesComment> getPerson_likes_commentSet() {
        return person_likes_commentSet;
    }

    public void setPerson_likes_commentSet(Set<PersonLikesComment> person_likes_commentSet) {
        this.person_likes_commentSet = person_likes_commentSet;
    }
}
