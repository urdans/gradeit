package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Group;
import lc101.liftoff.gradeit.models.Grouping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository("GroupingDao")
@Transactional
public interface GroupingDao extends CrudRepository<Grouping, Integer> {
    //All this form of querying are valid
    //public List<Grouping> findAllByGroupId(int id);
    //public List<Grouping> findAllByGroup_Id(int id);

    //@Query(value = "SELECT * FROM GROUPING WHERE GROUP_ID = ?1", nativeQuery = true)
    List<Grouping> findAllByGroupId(int groupId);

//    @Query(value = "SELECT COUNT(id) FROM grouping WHERE subject_id = ?1 AND group_id = ?2", nativeQuery = true)
//    int subjectsInGroup(int subjectId, int groupId);
    int countBySubjectIdAndGroupId(int subjectId, int groupId);

}
