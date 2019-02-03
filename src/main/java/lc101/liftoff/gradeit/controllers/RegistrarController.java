package lc101.liftoff.gradeit.controllers;


import lc101.liftoff.gradeit.models.Group;
import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.data.GroupDao;
import lc101.liftoff.gradeit.models.data.StudentDao;
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
import java.util.List;


@Controller
@RequestMapping("registrar")
public class RegistrarController {
    @Autowired
    private UserSession userSession;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private GroupDao groupDao;
    private int filterByGroupId = 0; //0 means "All groups"

    @RequestMapping(value = "students", method = RequestMethod.GET)
    public String registrarStudent(Model model, HttpServletRequest request) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        GroupForm groupForm = new GroupForm(filterByGroupId, groupDao.findAll());
        model.addAttribute("groupForm", groupForm);
        Iterable<Student> filteredStudentList;

        if (filterByGroupId == 0)
            filteredStudentList = studentDao.findAll();
        else
            filteredStudentList = studentDao.findAllByGroupId(filterByGroupId);

        model.addAttribute("students", filteredStudentList);
        model.addAttribute("username", userSession.getSessionUserName(request));

        return "registrarstudents";
    }

    @RequestMapping(value = "students", method = RequestMethod.POST)
    public String registrarStudentApplyFilter(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        filterByGroupId = groupForm.getGroupId();
        return registrarStudent(model, request);
    }

    @RequestMapping(value = "student/add", method = RequestMethod.GET)
    public String registrarStudentAdd(Model model, HttpServletRequest request) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        //omitting "editmode" attribute means this is on "add mode"
        GroupForm groupForm = new GroupForm(filterByGroupId, groupDao.findAll());
        model.addAttribute("groupForm", groupForm);
        model.addAttribute(new Student());
        return "registrarstudentedit";
    }

    @RequestMapping(value = "student/add", method = RequestMethod.POST)
    public String registrarStudentAddDo(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm,
                                        @ModelAttribute @Valid Student newStudent, Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll());

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

        return "registrarstudentedit";
    }

    @RequestMapping(value = "student/edit/update", method = RequestMethod.POST)
    public String registrarStudentEditDo(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm,
                                         @ModelAttribute @Valid Student editedStudent, Errors errors) {

        if (!registrarLoggedIn(request)) return "redirect:/";

        groupForm.setGroups(groupDao.findAll());

        if (errors.hasErrors()) return "registrarstudentedit";

        Student studentToUpdate = studentDao.findById(editedStudent.getId()).orElse(null);

        if (studentToUpdate != null) {

            if (!editedStudent.isConfirmed()) {
                if (!studentToUpdate.getEmail().equals(editedStudent.getEmail())) {
                    if (studentDao.countAllByEmail(editedStudent.getEmail()) >= 1) {
                        model.addAttribute("errormsg", "This email address is already registered!. " +
                                "Student was not added.");
                        model.addAttribute("editmode", "1");
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


    private boolean registrarLoggedIn(HttpServletRequest request) {
        return (userSession.decodeSession(request)) & (userSession.isRegistrar());
    }





    /*todo next
     * implement the view/edit view of student
     * implement the add view of student
     * implement the teachers list sub section
     *   implement the view/edit view of teacher
     *  implement the add view of teacher
     * implement the subject sub section
     * implement the groups sub section
     * */
}
