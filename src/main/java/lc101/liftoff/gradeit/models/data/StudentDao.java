package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository("StudentDao")
@Transactional
public interface StudentDao extends CrudRepository<Student, Integer> {

    @Query(value = "SELECT * FROM STUDENT WHERE GROUP_ID = ?1", nativeQuery = true)
    List<Student> findAllByGroupId(int groupId);

    @Query(value="SELECT COUNT(ID) FROM STUDENT WHERE EMAIL =?1 AND EMAIL <>''", nativeQuery = true)
    int countAllByEmail(String aEmail);

    Iterable<Student> findAllByGroupIdOrderByLastName(int groupId);

    Iterable<Student> findAllByOrderByLastNameAsc();
    /*INFO To order by without parameter always apend By after All*/

    @Query(value=
            "SELECT " +
            "   id, CONCAT(last_name, ', ', first_name) as name " +
            "FROM " +
            "   student " +
            "WHERE " +
            "   student.group_id = ( " +
            "   SELECT " +
            "       groupid " +
            "   FROM " +
            "       (SELECT " +
            "           grouping.id, `group`.id as groupid, `group`.`name` " +
            "       FROM " +
            "           grouping " +
            "       INNER JOIN " +
            "           `group` " +
            "       ON " +
            "           grouping.group_id = `group`.id " +
            "       WHERE " +
            "           grouping.id = ?1) " +
            "   AS groupinggroups) " +
            "AND " +
            "   student.active = 1 " +
            "ORDER BY name"
            ,nativeQuery = true)
    List<IStudentIdAndName> getGroupingStudents(int groupingId);

    Student findFirstByUserName(String userName);
}
