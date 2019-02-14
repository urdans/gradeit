package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository("TeacherDao")
@Transactional
public interface TeacherDao extends CrudRepository<Teacher, Integer> {

    @Query(value="SELECT COUNT(ID) FROM STUDENT WHERE EMAIL =?1 AND EMAIL <>''", nativeQuery = true)
    int countAllByEmail(String aEmail);

/*
    @Query(value = "SELECT ID, CONCAT(FIRST_NAME, ' ',LAST_NAME) AS NAME FROM TEACHER WHERE ACTIVE", nativeQuery = true)
    List<TeacherForm> getActiveTeachers();
*/

    @Query(value = "SELECT * FROM TEACHER WHERE ACTIVE", nativeQuery = true)
    List<Teacher> getActiveTeachers();

}
