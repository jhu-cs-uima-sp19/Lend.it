package com.example.lendit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAccountEditable extends AppCompatActivity {
    Spinner building;
    List<String> spinnerBuildings;
    String username;
    EditText first;
    EditText last;
    Button apply;
    Button cancel;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_editable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b = getIntent().getExtras();
        if (b != null) {
            username = b.getString("username");
        }

        apply = (Button) findViewById(R.id.applyChangesBTN);
        cancel = (Button) findViewById(R.id.cancelBTN);

        first = (EditText) findViewById(R.id.firstNameET);
        last = (EditText) findViewById(R.id.lastNameET);
        building = (Spinner) findViewById(R.id.buildingSpinner);
        spinnerBuildings = new ArrayList<String>(Arrays.asList("Charles Commons", "McCoy", "Bradford", "AMRI", "AMRII", "AMRIIIA", "AMRIIIB", "Wolman", "The Charles", "Homewood", "The Academy", "The Social", "Uni West", "100 West"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerBuildings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building.setAdapter(adapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccountEditable.this.finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeChanges();
            }
        });
    }

    public void makeChanges() {
        //make database changes for name, building
        UserAccountEditable.this.finish();

    }



}
