package changs.android.widget.datepick;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by yincs on 2017/2/23.
 */

class DayPickerEngine {
    private static final String TAG = "DayPickerEngine";

    private static final int MONTHS_IN_YEAR = 12;
    private static final int WEEKS_DAY = 7;

    private static final int NODE_START = 1;
    private static final int NODE_MIDDLE = 2;
    private static final int NODE_END = 3;
    private static final int NODE_NORAML = 0;


    private final RecyclerView recyclerView;
    private final DayPickerConfig dayPickerConfig;

    private final Calendar currentDate;
    private final Calendar tempDate;
    private final Calendar minDate;
    private final Calendar maxDate;
    private final MonthAdapter adapter;
    private final Context context;
    private final LayoutInflater inflater;

    private Calendar startCalendar;
    private Calendar endCalendar;
    private DatePicker.OnItemClickListener onItemClickListener;

    //recyclerView嵌套复用item.所以采用用一个item实现
    public DayPickerEngine(Context context, RecyclerView recyclerView, DayPickerConfig dayPickerConfig) {
        this.recyclerView = recyclerView;
        this.dayPickerConfig = dayPickerConfig;
        this.context = context;

        final Locale locale = Locale.getDefault();
        currentDate = Calendar.getInstance(locale);
        tempDate = Calendar.getInstance(locale);
        minDate = Calendar.getInstance(locale);
        maxDate = Calendar.getInstance(locale);

        inflater = LayoutInflater.from(context);
        setupConfig();

        adapter = new MonthAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    public void setStartDay(int startYear, int startMonth, int startDay) {
        if (startCalendar == null) {
            startCalendar = initCalendar(Calendar.getInstance());
        }
        startCalendar.set(startYear, startMonth, startDay);

        check();
    }


    public void setEndDay(int endYear, int endMonth, int endDay) {
        if (endCalendar == null) {
            endCalendar = initCalendar(Calendar.getInstance());
        }
        endCalendar.set(endYear, endMonth, endDay);

        check();
    }


    public void setOnItemClickListener(DatePicker.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void check() {
        if (startCalendar == null
                || endCalendar == null) return;

        if (endCalendar.before(startCalendar)) {
            Log.e(TAG, "End date before the start date");
            return;
        }

        adapter.notifyDataForce();
    }

    private Calendar initCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private void setupConfig() {
        minDate.setTimeInMillis(dayPickerConfig.getMinDate());
        maxDate.setTimeInMillis(dayPickerConfig.getMaxDate());
    }

    private int getMonthForPosition(int position) {
        return (position + minDate.get(Calendar.MONTH)) % MONTHS_IN_YEAR;
    }

    private int getYearForPosition(int position) {
        final int yearOffset = (position + minDate.get(Calendar.MONTH)) / MONTHS_IN_YEAR;
        return yearOffset + minDate.get(Calendar.YEAR);
    }

    private class MonthAdapter extends RecyclerView.Adapter<MonthViewHolder> {

        private int count;

        public MonthAdapter() {
            final int diffYear = maxDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR);
            final int diffMonth = maxDate.get(Calendar.MONTH) - minDate.get(Calendar.MONTH);
            count = diffMonth + MONTHS_IN_YEAR * diffYear + 1;
        }


        int c;

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder = " + (c++));
            return new MonthViewHolder(inflater.inflate(R.layout.item_month, parent, false));
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, int position) {
            final int year = getYearForPosition(position);
            final int month = getMonthForPosition(position);
            holder.mothView.setTime(year, month);
            holder.tvMonth.setText(year + "年" + (month + 1) + "月");
        }

        @Override
        public int getItemCount() {
            return count;
        }

        public void notifyDataForce() {
//            notifyDataSetChanged();
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                MonthViewHolder monthViewHolder = (MonthViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                monthViewHolder.mothView.notifyDataForce();
            }
        }
    }

    private class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth;
        MothView mothView;

