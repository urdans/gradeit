package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Schedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository("ScheduleDao")
@Transactional
public interface ScheduleDao extends CrudRepository<Schedule, Integer> {
    List<Schedule> findAllByGroupingIdOrderByDateAsc(int groupingId);

    /*Info this is another way of querying, using default methods in the interface*/
    default double getPercentageSumByGroupingId(int groupingId){
        double sum = 0;
        List<Schedule> schedules = findAllByGroupingIdOrderByDateAsc(groupingId);
        for(Schedule schedule: schedules) {
            sum = sum + schedule.getPercentage();
        }
        return sum;
    }

    default double getPercentageSumByGroupingIdExcluding(int groupingId, int scheduleId){
        double sum = 0;
        List<Schedule> schedules = findAllByGroupingIdOrderByDateAsc(groupingId);
        for(Schedule schedule: schedules) {
            if(schedule.getId() != scheduleId) sum = sum + schedule.getPercentage();
        }
        return sum;
    }

    @Query(value =
            "SELECT " +
            "	Date(date) as date, description, percentage, grade.`value` AS gradeValue, grade.id AS gradeId, " +
                    "`schedule`.id AS scheduleId " +
            "FROM " +
            "	(SELECT " +
            "		* " +
            "	FROM " +
            "		grade " +
            "	where " +
            "		student_id = ?2) " +
            "AS " +
            "	grade " +
            "RIGHT JOIN " +
            "	`schedule` " +
            "ON " +
            "	grade.schedule_id = `schedule`.id " +
            "WHERE " +
            "	grouping_id = ?1 " +
            "ORDER BY date ASC"
            ,nativeQuery = true)
    List<IGrade> getGradesByGroupingAndStudent(int groupingId, int studentId);
}


//find…By, read…By, query…By, count…By, and get…By