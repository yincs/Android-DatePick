package changs.android.widget.datepick;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yincs on 2017/2/23.
 */

public class DayView extends TextView {

    public DayView(Context context) {
        super(context);
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
