package personenbezogene;

import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NamedNativeQuery(
        name = "getshortpath",
        callable = true,
        query = "{?=select * from getshortpath(?,?)}",
        resultClass = ShortestFriendshipPath.class
)
public class ShortestFriendshipPath {
    @Id
    int id;

    long personId;

    long personId2;

    long[] path;

    int lenght;



}
