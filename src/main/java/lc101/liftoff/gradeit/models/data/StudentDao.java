package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("StudentDao")
@Transactional
public interface StudentDao extends CrudRepository<Student, Integer> {
}
