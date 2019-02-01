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
    private boolean sessionExists;
    private HttpServletRequest request;
    private HttpSession session;

    public UserSession(){
    }

    public boolean decodeSession(HttpServletRequest aRequest){
        request = aRequest;
        session = request.getSession(false);
        if(session==null)
            return false;
        try {
            String user_s = (String)session.getAttribute("usertype");
            String id_s = (String)session.getAttribute("userid");

            if( user_s==null || id_s==null)
                return false;

            if(user_s.equals(UserType.STUDENT.toString())) {
                userType = UserType.STUDENT;
            } else if(user_s.equals(UserType.TEACHER.toString())){
                userType = UserType.TEACHER;
            } else if(user_s.equals(UserType.REGISTRAR.toString())){
                userType = UserType.REGISTRAR;
            } else {
                return false;
            }

            int id = Integer.parseInt(id_s);
            if(id<=0)
                return false;

            //checking if the user id exists in the corresponding table
            /*New refactored code @1*/
            return userExistById(studentDao, id) || userExistById(teacherDao, id) || userExistById(registrarDao, id);


            /* code under refactoring @1
            if(userType==UserType.STUDENT){
                return studentDao.existsById(id);
            }
            if(userType==UserType.TEACHER){
                return teacherDao.existsById(id);
            }
            if(userType==UserType.REGISTRAR){
                return registrarDao.existsById(id);
            }*/
        }
        catch (Exception e) {
            return false;
        }
//        return false; //@1
    }

    public boolean decodeSession(HttpServletRequest aRequest, String userName, String password){
        request = aRequest;
        if(userName.equals("") | password.equals(""))
            return false;
        //the user must exist only once in all the three user tables together
//        for(Student std: studentDao.findAll()){ /*done refactor using User interface semantics*/
//            String un = std.getUserName();
//            String psw = std.getPassword();
//            if(un == null || psw == null) continue;
//            String salt = HashTools.extractSalt(psw);
//            String providedSaltedPassword = HashTools.hashAndSaltPassword(salt, password);
//            if(un.equals(userName) & psw.equals(providedSaltedPassword)){
//                userType = UserType.STUDENT;
//                userId =  std.getId();
//                createSession();
//                return true;
//            }
//        }


        if(validateUser(studentDao, userName, password)) {
            userType = UserType.STUDENT;
            createSession();
            return true;
        }
        if(validateUser(teacherDao, userName, password)) {
            userType = UserType.TEACHER;
            createSession();
            return true;
        }
        if(validateUser(registrarDao, userName, password)) {
            userType = UserType.REGISTRAR;
            createSession();
            return true;
        }

//        for(Teacher tch: teacherDao.findAll()){
//            String un = tch.getUserName();
//            String psw = tch.getPassword();
//            if(un == null || psw == null) continue;
//            String salt = HashTools.extractSalt(psw);
//            String providedSaltedPassword = HashTools.hashAndSaltPassword(salt, password);
//            if(un.equals(userName) & psw.equals(providedSaltedPassword)){
//                userType = UserType.TEACHER;
//                userId =  tch.getId();
//                createSession();
//                return true;
//            }
//        }
//        for(Registrar rgt: registrarDao.findAll()){
//            String un = rgt.getUserName();
//            String psw = rgt.getPassword();
//            if(un == null || psw == null) continue;
//            String salt = HashTools.extractSalt(psw);
//            String providedSaltedPassword = HashTools.hashAndSaltPassword(salt, password);
//            if(un.equals(userName) & psw.equals(providedSaltedPassword)){
//                userType = UserType.REGISTRAR;
//                userId =  rgt.getId();
//                createSession();
//                return true;
//            }
//        }
        return false;
    }

    public void endSession(HttpServletRequest aRequest){
        request = aRequest;
        session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
    }

    private void createSession(){
        session = request.getSession(true);
        session.setAttribute("usertype", userType.toString());
        session.setAttribute("userid",String.valueOf(userId));
    }

    public boolean isStudent(){
        return userType==UserType.STUDENT;
    }

    public boolean isTeacher(){
        return userType==UserType.TEACHER;
    }

    public boolean isRegistrar(){
        return userType==UserType.REGISTRAR;
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
        for(User usr: userDao.findAll()){
            String un = usr.getUserName();
            String psw = usr.getPassword();
            if(un == null || psw == null) continue;
            String salt = HashTools.extractSalt(psw);
            String providedSaltedPassword = HashTools.hashAndSaltPassword(salt, uPwd);
            if(un.equals(uName) & psw.equals(providedSaltedPassword)){
                userId = usr.getId();
                return true;
            }
        }
        return false;
    }
}
