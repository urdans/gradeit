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

    @Query(value="SELECT COUNT(ID) FROM teacher WHERE EMAIL =?1 AND EMAIL <>''", nativeQuery = true)
    int countAllByEmail(String aEmail);

    @Query(value = "SELECT * FROM teacher WHERE ACTIVE", nativeQuery = true)
    List<Teacher> getActiveTeachers();

    Iterable<Teacher> findAllByOrderByLastName();

    @Query(value =
            "SELECT " +
                "groupingid, groupid, groupname, subjectid, subject.name as subjectname " +
            "FROM " +
                "(SELECT " +
                    "groupingid, group_id as groupid, `group`.name as groupname, subject_id as subjectid " +
                "FROM " +
                    "(SELECT " +
                        "grouping.id as groupingid, grouping.group_id, grouping.subject_id " +
                    "FROM " +
                        "grouping " +
                    "INNER JOIN " +
                        "teacher ON teacher.id = grouping.teacher_id " +
                    "WHERE " +
                        "teacher.id = ?1" +
                    ") AS teachergroups " +
                "INNER JOIN " +
                    "`group` ON `group`.id = teachergroups.group_id) AS teachergroupsandsubjects " +
            "INNER JOIN " +
                "`subject` ON `subject`.id = teachergroupsandsubjects.subjectid " +
            "ORDER BY groupname, subjectname"
            ,nativeQuery = true)
    List<IGroupSubjectPair> teacherGroupsAndSubjects(int teacherId);

    Teacher findFirstByUserName(String userName);
}
