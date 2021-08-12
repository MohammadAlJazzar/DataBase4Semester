package personenbezogene;

import executor.MainExecutor;
import netzwerk.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PersonRelatedImpl implements PersonRelatedAPI {

    @Override
    public Person getProfile(Long personId) {
        Session session = MainExecutor.getSession();
        Person person = session.load(Person.class, personId);
        System.out.printf(
                "[ Id: %d, First name: %s, Last name: %s, Gender: %s, Birthday: %s, City: %s, Creation Date: %s ]%n",
                person.getPersonID(),
                person.getFirstName(),
                person.getLastName(),
                person.getGender(),
                person.getBirthday(),
                person.getCity().getName(),
                person.getCreationDate().toString())
        ;
        session.close();
        return person;
    }


    @Override
    public Set<Tag> getCommonInterestsOfMyFriends(Long personId) {
        Session session = MainExecutor.getSession();
        Person person = session.load(Person.class, personId);
        Set<Tag> commonTags = new HashSet<>(person.getTagSet());
        List<PersonKnowsPerson> a = new ArrayList<>(person.getPersonKnowsPersonSet());


        for (int i = 0; i < a.size(); i++) {
            Person nextPerson = a.get(i).getSubPerson();

            commonTags = commonTags.stream()
                    .distinct()
                    .filter(nextPerson.getTagSet()::contains)
                    .collect(Collectors.toSet());
        }

        if (commonTags.size() > 0) {
            commonTags.forEach(
                    tag -> System.out.printf(
                            " Id: %d Name: %s%n",
                            tag.getTagID(),
                            tag.getName()
                    )
            );
        } else {
            System.out.printf("Es gibt keine gemeinsame Tags zwischen den Freunden der Person >> %s %s << und der Person selbs.%n",
                    person.getFirstName(),
                    person.getLastName());
        }
        session.close();

        return commonTags;
    }

    @Override
    public List<Person> getCommonFriends(Long personId, Long person2Id) {
        Session session = MainExecutor.getSession();
        Person person1 = session.load(Person.class, personId);

        List<PersonKnowsPerson> commonFriends = new ArrayList<>(person1.getPersonKnowsPersonSet());
        List<Person> result = new ArrayList<>();
        List<Person> finalResult = result;
        commonFriends.forEach(personKnowsPerson -> finalResult.add(personKnowsPerson.getSubPerson()));

        Person person2 = session.load(Person.class, person2Id);
        List<PersonKnowsPerson> commonFriends2 = new ArrayList<>(person2.getPersonKnowsPersonSet());
        List<Person> friends2 = new ArrayList<>();
        commonFriends2.forEach(personKnowsPerson -> friends2.add(personKnowsPerson.getSubPerson()));

        result = result.stream().distinct().filter(friends2::contains).collect(Collectors.toList());

        result.forEach(
                friend -> System.out.printf(
                        " Id: %d First name: %s Last name: %s%n",
                        friend.getPersonID(),
                        friend.getFirstName(),
                        friend.getLastName()
                )
        );
        session.close();
        return result;
    }

    @Override
    public List<Person> getPersonsWithMostCommonInterests(Long personId) {
        Session session = MainExecutor.getSession();

        Person person = session.load(Person.class, personId);

        session.beginTransaction();
        List<Person> people = session.createQuery("from Person", Person.class).list();
        session.getTransaction().commit();

        people.remove(person);
        List<Person> mostCommonInterests = new ArrayList<>();

        int x = 0;
        for (int i = 0; i < people.size(); i++) {
            Person nextPerson = people.get(i);
            List<Tag> commonTags = new ArrayList<>();

            commonTags = person.getTagSet().stream()
                    .distinct()
                    .filter(nextPerson.getTagSet()::contains)
                    .collect(Collectors.toList());

            if (commonTags.size() > x) {
                x = commonTags.size();
                mostCommonInterests.clear();
                mostCommonInterests.add(nextPerson);
            } else if (commonTags.size() == x) {
                mostCommonInterests.add(nextPerson);
            }
        }

        mostCommonInterests.forEach(
                person1 -> System.out.printf(
                        " Id: %d First name: %s Last name: %s%n",
                        person1.getPersonID(),
                        person1.getFirstName(),
                        person1.getLastName()
                )
        );
        session.close();
        return mostCommonInterests;
    }

    @Override
    public List<Organisation> getJobRecommendation(Long personId) {
        Session session = MainExecutor.getSession();
        Person person = session.load(Person.class, personId);
        List<Organisation> organisations = new ArrayList<>();
        for (PersonKnowsPerson personKnowsPerson : person.getPersonKnowsPersonSet()) {
            if (!personKnowsPerson.getPerson().getWorkAtSet().isEmpty())
                for (WorkAt workAt : personKnowsPerson.getPerson().getWorkAtSet()) {
                    Company company = workAt.getCompany();
                    if (person.getCity().getCountry().equals(company.getCountry())) {
                        organisations.add(company);
                        System.out.printf(
                                " company:: Id: %d Name: %s URL: %s%n",
                                company.getOrganisationID(),
                                company.getName(),
                                company.getUrl()
                        );
                    }

                }

            if (!personKnowsPerson.getPerson().getStudyAtSet().isEmpty())
                for (StudyAt studyAt : personKnowsPerson.getPerson().getStudyAtSet()) {
                    University university = studyAt.getUniversity();
                    if (person.getCity().equals(university.getCity())) {
                        organisations.add(university);
                        System.out.printf(
                                " university:: Id: %d Name: %s URL: %s city: %s%n",
                                university.getOrganisationID(),
                                university.getName(),
                                university.getUrl(),
                                university.getCity().getName()
                        );
                    }
                }
        }
        session.close();
        return organisations;
    }
//
//    @Override
//    public List<Person> getShorthestFriendshipPath(Long personId, Long friendId) {
//        Session session = MainExecutor.getSession();
//
//        session.doWork(new ShortestFriendshipPath() {
//            @Override
//            public void execute(Connection connection) throws SQLException {
//                connection.prepareCall("{call queryinteriorexteriorcount()}").executeQuery();
//            }
//        });

        session.close();

        return null;
    }


}
