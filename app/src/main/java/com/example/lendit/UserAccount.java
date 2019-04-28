package com.example.lendit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAccount extends AppCompatActivity {

    Bundle b;
    String username;
    TextView name;
    TextView building;
    TextView numNeighbors;
    TextView numPosts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // all data fields in user profile
    Map<String, Object> profileData;
    List<DocumentSnapshot> asksData;
    List<DocumentSnapshot> lendsData;
    private static final String TAG = "UserAccountActivity";
    private ListView mListView;
    ArrayList<PostCard> cardList = new ArrayList();
    Map<String, Object> postInfo;
    String buildingName;
    ArrayList<UserCard> userCards = new ArrayList();
    QuerySnapshot neighborData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get username from bundle in intent
        b = getIntent().getExtras();
        if (b != null) {
            username = b.getString("username");
        }

        name = findViewById(R.id.nameTxt);
        building = findViewById(R.id.buildingTxt);
        numNeighbors = findViewById(R.id.neighborsNumTxt);
        numPosts = findViewById(R.id.myPostsNumTxt);
        // change to listview of
        mListView = findViewById(R.id.listViewProfileLends);

        // display name
        name.setText(username);

        // get users' profile data
        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
                buildingName = profileData.get("building").toString();
                name.setText(profileData.get("first").toString() + " " + profileData.get("last").toString());
                building.setText(buildingName);

            }
        });

        // querying for users w/ same building field
        db.collection("users").whereEqualTo("building", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    neighborData = task.getResult();
                    for (QueryDocumentSnapshot s : neighborData) {
                        count++;
                    }
                    numNeighbors.setText("" + count);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }

});
        // get users' lend data (most recent at top)
        /*orderBy("post_date", Query.Direction.DESCENDING)*/
        db.collection("lends").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //String practiceImg = "gs6://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg";
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        Map<String, Object> d = s.getData();
                        cardList.add(new PostCard(d.get("photoID").toString(), d.get("title").toString(), d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("deposit").toString(), d.get("description").toString(), d.get("username").toString()));
                    }

                    PostCardListAdapter adapter = new PostCardListAdapter(UserAccount.this, cardList, username);
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

        // get users' ask data (most recent at top)
        db.collection("asks").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        Map<String, Object> d = s.getData();
                        cardList.add(new PostCard(d.get("title").toString(), d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("description").toString(), d.get("username").toString()));
                    }

                    PostCardListAdapter adapter = new PostCardListAdapter(UserAccount.this, cardList, username);
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

    public void toNeighborList() {
        for (QueryDocumentSnapshot s : neighborData) {
            Map<String, Object> d = s.getData();
            userCards.add(new UserCard(d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("username").toString()));
        }

        Intent i;
        i = new Intent(this, NeighborList.class);
        i.putExtra("userList", userCards);
        i.putExtra("username", username);
        this.startActivity(i);
    }

    public void editProfile(View v) {
        Intent i = new Intent(UserAccount.this, UserAccountEditable.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        i.putExtras(bundle);
        startActivity(i);
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
        getMenuInflater().inflate(R.menu.user_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_mode_close_button) {
            UserAccount.this.finish();
            return true;
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}