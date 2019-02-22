package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.data.StudentDao;
import lc101.liftoff.gradeit.models.forms.UserProfileForm;
import lc101.liftoff.gradeit.tools.UserRegistration;
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

import static lc101.liftoff.gradeit.tools.HashTools.hashAndSaltPassword;

@Controller
@RequestMapping(value = "student")
public class StudentController {
	@Autowired
	UserRegistration userRegistration;

	@Autowired
	UserSession userSession;

	@Autowired
	StudentDao studentDao;

	public boolean studentLoggedIn(HttpServletRequest request) {
		return (userSession.decodeSession(request)) & (userSession.isStudent());
	}

	@RequestMapping(value = "grades", method = RequestMethod.GET)
	public String studentGrades(Model model, HttpServletRequest request) {

		if (!studentLoggedIn(request)) return "redirect:/";

		model.addAttribute("username", userSession.getSessionUserName(request));
		return "studentgrades";
	}

	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public String studentProfile(Model model, HttpServletRequest request, @ModelAttribute @Valid UserProfileForm userProfileForm,
	                             Errors errors) {

		if (!studentLoggedIn(request)) return "redirect:/";

		if (request.getMethod().equals("GET")) { //first time calling
			userProfileForm.setUserData(userSession.getUserAsStudent());
		} else if (!errors.hasErrors()) {//it's a POST

			Student student = studentDao.findById(userProfileForm.getId()).orElse(null);

			if (student == null) {

				model.addAttribute("msg", "Student not found!");

			} else if (userProfileForm.getId() != userSession.getUserId()) {

				model.addAttribute("msg", "No hacking permitted!");

			} else if (!userProfileForm.getPassword().equals(userProfileForm.getPassword2())) {

				errors.rejectValue("password", "0", "Passwords don't match. Please try again!");
				errors.rejectValue("password2", "1", "Passwords don't match. Please try again!");

			} else if (!userRegistration.emailGrammarIsOk(userProfileForm.getEmail())) {

				errors.rejectValue("email", "2", "Invalid email address. Please try again!");

			} else if (!userRegistration.isUserNameAvailable(student, userProfileForm.getUserName())) {

				errors.rejectValue("userName", "3", "This user name is not available.");

			} else {
				student.setFirstName(userProfileForm.getFirstName());
				student.setLastName(userProfileForm.getLastName());
				student.setEmail(userProfileForm.getEmail());
				student.setUserName(userProfileForm.getUserName());
				student.setPassword(hashAndSaltPassword(userProfileForm.getPassword()));
				student.setAddress(userProfileForm.getAddress());
				student.setPhoneNumber(userProfileForm.getPhoneNumber());
				studentDao.save(student);
				model.addAttribute("msg", "Profile was properly updated!");
				userSession.updateSessionUserName(request, userProfileForm.getUserName());
			}
		}
		model.addAttribute("username", userSession.getSessionUserName(request));
		return "studentprofile";
	}

	@RequestMapping(value = "profile", method = RequestMethod.POST)
	public String studentUpdateProfile(Model model, HttpServletRequest request,
	                                   @ModelAttribute @Valid UserProfileForm userProfileForm, Errors errors) {
		return studentProfile(model, request, userProfileForm, errors);
	}
}
