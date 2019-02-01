package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.tools.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="teacher")
public class TeacherController {
    @Autowired
    UserSession userSession;

    @RequestMapping(value = "roster", method = RequestMethod.GET)
    public String teacherRosterlogIn(HttpServletRequest request){
        if(!teacherLoggedIn(request)){
            return "redirect:/";
        }
        return "teacherroster";
    }

    public boolean teacherLoggedIn(HttpServletRequest request){
        return (userSession.decodeSession(request)) & (userSession.isTeacher());
    }
}
