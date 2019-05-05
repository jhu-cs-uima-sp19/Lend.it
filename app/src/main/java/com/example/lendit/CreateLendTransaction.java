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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    EditText deposit;
    TextView borrower;
    TextView lender;
    TextView item;
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
    Map<String, Object> profileData;
    Map<String, Object> postData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lend_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        p = i.getParcelableExtra("post");
        username = i.getStringExtra("username");

        createLendRequest = findViewById(R.id.createRequest);

        deposit = findViewById(R.id.depositET);
        deposit.setText(p.deposit);

        item = findViewById(R.id.itemET);
        item.setText(p.postTitle);

        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
                borrower = findViewById(R.id.borrowerET);
                borrower.setText(profileData.get("first").toString() + " " + profileData.get("last").toString());
            }
        });

        db.collection("users").document(p.username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                postData = documentSnapshot.getData();
                lender = findViewById(R.id.lenderET);
                lender.setText(postData.get("first").toString() + " " + postData.get("last").toString());
            }
        });



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
                                String m = "" + (month + 1);
                                String d = "" + dayOfMonth;
                                if ((month + 1) < 10) {
                                    m = "0" + m;
                                }
                                if (dayOfMonth < 10) {
                                    d = "0" + d;
                                }
                                fromDate.setText(m + "/" + d + "/" + year);
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
                                String m = "" + (month + 1);
                                String d = "" + dayOfMonth;
                                if ((month + 1) < 10) {
                                    m = "0" + m;
                                }
                                if (dayOfMonth < 10) {
                                    d = "0" + d;
                                }
                                toDate.setText(m + "/" + d + "/" + year);
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
                        String h = "" + hourOfDay;
                        String m = "" + minute;
                        if (hourOfDay < 10) {
                            h = "0" + h;
                        }
                        if (minute < 10) {
                            m = "0" + m;
                        }

                        fromTime.setText(h + ":" + m);
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
                        String h = "" + hourOfDay;
                        String m = "" + minute;
                        if (hourOfDay < 10) {
                            h = "0" + h;
                        }
                        if (minute < 10) {
                            m = "0" + m;
                        }
                        toTime.setText(h + ":" + m);
                    }
                }
                        , currHour, currMin, false);
                timePick.show();
            }
        });

        createLendRequest.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               createRequest();
            }
        });
    }


    public void createRequest() {
        String uniqueID = UUID.randomUUID().toString();
        Map<String, Object> request = new HashMap<String, Object>();
        Date currDate = new Date();

        request.put("borrower", username);
        request.put("lender", p.username);
        request.put("postTitle", p.postTitle);
        request.put("deposit", deposit.getText().toString());
        request.put("id", uniqueID);

        try {
            //1. Create a Date from String
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");

            Date from = sdf.parse(fromDate.getText().toString() + " " + fromTime.getText().toString());
            Log.d(TAG, fromDate.getText().toString() + " " + fromTime.getText().toString());
            Date to = sdf.parse(toDate.getText().toString() + " " + toTime.getText().toString());

            Calendar calendarTo = Calendar.getInstance();
            calendarTo.setTime(to);
            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.setTime(from);

            if (calendarTo.getTime().compareTo(calendarFrom.getTime()) < 0) {
                Log.d(TAG,"im in the to before from");
                Toast.makeText(CreateLendTransaction.this, "Please choose valid start and end times.", Toast.LENGTH_LONG).show();
                throw new ParseException("To before from", 1);
            }
            if (Calendar.getInstance().getTime().compareTo(calendarTo.getTime()) > 0) {
                Log.d(TAG,"im in the to before current");
                Log.d(TAG,"current" + Calendar.getInstance().getTime());
                Log.d(TAG,"to" + calendarTo.getTime());
                Toast.makeText(CreateLendTransaction.this, "Please choose valid start and end times.", Toast.LENGTH_LONG).show();
                throw new ParseException("To before current", 1);
            }

            request.put("from", calendarFrom.getTime());
            request.put("to", calendarTo.getTime());
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
            Toast.makeText(CreateLendTransaction.this, "Lend Transaction Request Sent!", Toast.LENGTH_LONG).show();
            CreateLendTransaction.this.finish();
        } catch(ParseException e) {
            Log.d(TAG, "parse exception");
            Toast.makeText(CreateLendTransaction.this, "Please choose valid start and end times.", Toast.LENGTH_LONG).show();
        }
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
