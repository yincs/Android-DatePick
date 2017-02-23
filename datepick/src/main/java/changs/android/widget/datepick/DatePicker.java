package changs.android.widget.datepick;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
        dayPickerEngine = new DayPickerEngine(context, rccView, new DayPickerConfig());
    }

}
