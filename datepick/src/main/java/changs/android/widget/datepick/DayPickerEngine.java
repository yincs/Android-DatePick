package changs.android.widget.datepick;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private static final int NODE_NORMAL = 0;


    private final Calendar currentDate;
    private final Calendar minDate;
    private final Calendar maxDate;
    private final MonthAdapter adapter;
    private final Context context;
    private final LayoutInflater inflater;

    private Calendar startCalendar;
    private Calendar endCalendar;
    private DatePicker.OnItemClickListener onItemClickListener;

    DayPickerEngine(Context context, RecyclerView recyclerView) {
        this.context = context;

        final Locale locale = Locale.getDefault();
        currentDate = Calendar.getInstance(locale);
        minDate = Calendar.getInstance(locale);
        maxDate = Calendar.getInstance(locale);

        inflater = LayoutInflater.from(context);

        adapter = new MonthAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    void setDay(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        final boolean nSetStartDate = startYear == -1 && startMonth == -1 && startDay == -1;
        if (nSetStartDate && startCalendar == null) {
            Log.e(TAG, "请先设置开始时间");
            return;
        }
        if (startCalendar == null) {
            startCalendar = initCalendar(Calendar.getInstance());
        }

        boolean startNoChange = startCalendar.get(Calendar.YEAR) == startYear && startCalendar.get(Calendar.MONTH) == startMonth && startCalendar.get(Calendar.DATE) == startDay;
        if (!nSetStartDate && !startNoChange) {
            startCalendar.set(startYear, startMonth, startDay);
        }

        final boolean nSetEndDate = endYear == -1 && endMonth == -1 && endDay == -1;
        boolean endNoChange = true;
        if (!nSetEndDate) {
            if (endCalendar == null) {
                endCalendar = initCalendar(Calendar.getInstance());
            }
            endNoChange = endCalendar.get(Calendar.YEAR) == endYear && endCalendar.get(Calendar.MONTH) == endMonth && endCalendar.get(Calendar.DATE) == endDay;
            if (!endNoChange)
                endCalendar.set(endYear, endMonth, endDay);
        }
        if (!endNoChange && endCalendar.before(startCalendar)) {
            endCalendar.set(startYear, startMonth, startDay);
        }

        if (!startNoChange || !endNoChange)
            adapter.notifyDataForce();
    }


    void setOnItemClickListener(DatePicker.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    private Calendar initCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    Calendar getMinDate() {
        return minDate;
    }

    Calendar getMaxDate() {
        return maxDate;
    }

    void setupAdapter() {
        adapter.setup();
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
        private List<MonthViewHolder> holderPool = new ArrayList<>();


        MonthAdapter() {

        }

        private void setup() {
            final int diffYear = maxDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR);
            final int diffMonth = maxDate.get(Calendar.MONTH) - minDate.get(Calendar.MONTH);
            count = diffMonth + MONTHS_IN_YEAR * diffYear + 1;
        }


        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final MonthViewHolder viewHolder = new MonthViewHolder(inflater.inflate(R.layout.item_month, parent, false));
            holderPool.add(viewHolder);
            return viewHolder;
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

        void notifyDataForce() {
            for (int i = 0; i < holderPool.size(); i++) {
                MonthViewHolder monthViewHolder = holderPool.get(i);
                monthViewHolder.mothView.notifyDataForce();
            }
        }
    }

    private class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth;
        MothView mothView;

        MonthViewHolder(View itemView) {
            super(itemView);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            mothView = new MothView((ViewGroup) itemView.findViewById(R.id.dayGroup));
        }

    }

    private class MothView {
        private final Calendar calendar = initCalendar(Calendar.getInstance());
        private int firstDayPosition = -1;
        private int monthDays;
        private int year;
        private int month;

        private final DayView[] dayViews = new DayView[7 * 6];

        MothView(ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                ViewGroup group = (ViewGroup) viewGroup.getChildAt(i);
                for (int j = 0; j < group.getChildCount(); j++) {
                    dayViews[(i * 7) + j] = new DayView(group.getChildAt(j));
                }
            }
        }

        void setTime(int year, int month) {
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


                calendar.set(Calendar.DATE, day);

                if (calendar.before(currentDate)) {
                    dayView.day.setTextColor(context.getResources().getColor(R.color.tv_expire));
                }
                setupNode(dayView);
            }
        }

        private void setupNode(DayView dayView) {
            final int select = getSelect();
            if (select == NODE_MIDDLE) {
                dayView.day.setBackgroundResource(R.drawable.bg_select_middle);
                dayView.day.setTextColor(context.getResources().getColor(R.color.tv_normal));
            } else if (select == NODE_START) {
                dayView.day.setBackgroundResource(R.drawable.bg_select_start);
                dayView.day.setTextColor(context.getResources().getColor(R.color.tv_node));
            } else if (select == NODE_END) {
                dayView.day.setBackgroundResource(R.drawable.bg_select_end);
                dayView.day.setTextColor(context.getResources().getColor(R.color.tv_node));
            } else {
                dayView.day.setBackgroundResource(R.drawable.bg_select_normal);
                if (calendar.before(currentDate)) {
                    dayView.day.setTextColor(context.getResources().getColor(R.color.tv_expire));
                } else {
                    dayView.day.setTextColor(context.getResources().getColor(R.color.tv_normal));
                }
            }
        }


        public int getSelect() {
            if (startCalendar == null) return NODE_NORMAL;
            final int start = calendar.compareTo(startCalendar);
            if (start < 0) return NODE_NORMAL;
            if (start == 0) return NODE_START;
            if (endCalendar == null) return NODE_MIDDLE;
            final int end = calendar.compareTo(endCalendar);
            if (end > 0) return NODE_NORMAL;
            if (end == 0) return NODE_END;
            return NODE_MIDDLE;
        }

        public void notifyDataForce() {

            Log.d(TAG, "notifyDataForce() called with: " + "year = [" + year + "], month = [" + month + "]");

            for (int position = 0; position < dayViews.length; position++) {
                DayView dayView = dayViews[position];
                if (dayView.day.getVisibility() != View.VISIBLE
                        || dayView.itemView.getVisibility() != View.VISIBLE)
                    continue;
                final int day = position + 1 - firstDayPosition;
                calendar.set(Calendar.DATE, day);
                setupNode(dayView);
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
