package lc101.liftoff.gradeit.controllers;


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
    private int filterByGroupId = 0; //0 means "All groups"

    @RequestMapping(value = "students", method = RequestMethod.GET)
    public String registrarStudent(Model model, HttpServletRequest request) {
        if (!registrarLoggedIn(request)) {
            return "redirect:/";
        }
        GroupForm groupForm = new GroupForm(filterByGroupId, groupDao.findAll());
        model.addAttribute("form", groupForm);
        model.addAttribute("students", studentDao.filterByGroup(filterByGroupId));
        return "registrarstudents";
    }

    @RequestMapping(value = "students", method = RequestMethod.POST)
    public String registrarStudentApplyFilter(Model model, HttpServletRequest request, @ModelAttribute @Valid GroupForm groupForm) {
        if (!registrarLoggedIn(request)) {
            return "redirect:/";
        }
        filterByGroupId = groupForm.getGroupId();
        return registrarStudent(model, request);
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
