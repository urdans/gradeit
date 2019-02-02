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

    /*done refactor using User interface semantics*/
    public boolean emailIsInRecords(String email) {
        if(_emailIsInRecords(studentDao, email)) {
            userType = UserType.STUDENT;
            return true;
        }
        if(_emailIsInRecords(teacherDao, email)) {
            userType = UserType.TEACHER;
            return true;
        }
        userInfoSet = false;
        return false;
    }

    public boolean userIsRegistered() {
        getUserType();
        return userRegistered;
    }

    /*done refactor using User interface semantics*/
    public boolean userNameIsTaken(String userName) {
        if(_userNameIsTaken(studentDao, userName)) return true;
        if(_userNameIsTaken(teacherDao, userName)) return true;
        if(_userNameIsTaken(registrarDao, userName)) return true;
        return false;
    }

    public boolean registerNewUser(String userName, String Password) {
        int ut;
        /*done refactor using User interface semantics*/
        if(getUserType() == UserType.STUDENT) {
            if (_registerNewUser(studentDao, userName, Password)) ut = 1;
            else return false;
        } else {
            if(_registerNewUser(teacherDao, userName, Password)) ut = 2;
            else return false;
        }
        String token = HashTools.hash(userName + userEmail + String.valueOf(userId) + String.valueOf(ut));
        String link = "http://localhost:8080/registrationconfirmation?token=" + token + "&email=" + userEmail +
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

    /*done refactoring using User interface semantics*/
    private boolean _emailIsInRecords(CrudRepository<? extends User, Integer> userDao, String email) {
        for(User usr: userDao.findAll()) {
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

    /*done refactoring using User interface semantics*/
    private boolean _userNameIsTaken(CrudRepository<? extends User, Integer> userDao, String userName) {
        for(User usr: userDao.findAll()) {
            String un = usr.getUserName();
            if(un == null) continue;
            if(un.equals(userName)) return true;
        }
        return false;
    }

    private boolean _registerNewUser(CrudRepository<? extends User, Integer> userDao, String userName, String Password) {
        Optional<User> orUser = (Optional<User>) userDao.findById(userId);
        if(orUser.isPresent()) {
            User rUser = orUser.get();
            rUser.setUserName(userName);
            rUser.setPassword(hashAndSaltPassword(Password));
            rUser.setConfirmed(true); /*todo true for MVP only. Delete this line after MVP*/
            if(userDao instanceof StudentDao)
                studentDao.save((Student)rUser);        //this is a hack, as .save can't infer what rUser is
            else if(userDao instanceof TeacherDao)
                teacherDao.save((Teacher)rUser);
            return true;
        }
        else {
            return false; //this should never happen
        }
    }
}