package lc101.liftoff.gradeit.controllers;


import lc101.liftoff.gradeit.models.Group;
import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.data.*;
import lc101.liftoff.gradeit.models.forms.GroupForm;
import lc101.liftoff.gradeit.tools.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
@RequestMapping("registrar")
public class RegistrarController {
    @Autowired
    private UserSession userSession;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private GroupingDao groupingDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private SubjectDao subjectDao;

    public boolean registrarLoggedIn(HttpServletRequest request) {
        return (userSession.decodeSession(request)) & (userSession.isRegistrar());
    }

    @RequestMapping(value = "students", method = RequestMethod.GET)
    public String registrarStudents(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll()); //alway update the list of group names
        Iterable<Student> filteredStudentList;

        if (groupForm.getGroupId() == 0) //first request
            filteredStudentList = studentDao.findAllByOrderByLastNameAsc();
        else
            filteredStudentList = studentDao.findAllByGroupIdOrderByLastName(groupForm.getGroupId());

        model.addAttribute("students", filteredStudentList);
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "registrarstudents";
    }

    @RequestMapping(value = "students", method = RequestMethod.POST)
    public String registrarStudentApplyFilter(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm) {
        return registrarStudents(model, request, groupForm);
    }

    @RequestMapping(value = "student/add", method = RequestMethod.GET)
    public String registrarStudentAdd(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        //omitting "editmode" attribute means this is on "add mode"
        groupForm.setGroups(groupDao.findAll()); //will always suggest the first group in the first request
        model.addAttribute(new Student());
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "registrarstudentedit";
    }

    @RequestMapping(value = "student/add", method = RequestMethod.POST)
    public String registrarStudentAddDo(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm,
                                        @ModelAttribute @Valid Student newStudent, Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll());
        model.addAttribute("username", userSession.getSessionUserName(request));

        if (errors.hasErrors()) return "registrarstudentedit";

        if (studentDao.countAllByEmail(newStudent.getEmail()) >= 1) {
            model.addAttribute("errormsg", "This email address is already registered!. Student was not added.");
            return "registrarstudentedit";
        }

        Group group = groupDao.findById(groupForm.getGroupId()).orElse(null);
        newStudent.setGroup(group);
        studentDao.save(newStudent);
        model.addAttribute(new Student());
        return "registrarstudentedit";
    }

    @RequestMapping(value = "student/edit", method = RequestMethod.GET)
    public String registrarStudentEdit(Model model, HttpServletRequest request, @RequestParam int id) {

        if (!registrarLoggedIn(request)) return "redirect:/";
        /*todo what if id is omitted in student/edit?id=i ? whitelabel error. Need to capture.*/

        Student studentToEdit = studentDao.findById(id).orElse(null);

        if (studentToEdit == null) return "redirect:/registrar/students";

        int groupId;
        if (studentToEdit.getGroup() == null)
            groupId = 0;
        else
            groupId = studentToEdit.getGroup().getId();

        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"
        GroupForm groupForm = new GroupForm(groupId, groupDao.findAll());
        model.addAttribute("groupForm", groupForm);
        model.addAttribute(studentToEdit);
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "registrarstudentedit";
    }

    @RequestMapping(value = "student/edit/update", method = RequestMethod.POST)
    public String registrarStudentEditDo(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm,
                                         @ModelAttribute @Valid Student editedStudent, Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll());
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"

        if (errors.hasErrors()) return "registrarstudentedit";

        Student studentToUpdate = studentDao.findById(editedStudent.getId()).orElse(null);

        if (studentToUpdate != null) {

            if (!studentToUpdate.isConfirmed()) {
                if (!studentToUpdate.getEmail().equals(editedStudent.getEmail())) {
                    if (studentDao.countAllByEmail(editedStudent.getEmail()) >= 1) {
                        model.addAttribute("errormsg", "This email address is already registered!. " +
                                "Student was not added.");
                        return "registrarstudentedit";
                    }
                }
                studentToUpdate.setEmail(editedStudent.getEmail());
                studentToUpdate.setAddress(editedStudent.getAddress());
                studentToUpdate.setPhoneNumber(editedStudent.getPhoneNumber());
            }

            studentToUpdate.setFirstName(editedStudent.getFirstName());
            studentToUpdate.setLastName(editedStudent.getLastName());
            studentToUpdate.setActive(editedStudent.isActive());
            int editedGroupId = groupForm.getGroupId();
            if (editedGroupId == 0)
                studentToUpdate.setGroup(null);
            else {
                Group groupToUpdate = groupDao.findById(editedGroupId).orElse(null);
                studentToUpdate.setGroup(groupToUpdate);
            }
            studentDao.save(studentToUpdate);
        }

        return "redirect:/registrar/students";
    }

    @RequestMapping(value = "teachers", method = RequestMethod.GET)
    public String registrarTeachers(Model model, HttpServletRequest request) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        Iterable<Teacher> teacherList;
        teacherList = teacherDao.findAllByOrderByLastName();
        model.addAttribute("teachers", teacherList);
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "registrarteachers";
    }

    @RequestMapping(value = "teacher/add", method = RequestMethod.GET)
    public String registrarTeacherAdd(Model model, HttpServletRequest request) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        //omitting "editmode" attribute means this is on "add mode"
        model.addAttribute(new Teacher());
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "registrarteacheredit";
    }

    @RequestMapping(value = "teacher/add", method = RequestMethod.POST)
    public String registrarTeacherAddDo(Model model, HttpServletRequest request, @ModelAttribute @Valid Teacher newTeacher,
                                        Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";
        model.addAttribute("username", userSession.getSessionUserName(request));

        if (errors.hasErrors()) return "registrarteacheredit";

        if (teacherDao.countAllByEmail(newTeacher.getEmail()) >= 1) {
            model.addAttribute("errormsg", "This email address is already registered!. Teacher was not added.");
            return "registrarteacheredit";
        }

        teacherDao.save(newTeacher);
        model.addAttribute(new Teacher());
        return "registrarteacheredit";
    }

    @RequestMapping(value = "teacher/edit", method = RequestMethod.GET)
    public String registrarTeacherEdit(Model model, HttpServletRequest request, @RequestParam int id) {

        if (!registrarLoggedIn(request)) return "redirect:/";
        /*todo what if id is omitted in student/edit?id=i ? whitelabel error. Need to capture.*/

        Teacher teacherToEdit = teacherDao.findById(id).orElse(null);

        if (teacherToEdit == null) return "redirect:/registrar/teachers";

        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"
        model.addAttribute(teacherToEdit);
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "registrarteacheredit";
    }

    @RequestMapping(value = "teacher/edit/update", method = RequestMethod.POST)
    public String registrarTeacherEditDo(Model model, HttpServletRequest request, @ModelAttribute @Valid Teacher editedTeacher,
                                         Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"

        if (errors.hasErrors()) return "registrarteacheredit";

        Teacher teacherToUpdate = teacherDao.findById(editedTeacher.getId()).orElse(null);

        if (teacherToUpdate != null) {

            if (!teacherToUpdate.isConfirmed()) {
                if (!teacherToUpdate.getEmail().equals(editedTeacher.getEmail())) {
                    if (studentDao.countAllByEmail(editedTeacher.getEmail()) >= 1) {
                        model.addAttribute("errormsg", "This email address is already registered!. " +
                                "Teacher was not added.");
                        return "registrarteacheredit";
                    }
                }
                teacherToUpdate.setEmail(editedTeacher.getEmail());
                teacherToUpdate.setPhoneNumber(editedTeacher.getPhoneNumber());
            }

            teacherToUpdate.setFirstName(editedTeacher.getFirstName());
            teacherToUpdate.setLastName(editedTeacher.getLastName());
            teacherToUpdate.setActive(editedTeacher.isActive());
            teacherDao.save(teacherToUpdate);
        }

        return "redirect:/registrar/teachers";
    }

    @RequestMapping(value = "subjects", method = RequestMethod.GET)
    public String registrarSubjects(Model model, HttpServletRequest request) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("subjects", subjectDao.findAll());
        return "registrarsubjects";
    }

    @RequestMapping(value = "groups", method = RequestMethod.GET)
    public String registrarGroups(Model model, HttpServletRequest request, @ModelAttribute() @Valid GroupForm groupForm) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll()); //always update the group list from the db

        if(groupForm.getGroupId() == 0) { //means it's the first request to this endpoint
            Group group = groupForm.getFirstGroup();
            if (group != null) groupForm.setGroupId(group.getId());
        }

        model.addAttribute("grouping", groupingDao.findAllByGroupId(groupForm.getGroupId()));
        model.addAttribute("teachers", teacherDao.getActiveTeachers());
        model.addAttribute("subjects", subjectDao.findAll());
        model.addAttribute("username", userSession.getSessionUserName(request));

        return "registrargroups";
    }

    @RequestMapping(value = "groups", method = RequestMethod.POST)
    public String registrarGroupsApplyFilter(Model model, HttpServletRequest request, @ModelAttribute() @Valid GroupForm groupForm) {
        return registrarGroups(model, request, groupForm);
    }
    /*todo
    * put order in the data (order by) when presenting it all over*/


    /*todo (keep in mind the reference integrity when deleting). Maybe no need to delete, what is active for?
     * implement student deletion
     * implement teacher deletion
     */

    /*todo
     * if a student or a teacher is singning in and he is not active, he wont be able to sign in.
     */
}
