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
                                fromDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
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
                                toDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
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
                        fromTime.setText(String.format("%2d:%2d ", hourOfDay, minute));
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
                        toTime.setText(String.format("%2d:%2d ", hourOfDay, minute));
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
        request.put("borrower", username);
        request.put("lender", p.username);
        request.put("postID", p.postID);
        request.put("deposit", deposit.getText().toString());
        request.put("id", uniqueID);

        String pattern = "mm/dd/yyyy hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        try {
            Date from = simpleDateFormat.parse(fromDate.getText().toString() + " " + fromTime.getText().toString());
            Date to = simpleDateFormat.parse(toDate.getText().toString() + " " + toTime.getText().toString());
            request.put("from", from);
            request.put("to", to);
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
        } catch(ParseException e) {
            Log.d(TAG, "parse exception");
            Toast.makeText(CreateLendTransaction.this, "Error creating request.", Toast.LENGTH_LONG).show();
        }

        CreateLendTransaction.this.finish();
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
