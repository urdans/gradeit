package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.tools.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="student")
public class StudentController {
    @Autowired
    UserSession userSession;

    @RequestMapping(value = "grades", method = RequestMethod.GET)
    public String studentGrades(HttpServletRequest request){
        if(!studentLoggedIn(request)){
            return "redirect:/";
        }
        return "studentgrades";
    }

    public boolean studentLoggedIn(HttpServletRequest request){
        return (userSession.decodeSession(request)) & (userSession.isStudent());
    }
}