        public MonthViewHolder(View itemView) {
            super(itemView);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            mothView = new MothView((ViewGroup) itemView.findViewById(R.id.dayGroup));
        }

    }

    public class MothView {
        private final Calendar calendar = initCalendar(Calendar.getInstance());
        private int firstDayPosition = -1;
        private int monthDays;
        private int year;
        private int month;

        private final ViewGroup[] line = new ViewGroup[6];
        private final DayView[] dayViews = new DayView[7 * 6];

        public MothView(ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                ViewGroup group = (ViewGroup) viewGroup.getChildAt(i);
                line[i] = group;
                for (int j = 0; j < group.getChildCount(); j++) {
                    dayViews[(i * 7) + j] = new DayView(group.getChildAt(j));
                }
            }
        }

        public void setTime(int year, int month) {
            if (firstDayPosition != -1
                    && this.year == year
                    && this.month == month) {
                return;
            }
            this.year = year;
            this.month = month;

            calendar.set(year, month, 1);
            firstDayPosition = (calendar.get(Calendar.DAY_OF_WEEK) - 1);//0是周日
            monthDays = calendar.getActualMaximum(Calendar.DATE);
            Log.d(TAG, "---------------------------------------");
            Log.d(TAG, "year = " + year);
            Log.d(TAG, "month = " + month);
            Log.d(TAG, "firstDayPosition = " + firstDayPosition);
            Log.d(TAG, "monthDays = " + monthDays);

            setupView();
        }

        private void setupView() {
            for (int position = 0; position < dayViews.length; position++) {
                DayView dayView = dayViews[position];
                final int day = position + 1 - firstDayPosition;
                if (position < firstDayPosition) {
                    dayView.itemView.setVisibility(View.INVISIBLE);
                } else if (day > monthDays) {
                    dayView.itemView.setVisibility(View.VISIBLE);
                    dayView.day.setVisibility(View.GONE);
                } else {
                    dayView.itemView.setVisibility(View.VISIBLE);
                    dayView.day.setVisibility(View.VISIBLE);
                }
                if (dayView.day.getVisibility() != View.VISIBLE
                        || dayView.itemView.getVisibility() != View.VISIBLE)
                    continue;

                if (dayView.day.getVisibility() != View.VISIBLE
                        || dayView.itemView.getVisibility() != View.VISIBLE)
                    continue;

                dayView.day.setText(String.valueOf(day));

                setupNode(dayView, day);
            }
        }

        private void setupNode(DayView dayView, int day) {
            final int select = getSelect(day);
            Log.d(TAG, "select = " + year + " " + month + " " + day);
            if (select == NODE_MIDDLE) {
                dayView.day.setBackgroundResource(R.drawable.bg_select_middle);
            } else if (select == NODE_START) {
                dayView.day.setBackgroundResource(R.drawable.bg_select_start);
            } else if (select == NODE_END) {
                dayView.day.setBackgroundResource(R.drawable.bg_select_end);
            } else {
                dayView.day.setBackgroundResource(R.drawable.bg_select_normal);
            }
        }


        public int getSelect(int day) {
            calendar.set(Calendar.DATE, day);
            final int start = calendar.compareTo(startCalendar);
            if (start < 0) return NODE_NORAML;
            if (start == 0) return NODE_START;
            final int end = calendar.compareTo(endCalendar);
            if (end > 0) return NODE_NORAML;
            if (end == 0) return NODE_END;
            return NODE_MIDDLE;
        }

        public void notifyDataForce() {
            for (int position = 0; position < dayViews.length; position++) {
                DayView dayView = dayViews[position];
                if (dayView.day.getVisibility() != View.VISIBLE
                        || dayView.itemView.getVisibility() != View.VISIBLE)
                    continue;
                final int day = position + 1 - firstDayPosition;
                setupNode(dayView, day);
            }
        }

        private class DayView implements View.OnClickListener {
            final View itemView;
            final TextView day;

            public DayView(View itemView) {
                this.itemView = itemView;
                day = (TextView) itemView.findViewById(R.id.tv_day);
                day.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItem(year, month, getPositionInDays() + 1 - firstDayPosition);
            }

            public int getPositionInDays() {
                for (int i = 0; i < dayViews.length; i++) {
                    if (dayViews[i] == this)
                        return i;
                }
                throw new IllegalStateException();
            }
        }
    }


}
