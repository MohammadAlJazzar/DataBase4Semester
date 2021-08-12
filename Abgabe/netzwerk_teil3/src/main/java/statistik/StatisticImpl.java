package statistik;

import executor.MainExecutor;
import netzwerk.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author Mohammad Ghaith Albaba
 * @author Hasham
 * @author Mohammad Al Jazzar
 * @project db-teil3
 */

public class StatisticImpl implements StatisticAPI {

    @Override
    public void getPopularComments(Integer k) {
        Session session = MainExecutor.getSession();
        String hql = "FROM Comment C WHERE C.person_likes_commentSet.size >=" + k;
        Query query = session.createQuery(hql, Comment.class);
        List<Comment> results = query.list();

        results.forEach(comment -> System.out.println(
                "ID: " + comment.getCommentID() + " Creator: " + comment.getPerson().getFirstName() + " " + comment.getPerson().getLastName()
        ));
        session.close();
    }

    @Override
    public void getCountryWithMostComment_Post() {
        Session session = MainExecutor.getSession();

        List<Comment> comments = session.createQuery("from Comment ", Comment.class).list();
        List<Post> posts = session.createQuery("from Post ", Post.class).list();

        List<Message> messages = new ArrayList<>();

        messages.addAll(comments);
        messages.addAll(posts);

        Country country = messages.stream().filter(message -> Objects.nonNull(message.getIsLocatedIn()))
                .collect(Collectors.groupingBy(Message::getIsLocatedIn, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        System.out.println(country.getName());

        session.close();
    }

    @Override
    public void getTagClassHierarchy(int tagClassId) {
        Session session = MainExecutor.getSession();
        TagClass tagClass = session.load(TagClass.class, tagClassId);

        System.out.println(tagClass.toTree());

        session.close();
    }
}
