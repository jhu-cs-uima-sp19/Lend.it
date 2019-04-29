package com.example.lendit;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String username;
    private static String TAG = "HomePageActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView mListView;
    ArrayList<PostCard> cardList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView navUser = (TextView) hView.findViewById(R.id.titleTXT);
//ImageView imgvw = (ImageView) hView.findViewById(R.id.profpic);
        navUser.setText(username);
    }

    public void createPost(View view) {
        Intent i = new Intent(HomePage.this, CreatePost.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        final HomePage H = this;
        mListView = (ListView) findViewById(R.id.listViewLends);

        Log.d(TAG, "Card Act" + R.layout.card_activity);
        //query based on timestamp (most recent will be displayed first)
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "task successful");
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        cardList.add(new PostCard(s.getData().get("photo").toString(), s.getData().get("title").toString(), s.getData().get("deposit").toString(), s.getData().get("description").toString(), s.getData().get("username").toString(), s.getData().get("id").toString(), s.getData().get("post_date").toString()));                    }
                    PostCardListAdapter adapter = new PostCardListAdapter(H, cardList, username);
                    if ((adapter != null) && (mListView != null)) {
                        mListView.setAdapter(adapter);
                    } else {
                        System.out.println("Null Reference");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
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
        //getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void toAccount(View v) {
        Intent i;
        final Bundle bundle = new Bundle();
        i = new Intent(HomePage.this, UserAccount.class);
        bundle.putString("username", username);
        i.putExtras(bundle);
        startActivity(i);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;
        final Bundle bundle = new Bundle();


        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            // Handle the camera action
        } else if (id == R.id.nav_msg) {
            i = new Intent(HomePage.this, MessageInbox.class);
            bundle.putString("username", username);
            i.putExtras(bundle);
            startActivity(i);

        } else if (id == R.id.nav_acc) { //Account
            i = new Intent(HomePage.this, UserAccount.class);
            bundle.putString("username", username);
            i.putExtras(bundle);
            startActivity(i);
        } else if (id == R.id.nav_lends) {

        }  else if (id == R.id.nav_logout)  {
            i = new Intent(HomePage.this, MainActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getUsername() {
        return username;
    }
}
