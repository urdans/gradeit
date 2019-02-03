package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("TeacherDao")
@Transactional
public interface TeacherDao extends CrudRepository<Teacher, Integer> {

    @Query(value="SELECT COUNT(ID) FROM STUDENT WHERE EMAIL =?1 AND EMAIL <>''", nativeQuery = true)
    int countAllByEmail(String aEmail);

}
