package android.mad.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import model.EventImpl;

public class MyCalendar extends AppCompatActivity {

    private static final String TAG = "MyCalendar";

    private TextView yearTextView;
    private TextView monthTextView;
    private TextView[] dateViews;
    private TextView eventDisplay;
    private int initYear;
    private int initMonth;
    private ArrayList<EventImpl> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);

        Intent in = getIntent();
        events = (ArrayList<EventImpl>) in.getSerializableExtra("EVENTS");

        initYear = Calendar.getInstance().get(Calendar.YEAR);
        initMonth = Calendar.getInstance().get(Calendar.MONTH);

        yearTextView = findViewById(R.id.year_label_on_calendar_view);
        yearTextView.setText(String.valueOf(initYear));

        monthTextView = findViewById(R.id.month_label_on_calendar_view);
        setMonthText();

        eventDisplay = findViewById(R.id.event_text_view_on_calendar);
        resetEventDisplay();

        Button previousYearButton = findViewById(R.id.previous_year_button);
        previousYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initYear--;
                resetDateViews();
                resetEventDisplay();
            }
        });

        Button nextYearButton = findViewById(R.id.next_year_button);
        nextYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initYear++;
                resetDateViews();
                resetEventDisplay();
            }
        });

        Button previousMonthButton = findViewById(R.id.previous_month_button);
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMonth--;
                if (initMonth == -1) {
                    initMonth = 11;
                    initYear--;
                }
                resetDateViews();
                resetEventDisplay();
            }
        });

        Button nextMonthButton = findViewById(R.id.next_month_button);
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMonth++;
                if (initMonth == 12) {
                    initMonth = 0;
                    initYear++;
                }
                resetDateViews();
                resetEventDisplay();
            }
        });

        initDateViews();
        resetDateViews();
    }

    private void initDateViews() {
        dateViews = new TextView[42];

        dateViews[0] = findViewById(R.id.date_label_1);
        dateViews[1] = findViewById(R.id.date_label_2);
        dateViews[2] = findViewById(R.id.date_label_3);
        dateViews[3] = findViewById(R.id.date_label_4);
        dateViews[4] = findViewById(R.id.date_label_5);
        dateViews[5] = findViewById(R.id.date_label_6);
        dateViews[6] = findViewById(R.id.date_label_7);

        dateViews[7] = findViewById(R.id.date_label_8);
        dateViews[8] = findViewById(R.id.date_label_9);
        dateViews[9] = findViewById(R.id.date_label_10);
        dateViews[10] = findViewById(R.id.date_label_11);
        dateViews[11] = findViewById(R.id.date_label_12);
        dateViews[12] = findViewById(R.id.date_label_13);
        dateViews[13] = findViewById(R.id.date_label_14);

        dateViews[14] = findViewById(R.id.date_label_15);
        dateViews[15] = findViewById(R.id.date_label_16);
        dateViews[16] = findViewById(R.id.date_label_17);
        dateViews[17] = findViewById(R.id.date_label_18);
        dateViews[18] = findViewById(R.id.date_label_19);
        dateViews[19] = findViewById(R.id.date_label_20);
        dateViews[20] = findViewById(R.id.date_label_21);

        dateViews[21] = findViewById(R.id.date_label_22);
        dateViews[22] = findViewById(R.id.date_label_23);
        dateViews[23] = findViewById(R.id.date_label_24);
        dateViews[24] = findViewById(R.id.date_label_25);
        dateViews[25] = findViewById(R.id.date_label_26);
        dateViews[26] = findViewById(R.id.date_label_27);
        dateViews[27] = findViewById(R.id.date_label_28);

        dateViews[28] = findViewById(R.id.date_label_29);
        dateViews[29] = findViewById(R.id.date_label_30);
        dateViews[30] = findViewById(R.id.date_label_31);
        dateViews[31] = findViewById(R.id.date_label_32);
        dateViews[32] = findViewById(R.id.date_label_33);
        dateViews[33] = findViewById(R.id.date_label_34);
        dateViews[34] = findViewById(R.id.date_label_35);

        dateViews[35] = findViewById(R.id.date_label_36);
        dateViews[36] = findViewById(R.id.date_label_37);
        dateViews[37] = findViewById(R.id.date_label_38);
        dateViews[38] = findViewById(R.id.date_label_39);
        dateViews[39] = findViewById(R.id.date_label_40);
        dateViews[40] = findViewById(R.id.date_label_41);
        dateViews[41] = findViewById(R.id.date_label_42);
    }

    private int getFirstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(initYear, initMonth, 1);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    private int getMonthDays() {
        Calendar c = Calendar.getInstance();
        c.set(initYear, initMonth, 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void resetDateViews() {
        for (TextView v: dateViews) {
            v.setText("");
            v.setOnClickListener(null);
            v.setBackground(null);
        }

        int start = getFirstDayOfMonth() - 1;
        int daysOfMonth = getMonthDays();

        int i = 0;
        while (i < daysOfMonth) {
            dateViews[start].setText(String.valueOf(i + 1));
            start++;
            i++;
        }

        this.yearTextView.setText(String.valueOf(initYear));
        setMonthText();
        setupEventDisplayOnDateClick();
    }

    private void setMonthText() {
        String m;
        switch (initMonth) {
            case 0: m = "January"; break;
            case 1: m = "February"; break;
            case 2: m = "March"; break;
            case 3: m = "April"; break;
            case 4: m = "May"; break;
            case 5: m = "June"; break;
            case 6: m = "July"; break;
            case 7: m = "August"; break;
            case 8: m = "September"; break;
            case 9: m = "October"; break;
            case 10: m = "November"; break;
            case 11: m = "December"; break;
            default: m = "Error";
        }
        monthTextView.setText(m);
    }

    private void setupEventDisplayOnDateClick() {
        Log.d(TAG, "setupEventDisplayOnDateClick: called.");

        String outputFormat = "M/dd/yyyy H:mm:ss a";
        SimpleDateFormat format = new SimpleDateFormat(outputFormat);

        for (TextView t: dateViews) {
            if (t.getText().toString().equals("")) {
                continue;
            }
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder builder = new StringBuilder();
                    Calendar c = Calendar.getInstance();
                    for (EventImpl e: events) {
                        c.setTime(e.getStartDate());
                        if (c.get(Calendar.YEAR) == initYear) {
                            if (c.get(Calendar.MONTH) == initMonth) {
                                if (c.get(Calendar.DATE) == Integer.parseInt(t.getText().toString())) {
                                    builder.append(e.getTitle()).append(" ").append(format.format(e.getStartDate())).append(System.lineSeparator());
                                }
                            }
                        }
                    }
                    if (builder.toString().equals("")) {
                        resetEventDisplay();
                    } else {
                        eventDisplay.setText(builder.toString());
                    }
                }
            });
        }

        for (TextView t: dateViews) {
            if (t.getText().toString().equals("")) {
                continue;
            }
            for (EventImpl e: events) {
                Calendar c = Calendar.getInstance();
                c.setTime(e.getStartDate());
                if (c.get(Calendar.YEAR) == initYear) {
                    if (c.get(Calendar.MONTH) == initMonth) {
                        if (c.get(Calendar.DATE) == Integer.parseInt(t.getText().toString())) {
                            t.setBackgroundResource(R.drawable.circle);
                        }
                    }
                }
            }
        }
    }

    private void resetEventDisplay() {
        eventDisplay.setText(R.string.no_event);
    }

}
