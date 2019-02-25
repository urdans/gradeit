package lc101.liftoff.gradeit.controllers;

import lc101.liftoff.gradeit.models.Schedule;
import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.data.*;
import lc101.liftoff.gradeit.models.forms.GroupSubjectPairForm;
import lc101.liftoff.gradeit.models.forms.GroupingStudentsForm;
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
import java.util.List;

import static lc101.liftoff.gradeit.tools.HashTools.hashAndSaltPassword;

@Controller
@RequestMapping(value = "teacher")
public class TeacherController {
	@Autowired
	private UserRegistration userRegistration;

	@Autowired
	private UserSession userSession;

	@Autowired
	private TeacherDao teacherDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private ScheduleDao scheduleDao;

	@Autowired
	private GradeDao gradeDao;

	/*todo manage all the cases where there is no data yet, especially in the select elements in the html*/

	public boolean teacherLoggedIn(HttpServletRequest request) {
		return (userSession.decodeSession(request)) & (userSession.isTeacher());
	}

	@RequestMapping(value = "roster", method = RequestMethod.GET)
	public String teacherRoster(Model model, HttpServletRequest request,
	                            @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm) {

		if (!teacherLoggedIn(request)) return "redirect:/";

		groupSubjectPairForm.setGroupSubjectPairs(teacherDao.teacherGroupsAndSubjects(userSession.getUserId()));
		List<Schedule> schedules = scheduleDao.findAllByGroupingIdOrderByDateAsc(groupSubjectPairForm.getSelectedPair());
		model.addAttribute("schedules", schedules);
		model.addAttribute("studentlist", new GroupingStudentsForm(
				studentDao.getGroupingStudents(groupSubjectPairForm.getSelectedPair()), schedules, gradeDao).getStudentIdAndNameList());
		model.addAttribute("username", userSession.getSessionUserName(request));
		model.addAttribute("title", "GradeIt-roster");
		return "teacher/teacherroster";
	}

	@RequestMapping(value = "roster", method = RequestMethod.POST)
	public String teacherRosterApplyFilter(Model model, HttpServletRequest request,
	                                       @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm) {
		return teacherRoster(model, request, groupSubjectPairForm);
	}

	@RequestMapping(value = "schedules", method = RequestMethod.GET)
	public String teacherSchedules(Model model, HttpServletRequest request,
	                               @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm) {

		if (!teacherLoggedIn(request)) return "redirect:/";

		groupSubjectPairForm.setGroupSubjectPairs(teacherDao.teacherGroupsAndSubjects(userSession.getUserId()));
		List<Schedule> schedules = scheduleDao.findAllByGroupingIdOrderByDateAsc(groupSubjectPairForm.getSelectedPair());
		model.addAttribute("schedules", schedules);
		model.addAttribute("username", userSession.getSessionUserName(request));
		model.addAttribute("title", "GradeIt-schedules");

		return "teacher/teacherschedules";
	}

	@RequestMapping(value = "schedules", method = RequestMethod.POST)
	public String teacherScheduleApplyFilter(Model model, HttpServletRequest request,
	                                         @ModelAttribute @Valid GroupSubjectPairForm groupSubjectPairForm) {
		return teacherSchedules(model, request, groupSubjectPairForm);
	}

	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public String teacherProfile(Model model, HttpServletRequest request, @ModelAttribute @Valid UserProfileForm userProfileForm,
	                             Errors errors) {

		if (!teacherLoggedIn(request)) return "redirect:/";

		if (request.getMethod().equals("GET")) { //first time calling
			userProfileForm.setUserData(userSession.getUserAsTeacher());
		} else if (!errors.hasErrors()) {//it's a POST

			Teacher teacher = teacherDao.findById(userProfileForm.getId()).orElse(null);

			if (teacher == null) {

				model.addAttribute("msg", "Teacher not found!");

			} else if (userProfileForm.getId() != userSession.getUserId()) {

				model.addAttribute("msg", "No hacking permitted!");

			} else if (!userProfileForm.getPassword().equals(userProfileForm.getPassword2())) {

				errors.rejectValue("password", "0", "Passwords don't match. Please try again!");
				errors.rejectValue("password2", "1", "Passwords don't match. Please try again!");

			} else if (!userRegistration.emailGrammarIsOk(userProfileForm.getEmail())) {

				errors.rejectValue("email", "2", "Invalid email address. Please try again!");

			} else if (!userRegistration.isUserNameAvailable(teacher, userProfileForm.getUserName())) {

				errors.rejectValue("userName", "3", "This user name is not available.");

			} else {
				teacher.setFirstName(userProfileForm.getFirstName());
				teacher.setLastName(userProfileForm.getLastName());
				teacher.setEmail(userProfileForm.getEmail());
				teacher.setUserName(userProfileForm.getUserName());
				teacher.setPassword(hashAndSaltPassword(userProfileForm.getPassword()));
				teacher.setPhoneNumber(userProfileForm.getPhoneNumber());
				teacherDao.save(teacher);
				model.addAttribute("msg", "Profile was properly updated!");
				userSession.updateSessionUserName(request, userProfileForm.getUserName());
			}
		}
		model.addAttribute("username", userSession.getSessionUserName(request));
		model.addAttribute("title", "GradeIt-profile");
		return "teacher/teacherprofile";
	}

	@RequestMapping(value = "profile", method = RequestMethod.POST)
	public String teacherUpdateProfile(Model model, HttpServletRequest request,
	                                   @ModelAttribute @Valid UserProfileForm userProfileForm, Errors errors) {
		return teacherProfile(model, request, userProfileForm, errors);
	}
}
