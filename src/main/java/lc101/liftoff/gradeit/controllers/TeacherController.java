package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.models.Grade;
import lc101.liftoff.gradeit.models.Schedule;
import lc101.liftoff.gradeit.models.data.*;
import lc101.liftoff.gradeit.models.forms.GroupSubjectPairForm;
import lc101.liftoff.gradeit.models.forms.GroupingStudentsForm;
import lc101.liftoff.gradeit.tools.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value="teacher")
public class TeacherController {
    @Autowired
    UserSession userSession;

    @Autowired
    TeacherDao teacherDao;

    @Autowired
    StudentDao studentDao;

    @Autowired
    ScheduleDao scheduleDao;

    @Autowired
    GradeDao gradeDao;

    /*todo manage all the cases where there is no data yet, especially in the select elements in the html*/
    /*todo if an teacher or student user is not active, he must not be able to log in*/


    public boolean teacherLoggedIn(HttpServletRequest request){
        return (userSession.decodeSession(request)) & (userSession.isTeacher());
    }

    @RequestMapping(value = "roster", method = RequestMethod.GET)
    public String teacherRoster(Model model, HttpServletRequest request,
                                     @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm){

        if(!teacherLoggedIn(request)) return "redirect:/";

        groupSubjectPairForm.setGroupSubjectPairs(teacherDao.teacherGroupsAndSubjects(userSession.getUserId()));
        List<Schedule> schedules = scheduleDao.findAllByGroupingIdOrderByDateAsc(groupSubjectPairForm.getSelectedPair());
        model.addAttribute("schedules", schedules);
        model.addAttribute("studentlist", new GroupingStudentsForm(
                studentDao.getGroupingStudents(groupSubjectPairForm.getSelectedPair()), schedules, gradeDao).getStudentIdAndNameList());
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "teacherroster";
    }

    @RequestMapping(value = "roster", method = RequestMethod.POST)
    public String teacherRosterApplyFilter(Model model, HttpServletRequest request,
                                     @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm){
        return teacherRoster(model, request, groupSubjectPairForm);
    }

    /*todo all buttons to close/cancel should be consistently "Close" for local forms or "Cancel" for forms in other pages (or maybe "Back")
    */

    @RequestMapping(value = "schedules", method = RequestMethod.GET)
    public String teacherSchedules(Model model, HttpServletRequest request,
                                @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm){

        if(!teacherLoggedIn(request)) return "redirect:/";

        groupSubjectPairForm.setGroupSubjectPairs(teacherDao.teacherGroupsAndSubjects(userSession.getUserId()));
        List<Schedule> schedules = scheduleDao.findAllByGroupingIdOrderByDateAsc(groupSubjectPairForm.getSelectedPair());
        model.addAttribute("schedules", schedules);
        model.addAttribute("username", userSession.getSessionUserName(request));
        return "teacherschedules";
    }

    @RequestMapping(value = "schedules", method = RequestMethod.POST)
    public String teacherScheduleApplyFilter(Model model, HttpServletRequest request,
                                           @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm){
        return teacherSchedules(model, request, groupSubjectPairForm);
    }
}
