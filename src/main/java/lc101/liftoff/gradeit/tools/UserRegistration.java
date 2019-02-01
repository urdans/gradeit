package lc101.liftoff.gradeit.tools;

import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.User;
import lc101.liftoff.gradeit.models.data.RegistrarDao;
import lc101.liftoff.gradeit.models.data.StudentDao;
import lc101.liftoff.gradeit.models.data.TeacherDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Optional;

import static lc101.liftoff.gradeit.tools.HashTools.hashAndSaltPassword;

@Component("UserRegistration")
public class UserRegistration {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private RegistrarDao registrarDao;

    private UserType userType;
    private boolean userInfoSet = false;
    private boolean userRegistered = false;
    private int userId = 0;
    private String userEmail ="";

    public UserRegistration(){}

    public boolean emailGrammarIsOk(String email) {
        return EmailValidator.getInstance(true).isValid(email);
    }

    public boolean emailIsInRecords(String email) {
//        for(Student std: studentDao.findAll()) {/*todo refactor using User interface semantics*/
//            String eml = std.getEmail();
//            if(eml == null) continue;
//            if(eml.equals(email)) {
//                userType = UserType.STUDENT;
//                userRegistered = std.isConfirmed();
//                userId = std.getId();
//                userEmail = email;
//                userInfoSet = true;
//                return true;
//            }
//        }
        if(emailExists(studentDao, email)) {
            userType = UserType.STUDENT;
            return true;
        }
        if(emailExists(teacherDao, email)) {
            userType = UserType.TEACHER;
            return true;
        }

//        for(Teacher tch: teacherDao.findAll()) {
//            String eml = tch.getEmail();
//            if(eml == null) continue;
//            if(eml.equals(email)) {
//                userType = UserType.TEACHER;
//                userRegistered = tch.isConfirmed();
//                userId = tch.getId();
//                userEmail = email;
//                userInfoSet = true;
//                return true;
//            }
//        }
        userInfoSet = false;
        return false;
    }

    public boolean userIsRegistered() {
        getUserType();
        return userRegistered;
    }

    public boolean userNameIsTaken(String username) {
        for(Student std: studentDao.findAll()) {/*todo refactor using User interface semantics*/
            String un = std.getUserName();
            if(un == null) continue;
            if(un.equals(username)) {
                return true;
            }
        }
        for(Teacher tch: teacherDao.findAll()) {
            String un = tch.getUserName();
            if(un == null) continue;
            if(un.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean registerNewUser(String userName, String password) {
        String token;
        String link;
        int ut;

        if(getUserType() == UserType.STUDENT) {   //check that the info about the user has been set
            Optional<Student> orStudent = studentDao.findById(userId); /*todo refactor using User interface semantics*/
            if(orStudent.isPresent()) {
                Student rStudent = orStudent.get();
                rStudent.setUserName(userName);
                rStudent.setPassword(hashAndSaltPassword(password));
                rStudent.setConfirmed(true); /*todo for MVP. Change to false after MVP*/
                studentDao.save(rStudent);
            }
            else {
                return false;
            }
            ut = 1;
        }
        else {
            Optional<Teacher> orTeacher = teacherDao.findById(userId);
            if(orTeacher.isPresent()) {
                Teacher rTeacher = orTeacher.get();
                rTeacher.setUserName(userName);
                rTeacher.setPassword(hashAndSaltPassword(password));
                rTeacher.setConfirmed(true); /*todo for MVP. Change to false after MVP*/
                teacherDao.save(rTeacher);
            }
            else {
                return false;
            }

            ut = 2;
        }
        token = HashTools.hash(userName + userEmail + String.valueOf(userId) + String.valueOf(ut));
        link = "http://localhost:8080/registrationconfirmation?token=" + token + "&email=" + userEmail +
                "&userid=" + String.valueOf(userId) + "&usertype=" + String.valueOf(ut);
        /*todo for after MVP: Send and email with the link to the user email address*/
        return true;
    }

    public UserType getUserType() {
        if(userInfoSet){
            return userType;
        }
        throw new RuntimeException("userInfoSet is not set. You must call emailIsInRecords() before using getUserType()");
    }

    private boolean emailExists(CrudRepository<? extends User, Integer> userDao, String email) {
        for(User usr: userDao.findAll()) {/*todo refactoring using User interface semantics*/
            String eml = usr.getEmail();
            if(eml == null) continue;
            if(eml.equals(email)) {
                userRegistered = usr.isConfirmed();
                userId = usr.getId();
                userEmail = email;
                userInfoSet = true;
                return true;
            }
        }
        return false;
    }
}
