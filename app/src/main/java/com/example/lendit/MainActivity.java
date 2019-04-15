package com.example.lendit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class MainActivity extends AppCompatActivity {

    EditText emailAddress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button signInButton = (Button) findViewById(R.id.signin);
        emailAddress = (EditText) findViewById(R.id.email);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toHome(View view) {
        String email = emailAddress.getText().toString();
        if (email.contains("@jhu.edu")) {
            final String username = email.substring(0, email.indexOf('@'));
            // get users' profile data
            db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String dbPass = task.getResult().get("password").toString();
                            EditText passField = (EditText) findViewById(R.id.password);
                            String enteredPass = passField.getText().toString();
                            if (dbPass.equals(enteredPass)) {
                                Intent i = new Intent(MainActivity.this, HomePage.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("username", username);
                                i.putExtras(bundle);
                                startActivity(i);
                            } else {
                                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //number of neighbors: are we querying a list of usernames stored in user data or just querying for all with same building field
                            // numNeighbors.setText(data.get().toString());
                            Toast.makeText(MainActivity.this, "Account with this email address does not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please enter your JHU email address", Toast.LENGTH_LONG).show();
        }
    }

    public void toSignUp(View view) {
        Intent i = new Intent(MainActivity.this, Signup.class);
        startActivity(i);
    }
}
