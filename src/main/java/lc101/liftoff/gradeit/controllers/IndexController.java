package lc101.liftoff.gradeit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="")
public class IndexController {
//will admin logging, registration and registration confirmation
    private HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String logIn(HttpServletRequest request){
        //is the user is logged in, redirect to /student, /teacher or /registrar url
        //otherwise, render the log in page
        session = request.getSession(false);

        if(userIsStudent()){
            return "redirect:/student";
        }

        if(userIsTeacher()){
            return "redirect:/teacher";
        }

        if(userIsRegistrar()){
            return "redirect:/registrar";
        }

        return "login";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String logInDone(HttpServletRequest request){
        //check the user type. Remember the user has logged in and redirect accordingly
        //If wrong user show error message
        session = request.getSession(true);
        session.setAttribute("uname","urdans");
        if(userIsStudent()){
            return "redirect:student";
        }

        if(userIsTeacher()){
            return "redirect:teacher";
        }

        if(userIsRegistrar()){
            return "redirect:registrar";
        }

        return "redirect:";
    }


    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String registerDone(){
        return "registrationdone";
    }

    @RequestMapping(value = "registrationconfirmation", method = RequestMethod.GET)
    public String registrationConfirmed(HttpServletRequest request){
        //if the token (registrationconfirmation?token=123456ABCD&email=urdans@gmail.com&userid=49&usertype=1) is
        // correct return this page. otherwise redirect to register
        String token = request.getParameter("token");
        String email = request.getParameter("email");
        String userid = request.getParameter("userid");
        String usertype = request.getParameter("usertype");
        if(token.equals("123456ABCD") & email.equals("urdans@gmail.com") & userid.equals("49") & usertype.equals("1")){
            return "registrationconfirmed";
        }
        return "redirect:register";
    }

    public boolean userIsLogged(){
        return false;
    }

    public boolean userIsStudent(){
        if(session==null) return false;
        String user = (String)session.getAttribute("uname");
        return user.equals("urdans");
//        return false;
    }

    public boolean userIsTeacher(){
        return false;
    }

    public boolean userIsRegistrar(){
        return false;
    }
}
