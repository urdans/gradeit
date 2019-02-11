package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.models.Subject;
import lc101.liftoff.gradeit.models.data.SubjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RestAPIController {
    @Autowired
    RegistrarController registrarController;

    @Autowired
    private SubjectDao subjectDao;

    //example: http://localhost:8080/api/getsubject?id=25
    @GetMapping("/api/getsubject")
    public Subject getSubjectById(@RequestParam(value="id", defaultValue="0") int id, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return null;

        if(id == 0) return null;

        return subjectDao.findById(id).orElse(null);
    }

    //example: http://localhost:8080/api/updatesubject data about subject in a json block
    @PutMapping("/api/updatesubject")
    public boolean updateSubjectById(@RequestBody Subject subjectToEdit, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return false;

        int id = subjectToEdit.getId();

        if(id == 0) return false;

        Subject subject = subjectDao.findById(id).orElse(null);

        if(subject == null) return false; //subject to edit not found

        subject.setName(subjectToEdit.getName());
        subjectDao.save(subject);
        return true;
    }

    @PostMapping("/api/addsubject")
    public int addSubject(@RequestBody Subject newSubject, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return -1;

        if(newSubject.getName() == null || newSubject.getName().length() < 3) return -1;

        Subject subject  = subjectDao.save(newSubject);

        return subject.getId();
    }

    @DeleteMapping("/api/deletesubject")
    public boolean deleteSubject(@RequestBody Subject subjectToDelete, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return false;

        Subject subject  = subjectDao.findById(subjectToDelete.getId()).orElse(null);

        if(subject == null) return false;
        /*todo
         * what if this subject is referenced by the "grouping" table? Test this later
         * same concept applies to all the deletions, students and teachers, etc.
         * */
        try {
            subjectDao.delete(subject);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

}
