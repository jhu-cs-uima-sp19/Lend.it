package com.example.lendit;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signup extends AppCompatActivity {
    Button signUp;
    EditText emailAddress;
    EditText password;
    EditText first;
    EditText last;
    Spinner building;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SignUpActivity";
    List<String> spinnerBuildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Toolbar toolbar = findViewById(R.id.toolbar),
        // set variables for all signup screen components
        signUp = findViewById(R.id.createUser);
        emailAddress = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.passET);
        first = (EditText) findViewById(R.id.firstNameET);
        last = (EditText) findViewById(R.id.lastNameET);
        building = (Spinner) findViewById(R.id.buildingSpinner);

        // populate buildings spinner
        spinnerBuildings = new ArrayList<String>(Arrays.asList("Charles Commons", "McCoy", "Bradford", "AMRI", "AMRII", "AMRIIIA", "AMRIIIB", "Wolman", "The Charles", "Homewood", "The Academy", "The Social", "Uni West", "100 West"));



        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerBuildings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building.setAdapter(adapter);

        // listener for create button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailAddress.getText().toString();
                String pass = password.getText().toString();
                if (validPassword(pass) && validEmail(email)) {
                    // create user
                    createUser(email, pass);
                    // go to home page
                }
            }
        });
    }


    public boolean validEmail(String email) {
        if (email.contains("@jhu.edu")) {
            return true;
        } else {
            Toast.makeText(this, "Please enter your JHU email address", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean validPassword(String pass) {
        EditText confirmPassword = (EditText) findViewById(R.id.confirmPassET);
        String confirm = confirmPassword.getText().toString();
        if (pass.compareTo(confirm) == 0) {
            if (pass.equals("")) {
                Toast.makeText(this, "Password must not be empty", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        } else {
            Toast.makeText(this, "Password entries do not match", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void createUser(String email, String password) {

        // figure out if we want to be able to add people not in our building as neighbors
        // String[] neighbors = ;
        //String profilePic;

        final String username = email.substring(0, email.indexOf('@'));
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", username);
        profile.put("password", password);
        profile.put("first", first.getText().toString());
        profile.put("last", last.getText().toString());
        profile.put("building", building.getSelectedItem().toString());
        profile.put("profileImg", "gs://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg");
        db.collection("users").document(username).set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Intent i = new Intent(Signup.this, HomePage.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                i.putExtras(bundle);
                startActivity(i);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }
}
