package com.example.lendit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
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

        // display name
        name.setText(username);

        // get users' profile data
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        building.setText(profileData.get("building").toString());
                        //number of neighbors: are we querying a list of usernames stored in user data or just querying for all with same building field
                        // numNeighbors.setText(data.get().toString());
                    }
                }
            }
        });

        // get users' lends
        db.collection("lends").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    lendsData = task.getResult().getDocuments();
                    // display number of users' posts
                    if (lendsData != null) {
                        numPosts.setText(String.valueOf(lendsData.size()));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // get users' asks
        db.collection("asks").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    asksData = task.getResult().getDocuments();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        mListView = (ListView) findViewById(R.id.listViewProfileLends);
        // populate list with ask and lend data
        String practiceImg = "drawable://" + R.drawable.bath;
        String dummyProfileImg = "drawable://" + R.drawable.bath;
        for (int i = 1; i < lendsData.size(); i++) {
            cardList.add(new PostCard(postInfo.get("photoID").toString(), postInfo.get("title").toString(), postInfo.get("fullName").toString(), postInfo.get("building").toString(), dummyProfileImg, postInfo.get("deposit").toString(), postInfo.get("description").toString()));
        }
        for (int i = 1; i < lendsData.size(); i++) {
            cardList.add(new PostCard(postInfo.get("title").toString(), postInfo.get("fullName").toString(), postInfo.get("building").toString(), dummyProfileImg, postInfo.get("description").toString()));
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_activity, cardList);
        if ((adapter != null) && (mListView != null)) {
            mListView.setAdapter(adapter);
        } else {
            System.out.println("Null Reference");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mode_close_button) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
