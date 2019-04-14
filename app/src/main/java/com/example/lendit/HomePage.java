package com.example.lendit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String userName;
    private static String TAG = "HomePageActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DocumentSnapshot> asksData;
    List<DocumentSnapshot> lendsData;
    private ListView mListView;
    ArrayList<PostCard> cardList = new ArrayList();
    Map<String, Object> postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("userName");
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mListView = (ListView) findViewById(R.id.listViewLends);

        // query based on timestamp (most recent will be displayed first)
        db.collection("asks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }*/
                    asksData = task.getResult().getDocuments();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        db.collection("lends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    /*for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }*/
                    lendsData = task.getResult().getDocuments();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // populate list with ask and lend data
        String practiceImg = "drawable://" + R.drawable.bath;
        // String dummyProfileImg = "drawable://" + R.drawable.bath;
        for (int i = 1; i < lendsData.size(); i++) {
            cardList.add(new PostCard(practiceImg, postInfo.get("title").toString(), postInfo.get("fullName").toString(), postInfo.get("building").toString(), postInfo.get("profileImg").toString(), postInfo.get("deposit").toString(), postInfo.get("description").toString()));
        }
        for (int i = 1; i < asksData.size(); i++) {
            cardList.add(new PostCard(postInfo.get("title").toString(), postInfo.get("fullName").toString(), postInfo.get("building").toString(), postInfo.get("profileImg").toString(), postInfo.get("description").toString()));
        }

       /* dummy data for testing
        list.add(new PostCard("drawable://" + R.drawable.bath, "Bathroom", "Ryan"));
        list.add(new PostCard("drawable://" + R.drawable.stove, "Stove", "Ravina"));
        list.add(new PostCard("drawable://" + R.drawable.kitchen, "Kitchen", "Taryn"));*/

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_activity, cardList);
        if ((adapter != null) && (mListView != null)) {
            mListView.setAdapter(adapter);
        } else {
            System.out.println("Null Reference");
        }

    }

    public void createPost(View view) {
        Intent i = new Intent(HomePage.this, CreatePost.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;
        Bundle bundle = new Bundle();


        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            // Handle the camera action
        } else if (id == R.id.nav_msg) {
            i = new Intent(HomePage.this, MessageInbox.class);
            bundle.putString("username", userName);
            i.putExtras(bundle);
            startActivity(i);

        } else if (id == R.id.nav_acc) { //Account
            i = new Intent(HomePage.this, UserAccount.class);
            bundle.putString("username", userName);
            i.putExtras(bundle);
            startActivity(i);
        } else if (id == R.id.nav_lends) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
