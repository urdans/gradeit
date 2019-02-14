package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.Teacher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//@Component("TeacherForm")
public class TeacherForm {
    public int id;
    public String name;

    public TeacherForm(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<TeacherForm> getTeacherFormList(Iterable<Teacher> teachers) {
        List<TeacherForm> teacherFormList = new ArrayList<>();
        for(Teacher teacher: teachers) {
            teacherFormList.add(new TeacherForm(teacher.getId(),teacher.getFirstName() + " " + teacher.getLastName()));
        }
        return  teacherFormList;
    }
}
