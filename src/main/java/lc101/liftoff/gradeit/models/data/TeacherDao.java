package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("TeacherDao")
@Transactional
public interface TeacherDao extends CrudRepository<Teacher, Integer> {
}
