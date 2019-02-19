package lc101.liftoff.gradeit.models.data;


import lc101.liftoff.gradeit.models.Grade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository("GradeDao")
@Transactional
public interface GradeDao extends CrudRepository<Grade, Integer> {
	//public List<Grouping> findAllByGroup_Id(int id);
	Grade findFirstByStudentIdAndScheduleId(int studentId, int scheduleId);
}
