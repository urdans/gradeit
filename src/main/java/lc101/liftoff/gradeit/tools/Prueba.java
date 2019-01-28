package lc101.liftoff.gradeit.tools;

import lc101.liftoff.gradeit.models.data.RegistrarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/*
* this is for making tests
*
* remove completely afterwards
* */
@Component("Prueba")
public class Prueba {
    @Autowired
    private RegistrarDao registrarDao;

    private boolean response;
    public boolean isOk(){
        response = registrarDao.existsById(1);
        return response;
    }
}
