package changs.android.widget.datepick;

import java.util.Calendar;

/**
 * Created by yincs on 2017/2/23.
 */

public class DayPickerConfig {

    private long minDate;
    private long maxDate;

    public DayPickerConfig() {
    }

    public long getMinDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 0, 23);
        System.out.println("calendar = " + calendar.get(Calendar.YEAR));
        System.out.println("calendar = " + (calendar.get(Calendar.MONTH) + 1));
        System.out.println("calendar = " + calendar.get(Calendar.DATE));

        final int maximum = calendar.getActualMaximum(Calendar.DATE);
        System.out.println("calendar = " + maximum);

        calendar.set(Calendar.DATE, 1);
        final int week = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("calendar = " + week);
        return calendar.getTimeInMillis();
    }

    public void setMinDate(long minDate) {
        this.minDate = minDate;
    }

    public long getMaxDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 8, 23);
        return calendar.getTimeInMillis();
    }

    public void setMaxDate(long maxDate) {
        this.maxDate = maxDate;
    }
}
