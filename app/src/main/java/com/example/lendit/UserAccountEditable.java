package com.example.lendit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAccountEditable extends AppCompatActivity {
    Spinner building;
    List<String> spinnerBuildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_editable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        building = (Spinner) findViewById(R.id.buildingSpinner);
        spinnerBuildings = new ArrayList<String>(Arrays.asList("Charles Commons", "McCoy", "Bradford", "AMRI", "AMRII", "AMRIIIA", "AMRIIIB", "Wolman", "The Charles", "Homewood", "The Academy", "The Social", "Uni West", "100 West"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerBuildings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building.setAdapter(adapter);
    }

}
