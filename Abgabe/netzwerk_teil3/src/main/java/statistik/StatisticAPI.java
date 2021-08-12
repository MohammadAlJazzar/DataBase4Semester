package statistik;

/**
 * @author Mohammad Ghaith Albaba
 * @author Hasham
 * @author Mohammad Al Jazzar
 * @project db-teil3
 */
public interface StatisticAPI {
    public void getPopularComments(Integer k);
    public void getCountryWithMostComment_Post();
    public void getTagClassHierarchy(int tagClassId);

}
