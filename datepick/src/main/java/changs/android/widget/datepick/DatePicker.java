package changs.android.widget.datepick;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by yincs on 2017/2/23.
 */

public class DatePicker extends FrameLayout {

    //    private final TextView[] weeks = new TextView[7];
    private static final String[] weeksName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    private final DayPickerEngine dayPickerEngine;

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.date_picker_material, this, true);

        ViewGroup llWeek = (ViewGroup) findViewById(R.id.ll_week);
        for (int i = 0; i < llWeek.getChildCount(); i++) {
            final TextView weekName = (TextView) llWeek.getChildAt(i);
            weekName.setText(weeksName[i]);
        }

        RecyclerView rccView = (RecyclerView) findViewById(R.id.rccView);
        dayPickerEngine = new DayPickerEngine(context, rccView);


        int monthCount = 12;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePicker, 0, 0);
            if (a.hasValue(R.styleable.DatePicker_month_count)) {
                monthCount = a.getInt(R.styleable.DatePicker_month_count, 12);
            }
            a.recycle();
        }
        setDefaultDay(monthCount);
    }

    private void setDefaultDay(int monthCount) {
        monthCount -= 1;
        if (monthCount >= 0) {
            dayPickerEngine.getMaxDate().add(Calendar.MONTH, monthCount);
            dayPickerEngine.setupAdapter();
        }
    }

    public void setStartDay(int startYear, int startMonth, int startDay) {
        dayPickerEngine.setDay(startYear, startMonth, startDay, -1, -1, -1);
    }

    public void setEndDay(int endYear, int endMonth, int endDay) {
        dayPickerEngine.setDay(-1, -1, -1, endYear, endMonth, endDay);
    }

    public void setDay(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        dayPickerEngine.setDay(startYear, startMonth, startDay, endYear, endMonth, endDay);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        dayPickerEngine.setOnItemClickListener(onItemClickListener);
    }


    public Calendar getMinDate() {
        return dayPickerEngine.getMinDate();
    }

    public Calendar getMaxDate() {
        return dayPickerEngine.getMaxDate();
    }

    /**
     * 开始更新日历
     */
    public void updateDate() {
        dayPickerEngine.setupAdapter();
    }

    public interface OnItemClickListener {
        void onItem(int year, int month, int day);
    }
}
