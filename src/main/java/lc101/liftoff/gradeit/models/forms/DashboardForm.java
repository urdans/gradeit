package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.Grouping;
import lc101.liftoff.gradeit.models.data.GroupingDao;
import lc101.liftoff.gradeit.models.data.IGrade;
import lc101.liftoff.gradeit.models.data.ScheduleDao;
import lc101.liftoff.gradeit.models.data.StudentDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardForm {
	public class DashboardItem {
		public int groupingId;
		public String subjectName;
		public Date lastEval;
		public String description;
		public double percentage;
		public double gradeValue;
		public double cumulative;

		public DashboardItem(int groupingId, String subjectName, Date lastEval, String description, double percentage, double gradeValue,
		                     double cumulative) {
			this.groupingId = groupingId;
			this.subjectName = subjectName;
			this.lastEval = lastEval;
			this.description = description;
			this.percentage = percentage;
			this.gradeValue = gradeValue;
			this.cumulative = cumulative;
		}
	}

	public List<DashboardItem> dashboardItemList = new ArrayList<>();

	public DashboardForm(int studentId, StudentDao studentDao, GroupingDao groupingDao, ScheduleDao scheduleDao) {
		int groupId = studentDao.findGroupIdById(studentId);
		List<Grouping> groupingList = groupingDao.findAllByGroupId(groupId);
		for (Grouping grouping : groupingList) {
			Date lastEval = null;
			String description = null;
			double percentage = 0;
			double gradeValue = 0;
			double cumulative = 0;
			boolean lastWasFound = false;
			List<IGrade> gradeList = scheduleDao.getGradesByGroupingAndStudent(grouping.getId(), studentId);

			for (int i = gradeList.size() - 1; i >= 0; i--) {
				IGrade grade = gradeList.get(i);

				if (grade.getGradeValue() != null && !lastWasFound) {
					lastWasFound = true;
					lastEval = grade.getDate();
					description = grade.getDescription();
					percentage = grade.getPercentage();
					gradeValue = grade.getGradeValue();
				}

				double gv = 0;

				if (grade.getGradeValue() != null) gv = grade.getGradeValue();

				cumulative = cumulative + gv * grade.getPercentage() / 100;
			}
			dashboardItemList.add(new DashboardItem(grouping.getId(), grouping.getSubject().getName(),
					lastEval, description, percentage, gradeValue, cumulative));
		}
	}
}
