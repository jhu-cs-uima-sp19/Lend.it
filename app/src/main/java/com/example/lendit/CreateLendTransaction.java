package com.example.lendit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateLendTransaction extends AppCompatActivity {

    TextView fromDate;
    TextView toDate;
    TextView fromTime;
    TextView toTime;
    DatePickerDialog datePickerFrom;
    DatePickerDialog datePickerTo;
    TimePickerDialog timePick;
    Button selectFrom;
    Button selectTo;
    Button selectTimeFrom;
    Button selectTimeTo;
    int year;
    int month;
    int day;
    int currHour;
    int currMin;
    String amPm;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lend_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectFrom = findViewById(R.id.borrowDate);
        selectTo = findViewById(R.id.returnDate);
        selectTimeFrom = findViewById(R.id.borrowTime);
        selectTimeTo = findViewById(R.id.returnTime);

        fromDate = findViewById(R.id.fromDateTxt);
        toDate = findViewById(R.id.toDateTxt);
        fromTime = findViewById(R.id.fromTimeTxt);
        toTime = findViewById(R.id.toTimeTxt);

        selectFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                datePickerFrom = new DatePickerDialog(CreateLendTransaction.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                fromDate.setText((month + 1) + "/" + dayOfMonth  + "/" + year);
                            }
                        }, year, month, day);
                datePickerFrom.show();
            }
        });

        selectTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                datePickerTo = new DatePickerDialog(CreateLendTransaction.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                toDate.setText((month + 1) + "/" + dayOfMonth  + "/" + year);
                            }
                        }, year, month, day);
                datePickerTo.show();
            }
        });

        selectTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = Calendar.getInstance();
                currHour = cal.get(Calendar.HOUR_OF_DAY);
                currMin = cal.get(Calendar.MINUTE);
                timePick = new TimePickerDialog(CreateLendTransaction.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        fromTime.setText(String.format("%2d:%2d", hourOfDay, minute) + amPm);
                    }
                }
                        , currHour, currMin, false);
                timePick.show();
            }
        });

        selectTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = Calendar.getInstance();
                currHour = cal.get(Calendar.HOUR_OF_DAY);
                currMin = cal.get(Calendar.MINUTE);
                timePick = new TimePickerDialog(CreateLendTransaction.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        toTime.setText(String.format("%2d:%2d", hourOfDay, minute) + amPm);
                    }
                }
                        , currHour, currMin, false);
                timePick.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_lend_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mode_close_button) {
            CreateLendTransaction.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
