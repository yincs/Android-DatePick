package changs.android.widget.datepick;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
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

//        startCalendar = initCalendar(Calendar.getInstance(locale));
//        endCalendar = initCalendar(Calendar.getInstance(locale));

        startCalendar.set(2017, 1, 23);
        endCalendar.set(2017, 1, 26);
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

    private void check() {
        if (startCalendar == null
                || endCalendar == null) return;

        if (endCalendar.before(startCalendar)) {
            throw new RuntimeException("结束日期在开始日期之前");
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


        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthViewHolder(inflater.inflate(R.layout.item_month, parent, false));
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, int position) {
            final int year = getYearForPosition(position);
            final int month = getMonthForPosition(position);
            holder.adapter.setTime(year, month);
            holder.tvMonth.setText(year + "年" + (month + 1) + "月");
        }

        @Override
        public int getItemCount() {
            return count;
        }

        public void notifyDataForce() {

        }
    }

    private class MonthViewHolder extends RecyclerView.ViewHolder {
        final DateAdapter adapter;
        TextView tvMonth;
        RecyclerView rccViewDate;

        public MonthViewHolder(View itemView) {
            super(itemView);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            rccViewDate = (RecyclerView) itemView.findViewById(R.id.rccViewDay);

            rccViewDate.setLayoutManager(new GridLayoutManager(context, 7));
            adapter = new DateAdapter();
            rccViewDate.setAdapter(adapter);
            rccViewDate.setNestedScrollingEnabled(false);
        }
    }

    private class DateAdapter extends RecyclerView.Adapter<DateViewHolder> {

        private final Calendar calendar = initCalendar(Calendar.getInstance());
        private int firstDayPosition = -1;
        private int count;
        private int monthDays;
        private int year;
        private int month;

        @Override
        public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DateViewHolder(inflater.inflate(R.layout.item_date, parent, false));
        }

        @Override
        public void onBindViewHolder(DateViewHolder holder, int position) {
            final int day = position + 1 - firstDayPosition;
            if (position < firstDayPosition) {
                holder.itemView.setVisibility(View.GONE);
            } else if (day > monthDays) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.day.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.day.setVisibility(View.VISIBLE);
            }
            if (holder.day.getVisibility() != View.VISIBLE
                    || holder.itemView.getVisibility() != View.VISIBLE)
                return;

            holder.day.setText(String.valueOf(day));

            final int select = getSelect(day);
            Log.d(TAG, "select = " + year + " " + month + " " + day);
            if (select == NODE_MIDDLE) {
                holder.day.setBackgroundResource(R.drawable.bg_select_middle);
            } else if (select == NODE_START) {
                holder.day.setBackgroundResource(R.drawable.bg_select_start);
            } else if (select == NODE_END) {
                holder.day.setBackgroundResource(R.drawable.bg_select_end);
            } else {
                holder.day.setBackgroundResource(R.drawable.bg_select_normal);
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
            count = monthDays + firstDayPosition;
            final int r = count % WEEKS_DAY;
            if (r != 0) {
                count += (WEEKS_DAY - r);
            }

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return count;
        }
    }

    private class DateViewHolder extends RecyclerView.ViewHolder {

        TextView day;

        public DateViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.tv_day);
        }
    }

}
