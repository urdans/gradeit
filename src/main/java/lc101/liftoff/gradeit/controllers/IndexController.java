package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.tools.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="")
public class IndexController {
    @Autowired
    UserSession userSession;

    private HttpSession session;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String logIn(HttpServletRequest request){
        /* if the session contains info about a logged user, redirect to /Student, /teacher or /registrar
         accordingly, otherwise render the log in page */
        if(userSession.decodeSession(request)){
            if(userSession.isStudent())
                return "redirect:/Student";
            if(userSession.isTeacher())
                return "redirect:/teacher";
            if(userSession.isRegistrar())
                return "redirect:/registrar";
        }
        return "login";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String logInDone(Model model, HttpServletRequest request, @RequestParam String username, @RequestParam String password){
        /* check if the combination user/password exist in any of the user tables and
        creates the session accordingly, otherwise show error message */
        if(userSession.decodeSession(request, username, password)){
            if(userSession.isStudent())
                return "redirect:/Student";
            if(userSession.isTeacher())
                return "redirect:/teacher";
            if(userSession.isRegistrar())
                return "redirect:/registrar";
        }
        model.addAttribute("errormsg", "Ivalid combination user/password. Please try again");
        return "login";
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
}
