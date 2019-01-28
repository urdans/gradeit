package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Schedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("ScheduleDao")
@Transactional
public interface ScheduleDao extends CrudRepository<Schedule, Integer> {
}
