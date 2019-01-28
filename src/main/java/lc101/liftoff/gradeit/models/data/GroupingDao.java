package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Grouping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("GroupingDao")
@Transactional
public interface GroupingDao extends CrudRepository<Grouping, Integer> {
}
