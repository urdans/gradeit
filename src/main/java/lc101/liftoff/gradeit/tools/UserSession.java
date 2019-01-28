package lc101.liftoff.gradeit.tools;

import lc101.liftoff.gradeit.models.Registrar;
import lc101.liftoff.gradeit.models.Student;
import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.data.RegistrarDao;
import lc101.liftoff.gradeit.models.data.StudentDao;
import lc101.liftoff.gradeit.models.data.TeacherDao;
import org.springframework.beans.factory.annotation.Autowired;
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

    public static enum UserType{
        STUDENT, TEACHER, REGISTRAR
    }

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
            if(userType==UserType.STUDENT){
                return studentDao.existsById(id);
            }
            if(userType==UserType.TEACHER){
                return teacherDao.existsById(id);
            }
            if(userType==UserType.REGISTRAR){
                return registrarDao.existsById(id);
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean decodeSession(HttpServletRequest aRequest, String userName, String password){
        request = aRequest;
        if(userName.equals("") | password.equals(""))
            return false;
        /*
        TODO: encode password here
        */

        //the user must exist only once in all the three user tables together
        for(Student std: studentDao.findAll()){
            if(std.getUserName().equals(userName) & std.getPassword().equals(password)){
                userType = UserType.STUDENT;
                userId =  std.getId();
                createSession();
                return true;
            }
        }
        for(Teacher tch: teacherDao.findAll()){
            if(tch.getUserName().equals(userName) & tch.getPassword().equals(password)){
                userType = UserType.TEACHER;
                userId =  tch.getId();
                createSession();
                return true;
            }
        }
        for(Registrar rgt: registrarDao.findAll()){
            if(rgt.getUserName().equals(userName) & rgt.getPassword().equals(password)){
                userType = UserType.REGISTRAR;
                userId =  rgt.getId();
                createSession();
                return true;
            }
        }
        return false;
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
}
