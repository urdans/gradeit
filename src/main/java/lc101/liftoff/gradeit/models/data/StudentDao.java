package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Group;
import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository("StudentDao")
@Transactional
public interface StudentDao extends CrudRepository<Student, Integer> {

/*    default Iterable<Student> filterByGroup(int groupId) {
        if(groupId == 0) return findAll();
        ArrayList<Student> allStudents = (ArrayList<Student>) findAll();
        ArrayList<Student> filteredStudents = new ArrayList<>();
        for (Student student : allStudents) {
            Group group = student.getGroup();
            if (group != null) {
                if (group.getId() == groupId) filteredStudents.add(student);
            }
        }
        return filteredStudents;
    }*/

    @Query(value = "SELECT * FROM STUDENT WHERE GROUP_ID = ?1", nativeQuery = true)
    List<Student> findAllByGroupId(int groupId);

    @Query(value="SELECT COUNT(ID) FROM STUDENT WHERE EMAIL =?1 AND EMAIL <>''", nativeQuery = true)
    int countAllByEmail(String aEmail);
}
