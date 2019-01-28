package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository("SubjectDao")
@Transactional
public interface SubjectDao extends CrudRepository<Subject, Integer> {
}
