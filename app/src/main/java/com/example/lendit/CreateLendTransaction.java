package com.example.lendit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class CreateLendTransaction extends AppCompatActivity {

    TextView fromDate;
    TextView toDate;
    DatePickerDialog datePickerFrom;
    DatePickerDialog datePickerTo;
    Button selectFrom;
    Button selectTo;
    int year;
    int month;
    int day;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lend_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectFrom = findViewById(R.id.borrowDate);
        selectTo = findViewById(R.id.returnDate);

        fromDate = findViewById(R.id.fromDateTxt);
        toDate = findViewById(R.id.toDateTxt);

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
    }

}
