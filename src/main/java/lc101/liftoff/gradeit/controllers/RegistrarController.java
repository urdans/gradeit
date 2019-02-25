package lc101.liftoff.gradeit.controllers;


import lc101.liftoff.gradeit.models.Group;
import lc101.liftoff.gradeit.models.Registrar;
import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.data.*;
import lc101.liftoff.gradeit.models.forms.GroupForm;
import lc101.liftoff.gradeit.models.forms.UserProfileForm;
import lc101.liftoff.gradeit.tools.UserRegistration;
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

import static lc101.liftoff.gradeit.tools.HashTools.hashAndSaltPassword;


@Controller
@RequestMapping("registrar")
public class RegistrarController {
    @Autowired
    private UserRegistration userRegistration;

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

    @Autowired
    private RegistrarDao registrarDao;

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
        model.addAttribute("title", "GradeIt-students");
        return "registrar/registrarstudents";
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
        model.addAttribute("title", "GradeIt-add student");
        return "registrar/registrarstudentedit";
    }

    @RequestMapping(value = "student/add", method = RequestMethod.POST)
    public String registrarStudentAddDo(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm,
                                        @ModelAttribute @Valid Student newStudent, Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll());
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("title", "GradeIt-add student");

        if (!userRegistration.emailGrammarIsOk(newStudent.getEmail())) {
            errors.rejectValue("email", "2", "Invalid email address. Please try again!");
        }

        if (errors.hasErrors()) return "registrar/registrarstudentedit";

        if (studentDao.countAllByEmail(newStudent.getEmail()) >= 1 ||
                teacherDao.countAllByEmail(newStudent.getEmail()) >= 1) {
            model.addAttribute("errormsg", "This email address is already registered!. Student was not added.");
            return "registrar/registrarstudentedit";
        }
        if (!userRegistration.emailGrammarIsOk(newStudent.getEmail())) {
            errors.rejectValue("email", "2", "Invalid email address. Please try again!");
        }

        Group group = groupDao.findById(groupForm.getGroupId()).orElse(null);
        newStudent.setGroup(group);
        studentDao.save(newStudent);
        model.addAttribute(new Student());
        return "registrar/registrarstudentedit";
    }

    @RequestMapping(value = "student/edit", method = RequestMethod.GET)
    public String registrarStudentEdit(Model model, HttpServletRequest request, @RequestParam(value = "id", required = false, defaultValue = "0") int id) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        if (id == 0) return "redirect:/registrar/students";

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
        model.addAttribute("title", "GradeIt-edit student");
        return "registrar/registrarstudentedit";
    }

    @RequestMapping(value = "student/edit/update", method = RequestMethod.POST)
    public String registrarStudentEditDo(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm,
                                         @ModelAttribute @Valid Student editedStudent, Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll());
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"
        model.addAttribute("title", "GradeIt-edit student");

        if (!userRegistration.emailGrammarIsOk(editedStudent.getEmail())) {
            errors.rejectValue("email", "2", "Invalid email address. Please try again!");
        }
        if (errors.hasErrors()) return "registrar/registrarstudentedit";

        Student studentToUpdate = studentDao.findById(editedStudent.getId()).orElse(null);

        if (studentToUpdate != null) {

            if (!studentToUpdate.isConfirmed()) {
                if (!studentToUpdate.getEmail().equals(editedStudent.getEmail())) {
                    if (studentDao.countAllByEmail(editedStudent.getEmail()) >= 1 ||
                            teacherDao.countAllByEmail(editedStudent.getEmail()) >= 1) {
                        model.addAttribute("errormsg", "This email address is already registered! " +
                                "Student was not added.");
                        return "registrar/registrarstudentedit";
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
        model.addAttribute("title", "GradeIt-teachers");
        return "registrar/registrarteachers";
    }

    @RequestMapping(value = "teacher/add", method = RequestMethod.GET)
    public String registrarTeacherAdd(Model model, HttpServletRequest request) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        //omitting "editmode" attribute means this is on "add mode"
        model.addAttribute(new Teacher());
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("title", "GradeIt-add teacher");
        return "registrar/registrarteacheredit";
    }

    @RequestMapping(value = "teacher/add", method = RequestMethod.POST)
    public String registrarTeacherAddDo(Model model, HttpServletRequest request, @ModelAttribute @Valid Teacher newTeacher,
                                        Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("title", "GradeIt-add teacher");

        if (!userRegistration.emailGrammarIsOk(newTeacher.getEmail())) {
            errors.rejectValue("email", "2", "Invalid email address. Please try again!");
        }

        if (errors.hasErrors()) return "registrar/registrarteacheredit";

        if (teacherDao.countAllByEmail(newTeacher.getEmail()) >= 1 ||
                studentDao.countAllByEmail(newTeacher.getEmail()) >= 1) {
            model.addAttribute("errormsg", "This email address is already registered!. Teacher was not added.");
            return "registrar/registrarteacheredit";
        }

        teacherDao. save(newTeacher);
        model.addAttribute(new Teacher());
        return "registrar/registrarteacheredit";
    }

    @RequestMapping(value = "teacher/edit", method = RequestMethod.GET)
    public String registrarTeacherEdit(Model model, HttpServletRequest request, @RequestParam(value = "id", required = false, defaultValue = "0") int id) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        if (id == 0) return "redirect:/registrar/teachers";

        Teacher teacherToEdit = teacherDao.findById(id).orElse(null);

        if (teacherToEdit == null) return "redirect:/registrar/teachers";

        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"
        model.addAttribute(teacherToEdit);
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("title", "GradeIt-edit teacher");
        return "registrar/registrarteacheredit";
    }

    @RequestMapping(value = "teacher/edit/update", method = RequestMethod.POST)
    public String registrarTeacherEditDo(Model model, HttpServletRequest request, @ModelAttribute @Valid Teacher editedTeacher,
                                         Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("editmode", "1"); //adding this attribute means it's "editing mode"
        model.addAttribute("title", "GradeIt-edit teacher");

        if (!userRegistration.emailGrammarIsOk(editedTeacher.getEmail())) {
            errors.rejectValue("email", "2", "Invalid email address. Please try again!");
        }

        if (errors.hasErrors()) return "registrar/registrarteacheredit";

        Teacher teacherToUpdate = teacherDao.findById(editedTeacher.getId()).orElse(null);

        if (teacherToUpdate != null) {

            if (!teacherToUpdate.isConfirmed()) {
                if (!teacherToUpdate.getEmail().equals(editedTeacher.getEmail())) {
                    if (teacherDao.countAllByEmail(editedTeacher.getEmail()) >= 1 ||
                            studentDao.countAllByEmail(editedTeacher.getEmail()) >= 1) {
                        model.addAttribute("errormsg", "This email address is already registered! " +
                                "Teacher was not added.");
                        return "registrar/registrarteacheredit";
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
        model.addAttribute("title", "GradeIt-subjects");
        return "registrar/registrarsubjects";
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
        model.addAttribute("title", "GradeIt-groups");

        return "registrar/registrargroups";
    }

    @RequestMapping(value = "groups", method = RequestMethod.POST)
    public String registrarGroupsApplyFilter(Model model, HttpServletRequest request, @ModelAttribute() @Valid GroupForm groupForm) {
        return registrarGroups(model, request, groupForm);
    }

    @RequestMapping(value = "profile", method = RequestMethod.GET)
    public String registrarProfile(Model model, HttpServletRequest request, @ModelAttribute @Valid UserProfileForm userProfileForm,
                                 Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        if (request.getMethod().equals("GET")) { //first time calling
            userProfileForm.setUserData(userSession.getUserAsRegistrar());
        } else if (!errors.hasErrors()) {//it's a POST

            Registrar registrar = registrarDao.findById(userProfileForm.getId()).orElse(null);

            if (registrar == null) {

                model.addAttribute("msg", "Registrar not found!");

            } else if (userProfileForm.getId() != userSession.getUserId()) {

                model.addAttribute("msg", "No hacking permitted!");

            } else if (!userProfileForm.getPassword().equals(userProfileForm.getPassword2())) {

                errors.rejectValue("password", "0", "Passwords don't match. Please try again!");
                errors.rejectValue("password2", "1", "Passwords don't match. Please try again!");

            } else if (!userRegistration.isUserNameAvailable(registrar, userProfileForm.getUserName())) {

                errors.rejectValue("userName", "3", "This user name is not available.");

            } else {
                registrar.setUserName(userProfileForm.getUserName());
                registrar.setPassword(hashAndSaltPassword(userProfileForm.getPassword()));
                registrarDao.save(registrar);
                model.addAttribute("msg", "Profile was properly updated!");
                userSession.updateSessionUserName(request, userProfileForm.getUserName());
            }
        }
        model.addAttribute("username", userSession.getSessionUserName(request));
        model.addAttribute("title", "GradeIt-profile");
        return "registrar/registrarprofile";
    }

    @RequestMapping(value = "profile", method = RequestMethod.POST)
    public String registrarUpdateProfile(Model model, HttpServletRequest request,
                                       @ModelAttribute @Valid UserProfileForm userProfileForm, Errors errors) {
        return registrarProfile(model, request, userProfileForm, errors);
    }









    /*todo
     * put order in the data (order by) when presenting it all over
     */


    /*todo
     * if a student or a teacher is singning in and he is not active, he wont be able to sign in.
     */
}
