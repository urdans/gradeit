package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("GroupDao")
@Transactional
public interface GroupDao extends CrudRepository<Group, Integer> {
}
