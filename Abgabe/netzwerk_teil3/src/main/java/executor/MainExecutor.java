package executor;

import netzwerk.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import personenbezogene.PersonRelatedImpl;
import statistik.StatisticImpl;
import java.io.IOException;


public class MainExecutor {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    public static void main(String[] args) throws IOException {

        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate-firma.cfg.xml").build();
        Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
        sessionFactory = metaData.getSessionFactoryBuilder().build();

        /**
         * Auskommentiern Sie nächtse Zeile, um die csv-Datei in dem Datenbank zu importieren
         * */
//        insertAllCSV();

       
        StatisticImpl statistic = new StatisticImpl();
        PersonRelatedImpl personRelated = new PersonRelatedImpl();
        
         /**
         *Schreiben Sie hier den Aufruf der Methode/Methoden, die Sie testen wollen.
         * */

        /**
         * Beispiele
         * */

        personRelated.getShorthestFriendshipPath(94L,96L);
//        statistic.getPopularComments(5);
//        statistic.getCountryWithMostComment_Post();
//
//        personRelated.getProfile(10995116277764L);
//        personRelated.getJobRecommendation(10995116277764L);
//        personRelated.getCommonFriends(10995116277764L,3298534883405L);

        System.out.println("Successfully inserted");
    }


    /**
     * Diese Mehtode dient zu insert all csv Dateien in der Datenbank
     * Die Csv Datein finden Sie in dem Ordner social_network in dem root Verzeichnis des Projekts
     * */
    private static void insertAllCSV() throws IOException {
        new TagClass().insertFromCSV("social_network\\tagclass_0_0.csv", '|');
        new Tag().insertFromCSV("social_network\\tag_0_0.csv", '|');
        new IsSubClassOF().insertFromCSV("social_network\\tagclass_isSubclassOf_tagclass_0_0.csv", '|');
        new Tag().insertHasTypeFromCSV("social_network\\tag_hasType_tagclass_0_0.csv", '|');
        new Place().insertFromCSV("social_network\\place_0_0.csv", '|');
        new Person().insertFromCSV("social_network\\person_0_0.csv", '|');
        new Emails().insertFromCSV("social_network\\person_email_emailaddress_0_0.csv", '|');
        new Speaks().insertFromCSV("social_network\\person_speaks_language_0_0.csv", '|');
        new Forum().insertFromCSV("social_network\\forum_0_0.csv", '|');
        new Post().insertFromCSV("social_network\\post_0_0.csv", '|');
        new Comment().insertFromCSV("social_network\\comment_0_0.csv", '|');
        new Post().insertHasTagFromCSV("social_network\\post_hasTag_tag_0_0.csv", '|');
        new Forum().insertHasTagFromCSV("social_network\\forum_hasTag_tag_0_0.csv", '|');
        new Forum_hasMember().insertFromCSV("social_network\\forum_hasMember_person_0_0.csv", '|');
        new PersonLikePost().insertFromCSV("social_network\\person_likes_post_0_0.csv", '|');
        new PersonLikesComment().insertFromCSV("social_network\\person_likes_comment_0_0.csv", '|');
        new PersonKnowsPerson().insertFromCSV("social_network\\person_knows_person_0_0.csv", '|');
        new Person().insertHasInterestFromCSV("social_network\\person_hasInterest_tag_0_0.csv", '|');
        new Organisation().insertFromCSV("social_network\\organisation_0_0.csv", '|');
        new StudyAt().insertFromCSV("social_network\\person_studyAt_organisation_0_0.csv", '|');
        new WorkAt().insertFromCSV("social_network\\person_workAt_organisation_0_0.csv", '|');
    }
}
