package com.example.lendit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

    public class NeighborList extends AppCompatActivity{
        String username;
        private static String TAG = "NeighborListActivity";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        private ListView mListView;
        ArrayList<UserCard> userCards;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setContentView(R.layout.activity_neighborlist);

            mListView = (ListView) findViewById(R.id.neighborListView);
            Intent i = getIntent();
            userCards = i.getParcelableArrayListExtra("userList");
            username = i.getStringExtra("username");
            UserCardListAdapter adapter = new UserCardListAdapter(this, userCards);
            if ((adapter != null) && (mListView != null)) {
                mListView.setAdapter(adapter);
            } else {
                System.out.println("Null Reference");
            }
        }

        @Override
        protected void onResume() {
            super.onResume();

        }

        @Override
        public void onStart() {
            super.onStart();
        }

    }

