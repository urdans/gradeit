package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.data.IStudentIdAndName;

import java.util.ArrayList;
import java.util.List;

public class GroupingStudentsForm {
    public class StudentIdAndName {
        public int studentId;
        public String studentName;

        public StudentIdAndName(int studentId, String studentName) {
            this.studentId = studentId;
            this.studentName = studentName;
        }
    }

    private List<StudentIdAndName> studentIdAndNameList = new ArrayList<>();

    public List<StudentIdAndName> getStudentIdAndNameList() {
        return studentIdAndNameList;
    }

    public GroupingStudentsForm(List<IStudentIdAndName> studentList) {
        for (IStudentIdAndName student : studentList) {
            studentIdAndNameList.add(new StudentIdAndName(student.getId(), student.getName()));
        }
    }
}
