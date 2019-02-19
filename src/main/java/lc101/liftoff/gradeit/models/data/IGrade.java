package lc101.liftoff.gradeit.models.data;

import java.util.Date;

public interface IGrade {
	Date getDate();
	String getDescription();
	Double getPercentage();
	Double getGradeValue();
	Integer getGradeId();
	int getScheduleId();
}
