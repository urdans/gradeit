package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository("SubjectDao")
@Transactional
public interface SubjectDao extends CrudRepository<Subject, Integer> {
    //find…By, read…By, query…By, count…By, and get…By
    //@Query(value = "SELECT * FROM GROUPING WHERE GROUP_ID = ?1", nativeQuery = true)
    int countByName(String name);
}
