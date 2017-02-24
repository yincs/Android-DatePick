package org.changs.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import changs.android.widget.datepick.DatePicker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);

//        datePicker.setStartDay(2017, 0, 8);
//        datePicker.setEndDay(2017, 1, 8);

        datePicker.setDay(2017, 0, 8, 2017, 1, 8);
        datePicker.setOnItemClickListener(new DatePicker.OnItemClickListener() {
            @Override
            public void onItem(int year, int month, int day) {
                Log.d(TAG, "onItem() called with: year = [" + year + "], month = [" + month + "], day = [" + day + "]");
                datePicker.setEndDay(year, month, day);
            }
        });
    }
}
