package lc101.liftoff.gradeit.tools;

import lc101.liftoff.gradeit.models.*;
import lc101.liftoff.gradeit.models.data.RegistrarDao;
import lc101.liftoff.gradeit.models.data.StudentDao;
import lc101.liftoff.gradeit.models.data.TeacherDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component("UserSession")
public class UserSession {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private RegistrarDao registrarDao;

    private UserType userType;
    private int userId;
    private String userName;
    private boolean sessionExists;
    private HttpServletRequest request;
    private HttpSession session;

    public UserSession() {
    }

    /**Checks if a valid session exist
     * @param aRequest The htpservletrequest received by the controller
     * @return true if the request contains a valid session
     */
    public boolean decodeSession(HttpServletRequest aRequest) {
        request = aRequest;
        session = request.getSession(false);
        if (session == null)
            return false;
        try {
            String user_s = (String) session.getAttribute("usertype");
            String id_s = (String) session.getAttribute("userid");

            if (user_s == null || id_s == null)
                return false;

            if (user_s.equals(UserType.STUDENT.toString())) {
                userType = UserType.STUDENT;
            } else if (user_s.equals(UserType.TEACHER.toString())) {
                userType = UserType.TEACHER;
            } else if (user_s.equals(UserType.REGISTRAR.toString())) {
                userType = UserType.REGISTRAR;
            } else {
                return false;
            }

            int id = Integer.parseInt(id_s);
            if (id <= 0)
                return false;

            return userExistById(studentDao, id) || userExistById(teacherDao, id) || userExistById(registrarDao, id);
        } catch (Exception e) {
            return false;
        }
    }

    /**Validates the user's credentials and creates a new session for that user
     * @param aRequest The htpservletrequest received by the controller
     * @param userName The user name
     * @param password The password
     * @return true if the user is validated successfully
     */
    public boolean decodeSession(HttpServletRequest aRequest, String userName, String password) {
        request = aRequest;
        if (userName.equals("") | password.equals(""))
            return false;
        //the user must exist only once in all the three user tables together
        if (validateUser(studentDao, userName, password)) {
            userType = UserType.STUDENT;
            createSession();
            return true;
        }
        if (validateUser(teacherDao, userName, password)) {
            userType = UserType.TEACHER;
            createSession();
            return true;
        }
        if (validateUser(registrarDao, userName, password)) {
            userType = UserType.REGISTRAR;
            createSession();
            return true;
        }
        return false;
    }

    public void endSession(HttpServletRequest aRequest) {
        request = aRequest;
        session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private void createSession() {
        session = request.getSession(true);
        session.setAttribute("usertype", userType.toString());
        session.setAttribute("userid", String.valueOf(userId));
        session.setAttribute("username", String.valueOf(userName));
    }

    public String getSessionUserName(HttpServletRequest aRequest) {
        session = request.getSession(false);
        if (session == null)
            return "";
        else {
            return (String) session.getAttribute("username");
        }
    }

    public boolean isStudent() {
        return userType == UserType.STUDENT;
    }

    public boolean isTeacher() {
        return userType == UserType.TEACHER;
    }

    public boolean isRegistrar() {
        return userType == UserType.REGISTRAR;
    }

    public UserType getUserType() {
        return userType;
    }

    public int getUserId() {
        return userId;
    }

    private boolean userExistById(CrudRepository userDao, int id) {
        return userDao.existsById(id);
    }

    private boolean validateUser(CrudRepository<? extends User, Integer> userDao, String uName, String uPwd) {
        for (User usr : userDao.findAll()) {
            String un = usr.getUserName();
            String psw = usr.getPassword();
            if (un == null || psw == null) continue;
            String salt = HashTools.extractSalt(psw);
            String providedSaltedPassword = HashTools.hashAndSaltPassword(salt, uPwd);
            if (un.equals(uName) & psw.equals(providedSaltedPassword)) {
                userId = usr.getId();
                userName = uName;
                return true;
            }
        }
        return false;
    }
}
