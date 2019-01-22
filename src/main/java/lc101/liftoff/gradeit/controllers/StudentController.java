package lc101.liftoff.gradeit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/")
public class StudentController {
    @RequestMapping(value = "student", method = RequestMethod.GET)
    public String logIn(){
        return "studenthome";
    }
}
