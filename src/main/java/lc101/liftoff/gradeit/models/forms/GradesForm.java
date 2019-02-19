package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.data.IGrade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GradesForm {
	public class StudentGrades{
		public Date date;
		public String description;
		public Double percentage;
		public Double gradeValue;
		public Integer gradeId;
		public int scheduleId;

		public StudentGrades(Date date, String description, Double percentage, Double gradeValue, Integer gradeId, int scheduleId) {
			this.date = date;
			this.description = description;
			this.percentage = percentage;
			this.gradeValue = gradeValue;
			this.gradeId = gradeId;
			this.scheduleId =scheduleId;
		}
	}
	public List<StudentGrades> grades = new ArrayList<>();

	public GradesForm(List<IGrade> iGrades) {
		for(IGrade iGrade : iGrades){
			grades.add(new StudentGrades(iGrade.getDate(), iGrade.getDescription(), iGrade.getPercentage(), iGrade.getGradeValue(),
					iGrade.getGradeId(), iGrade.getScheduleId()));
		}
	}
}
