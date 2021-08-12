package personenbezogene;

import netzwerk.Organisation;
import netzwerk.Person;
import netzwerk.Tag;

import java.util.List;
import java.util.Set;

public interface PersonRelatedAPI {

    public Person getProfile(Long personId);

    public Set<Tag> getCommonInterestsOfMyFriends(Long personId);

    public List<Person> getCommonFriends(Long personId, Long person2Id);

    // sTRING ODER person
    public List<Person> getPersonsWithMostCommonInterests(Long personId);

    public List<Organisation> getJobRecommendation(Long personId);

    public List<Person> getShorthestFriendshipPath(Long personId, Long friendId);


}
