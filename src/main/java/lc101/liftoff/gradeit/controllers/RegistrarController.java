package lc101.liftoff.gradeit.controllers;


import lc101.liftoff.gradeit.tools.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("registrar")
public class RegistrarController {
    @Autowired
    UserSession userSession;

    @RequestMapping(value = "students", method = RequestMethod.GET)
    public String registrarStudent(HttpServletRequest request){
        if(!registrarLoggedIn(request)){
            return "redirect:/";
        }
        return "registrarstudents";
    }

    public boolean registrarLoggedIn(HttpServletRequest request){
        return (userSession.decodeSession(request)) & (userSession.isRegistrar());
    }
}
