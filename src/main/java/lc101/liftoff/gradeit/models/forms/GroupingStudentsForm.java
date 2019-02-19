package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.Grade;
import lc101.liftoff.gradeit.models.Schedule;
import lc101.liftoff.gradeit.models.data.GradeDao;
import lc101.liftoff.gradeit.models.data.IStudentIdAndName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class GroupingStudentsForm {
    private GradeDao gradeDao;

    public class StudentIdAndName {
        public int studentId;
        public String studentName;
        public List<GradeEvalPair> gradeEvalPairs = new ArrayList<>();

        public StudentIdAndName(int studentId, String studentName) {
            this.studentId = studentId;
            this.studentName = studentName;
        }
    }

    public class GradeEvalPair {
        public Double gradeValue = null;
        public int gradeId; // = null;
//        public Schedule schedule = null;
        public int scheduleId;// = null;



        public GradeEvalPair(Double gradeValue, Integer gradeId, /*Schedule schedule*/ Integer scheduleId) {
            this.gradeValue = gradeValue;
            this.gradeId = gradeId;
//            this.schedule = schedule;
            this.scheduleId = scheduleId;

        }
    }

    private List<Schedule> schedules;

    private List<StudentIdAndName> studentIdAndNameList = new ArrayList<>();

    public List<StudentIdAndName> getStudentIdAndNameList() {
        return studentIdAndNameList;
    }

    public GroupingStudentsForm() {
    }

    public GroupingStudentsForm(List<IStudentIdAndName> studentList, List<Schedule> schedules, GradeDao gradeDao) {
        this.schedules = schedules;
        this.gradeDao = gradeDao;
        for (IStudentIdAndName student : studentList) {
            int studentId = student.getId();
            StudentIdAndName studentIdAndName = new StudentIdAndName(studentId, student.getName());
            double cum = 0;
            boolean atLeastOne= false;
            for(Schedule sch: schedules){
                int schId = sch.getId();
                Grade grade = gradeDao.findFirstByStudentIdAndScheduleId(studentId,schId);
                if(grade == null) {
                    studentIdAndName.gradeEvalPairs.add(new GradeEvalPair(null, 0, sch.getId()));
                }else{
                    studentIdAndName.gradeEvalPairs.add(new GradeEvalPair(grade.getValue(), grade.getId(), sch.getId()));
                    cum = cum + grade.getValue()*sch.getPercentage()/100;
                    atLeastOne = true;
                }
            }
            //inserting the cumulative
            if(!atLeastOne) studentIdAndName.gradeEvalPairs.add(new GradeEvalPair(null, 0, 0));
            else studentIdAndName.gradeEvalPairs.add(new GradeEvalPair(cum, 0, 0));

            studentIdAndNameList.add(studentIdAndName);
        }
//        Grade grade = gradeDao.findFirstByStudentIdAndScheduleId(studentId,schId);
//        if(grade == null) System.out.println("Grade: null");
//        else System.out.println(grade.getId()+ " " + grade.getValue() + " " + grade.getSchedule().getId() + " " + grade.getStudent().getId());
//        for(StudentIdAndName std: studentIdAndNameList){
//            System.out.print(std.studentId+ " " + std.studentName + " ");
//            for(GradeEvalPair grd: std.gradeEvalPairs){
//                System.out.print(grd.gradeValue + "->(" + grd.gradeId + "," + grd.scheduleId + ")");
////                System.out.print(grd.gradeValue + "->(" + grd.gradeId + ",");
////                if(grd.schedule == null) System.out.print("null) ");
////                else System.out.print(grd.schedule.getId() + ")");
//            }
//            System.out.println();
//        }
    }
}
