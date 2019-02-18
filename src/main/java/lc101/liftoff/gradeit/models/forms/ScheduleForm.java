package lc101.liftoff.gradeit.models.forms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleForm {
    public String date; //not using Date type as I don't have control over the conversion. I'm providing extractDate for getting the Date
    public double percentage;
    public String description;
    public int groupingId;
    public int scheduleId;

    public Date extractDate() {
        Date d;
        try {
            d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
        }
        catch (Exception e) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            d = today.getTime();
        }
        return d;
    }
}
