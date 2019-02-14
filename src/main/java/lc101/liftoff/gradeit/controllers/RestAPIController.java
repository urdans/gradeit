package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.models.Group;
import lc101.liftoff.gradeit.models.Grouping;
import lc101.liftoff.gradeit.models.Subject;
import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.data.GroupDao;
import lc101.liftoff.gradeit.models.data.GroupingDao;
import lc101.liftoff.gradeit.models.data.SubjectDao;
import lc101.liftoff.gradeit.models.data.TeacherDao;
import lc101.liftoff.gradeit.models.forms.GroupingForm;
import lc101.liftoff.gradeit.models.forms.MessageContainer;
import lc101.liftoff.gradeit.models.forms.TeacherForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class RestAPIController {
    @Autowired
    private RegistrarController registrarController;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private GroupingDao groupingDao;

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

    @PostMapping("/api/addgroup")
    public int addGroup(@RequestBody Group newGroup, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return 0;

        if(newGroup.getName().length() < 1) return 0;

        return groupDao.save(newGroup).getId();
    }

    @PutMapping("/api/updategroup")
    public boolean updateGroupById(@RequestBody Group groupToEdit, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return false;

        int id = groupToEdit.getId();

        if(id == 0) return false;

        Group group = groupDao.findById(id).orElse(null);

        if(group == null) return false; //group to edit not found

        group.setName(groupToEdit.getName());
        groupDao.save(group);
        return true;
    }

    @DeleteMapping("/api/deletegroup")
    public MessageContainer deleteGroup(@RequestBody Group groupToDelete, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return new MessageContainer("Unauthorized!", true);

        Group group  = groupDao.findById(groupToDelete.getId()).orElse(null);

        if(group == null) return new MessageContainer("Group not found!", true);
        /*todo
         * what if this group is referenced by the "student" and "grouping" tables?
         * Implement this pattern to other delete functions like students and teachers, etc.
         * */
        try {
            groupDao.delete(group);
        }catch (Exception e) {
            return new MessageContainer("Can not delete this group while it's in use!", true);
        }
        return new MessageContainer("Group name deleted!", false);
    }

    @PostMapping("/api/addgrouping")
    public MessageContainer addGrouping(@RequestBody GroupingForm newGrouping, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return new MessageContainer("Unauthorized!", true);

        if(newGrouping.group_id == 0 || newGrouping.subject_id == 0 || newGrouping.teacher_id == 0) return new MessageContainer("Invalid " +
                "group/subject/teacher identifiction!", true);

        Group group = groupDao.findById(newGrouping.group_id).orElse(null);

        if(group == null) return new MessageContainer("Group not found!", true);

        Subject subject = subjectDao.findById(newGrouping.subject_id).orElse(null);

        if(subject == null) return new MessageContainer("Subject not found!", true);

        Teacher teacher = teacherDao.findById(newGrouping.teacher_id).orElse(null);

        if(teacher == null) return new MessageContainer("Teacher not found!", true);

        Grouping grouping = new Grouping(group, subject, teacher);
        int id = groupingDao.save(grouping).getId();

        MessageContainer messageContainer = new MessageContainer("Subject-teacher pairing added!", false);
        messageContainer.id = id;
        return messageContainer;
    }

    @DeleteMapping("/api/deletegrouping")
    public MessageContainer deleteGrouping(@RequestBody GroupingForm groupingToDelete, HttpServletRequest request) {

        if (!registrarController.registrarLoggedIn(request)) return new MessageContainer("Unauthorized!", true);

        Grouping grouping  = groupingDao.findById(groupingToDelete.group_id).orElse(null);

        if(grouping == null) return new MessageContainer("Subject-teacher pairing not found!", true);

        try {
            groupingDao.delete(grouping);
        }catch (Exception e) {
            return new MessageContainer("Can not remove this subject-teacher pairing while it's in use!", true);
        }
        return new MessageContainer("Subject-teacher pairing removed!", false);
    }
}
