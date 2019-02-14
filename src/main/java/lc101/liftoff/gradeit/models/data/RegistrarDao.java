package lc101.liftoff.gradeit.models.data;

import lc101.liftoff.gradeit.models.Registrar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository("RegistrarDao")
@Transactional
public interface RegistrarDao extends CrudRepository<Registrar, Integer> {

}