package com.example.lendit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    Button createLendRequest;
    int year;
    int month;
    int day;
    int currHour;
    int currMin;
    String amPm;
    Calendar cal;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "CreateLendTransaction";
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    private Context context = this;
    String username;
    PostCard p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        p = i.getParcelableExtra("post");
        username = i.getStringExtra("username");

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

        // from date
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

        // to date
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

        // from time
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
                        fromTime.setText(String.format("%2d:%2d ", hourOfDay, minute) + amPm);
                    }
                }
                        , currHour, currMin, false);
                timePick.show();
            }
        });

        // to time
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
                        toTime.setText(String.format("%2d:%2d ", hourOfDay, minute) + amPm);
                    }
                }
                        , currHour, currMin, false);
                timePick.show();
            }
        });
    }


//        createLendRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createRequest();
//            }
//        });


    public void createRequest(View v) {
        String uniqueID = UUID.randomUUID().toString();
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("borrower", username);
        request.put("lender", p.username);
        request.put("postID", p.postID);
        //hh:mm
        //request.put("from", new Date());
        //request.put("to", );

        db.collection("transactionRequests").document(uniqueID).set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        CreateLendTransaction.this.finish();
    }

}
