package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.tools.UserRegistration;
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
@RequestMapping(value = "")
public class IndexController {
    @Autowired
    private UserSession userSession;

    @Autowired
    private UserRegistration userRegistration;

    private HttpSession session;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String logIn(HttpServletRequest request) {
        /* if the session contains information about a logged user, redirect to /Student, /teacher or /registrar
         accordingly, otherwise render the log in page */
        if (userSession.decodeSession(request)) {
            if (userSession.isStudent())
                return "redirect:/student/grades";  //home for student users
            if (userSession.isTeacher())
                return "redirect:/teacher/roster";  //home for teacher users
            if (userSession.isRegistrar())
                return "redirect:/registrar/students"; //home for registrar users
        }
        return "login";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String logInDone(Model model, HttpServletRequest request, @RequestParam String username,
                            @RequestParam String password) {
        /* check if the combination user/password exist in any of the user tables and
        creates the session accordingly, otherwise show error message */
        if (userSession.decodeSession(request, username, password)) {
            if (userSession.isStudent())
                return "redirect:/student/grades";
            if (userSession.isTeacher())
                return "redirect:/teacher/roster";
            if (userSession.isRegistrar())
                return "redirect:/registrar/students";
        }
        model.addAttribute("errormsg", "Ivalid combination user/password. Please try again");
        return "login";
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register(Model model) {
        return "register";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String registerDo(Model model, HttpServletRequest request, @RequestParam String email,
                             @RequestParam String username, @RequestParam String password1,
                             @RequestParam String password2) {
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        if (email.equals("") || username.equals("") || password1.equals("") || password2.equals("")) {
            model.addAttribute("errormsg", "Please fill in all the fields and try again");
            return register(model);
        }
        if (!password1.equals(password2)) {
            model.addAttribute("errormsg", "Passwords don't match. Please try again");
            return register(model);
        }
        if (!userRegistration.emailGrammarIsOk(email)) {
            model.addAttribute("errormsg", "Invalid email address. Please try again");
            return register(model);
        }
        if (userRegistration.emailIsInRecords(email)) {
            if (userRegistration.userIsRegistered()) {
                model.addAttribute("errormsg", "This user account is already registered");
                return register(model);
            }
            if (userRegistration.userNameIsTaken(username)) {
                model.addAttribute("errormsg", "This user name is not available");
                return register(model);
            }
            if (userRegistration.registerNewUser(username, password1)) {
                return "registrationdone";
            }
            model.addAttribute("errormsg", "Something went wrong");
            return register(model);
        } else {
            model.addAttribute("errormsg", "We couldn't find this email address in our records");
            return register(model);
        }
    }

    /*info implement after MVP*/
    @RequestMapping(value = "registrationconfirmation", method = RequestMethod.GET)
    public String registrationConfirmed(HttpServletRequest request) {
        //if the token (registrationconfirmation?token=123456ABCD&email=urdans@gmail.com&userid=49&usertype=1) is
        // correct return this page. otherwise redirect to register
        String token = request.getParameter("token");
        String email = request.getParameter("email");
        String userid = request.getParameter("userid");
        String usertype = request.getParameter("usertype");
        if (token.equals("123456ABCD") & email.equals("urdans@gmail.com") & userid.equals("49") & usertype.equals("1")) {
            return "registrationconfirmed";
        }
        return "redirect:register";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logOut(HttpServletRequest request) {
        userSession.endSession(request);
        return "redirect:";
    }
}
