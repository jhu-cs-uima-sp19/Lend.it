package com.example.lendit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        mListView = findViewById(R.id.listViewProfileLends);

        // display name
        name.setText(username);

        // get users' profile data
        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
                building.setText(profileData.get("building").toString());
                //number of neighbors: are we querying a list of usernames stored in user data or just querying for all with same building field
                // numNeighbors.setText(data.get().toString());
            }
        });

        // get users' lend data (most recent at top)
        db.collection("lends").whereEqualTo("username", username).orderBy("post_date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //String practiceImg = "gs6://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg";
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        Map<String, Object> d = s.getData();
                        cardList.add(new PostCard(d.get("photoID").toString(), d.get("title").toString(), d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("deposit").toString(), d.get("description").toString()));
                    }

                    CustomListAdapter adapter = new CustomListAdapter(UserAccount.this, cardList);
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
        db.collection("asks").whereEqualTo("username", username).orderBy("post_date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //String practiceImg = "gs6://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg";
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        Map<String, Object> d = s.getData();
                        cardList.add(new PostCard(d.get("title").toString(), d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("description").toString()));
                    }

                    CustomListAdapter adapter = new CustomListAdapter(UserAccount.this, cardList);
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



       /* dummy data for testing
        cardList.add(new PostCard("drawable://" + R.drawable.bath, "Bath", "Ryan", "Charles Commons", "drawable://" + R.drawable.ask, "$10", "a great appliance!"));
        cardList.add(new PostCard("drawable://" + R.drawable.stove, "Stove", "Ravina", "Charles Commons", "drawable://" + R.drawable.ask, "$10", "a great appliance!"));
        cardList.add(new PostCard("drawable://" + R.drawable.kitchen, "Kitchen", "Taryn", "Charles Commons", "drawable://" + R.drawable.ask, "$10", "a great appliance!"));
*/
//        Log.d(TAG, "about to make Custom List AD");
//        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_activity, cardList);
//        if ((adapter != null) && (mListView != null)) {
//            mListView.setAdapter(adapter);
//        } else {
//            System.out.println("Null Reference");
//        }

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

}

/*
public class UserAccount extends AppCompatActivity {

    Bundle b;
    String username;
    TextView name;
    TextView building;
    TextView numNeighbors;
    TextView numPosts;
    int counter;
    String profileImg;
    ImageView pf;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
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
        mListView = findViewById(R.id.listViewProfileLends);
        pf = findViewById(R.id.profilePic);
        pf.setImageResource(R.drawable.profile);
        // display name
       // name.setText(username);

        // get users' profile data
        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
                building.setText(profileData.get("building").toString());
                profileImg = profileData.get("profileImg").toString();
                        //number of neighbors: are we querying a list of usernames stored in user data or just querying for all with same building field
                        // numNeighbors.setText(data.get().toString());
                    }
        });

        // get users' lend data (most recent at top)
        //.orderBy("post_date", Query.Direction.DESCENDING)
        db.collection("lends").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                //String practiceImg = "gs6://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg";

                for (QueryDocumentSnapshot s : task.getResult()) {
                    Map<String, Object> d = s.getData();
                    cardList.add(new PostCard(d.get("photoID").toString(), d.get("title").toString(), d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("deposit").toString(), d.get("description").toString()));
                    //counter++;

                }

                CustomListAdapter adapter = new CustomListAdapter(UserAccount.this, R.layout.card_activity, cardList);
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
                    //String practiceImg = "gs6://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg";
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        Map<String, Object> d = s.getData();
                        cardList.add(new PostCard(d.get("title").toString(), d.get("fullName").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("description").toString()));
                        //counter++;
                    }

                    CustomListAdapter adapter = new CustomListAdapter(UserAccount.this, R.layout.card_activity, cardList);
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

        numPosts.setText("3");
        numNeighbors.setText("20");
        //pf.setImageDrawable("drawable://" + R.drawable.bath"");
        // post image

       /* dummy data for testing
        cardList.add(new PostCard("drawable://" + R.drawable.bath, "Bath", "Ryan", "Charles Commons", "drawable://" + R.drawable.ask, "$10", "a great appliance!"));
        cardList.add(new PostCard("drawable://" + R.drawable.stove, "Stove", "Ravina", "Charles Commons", "drawable://" + R.drawable.ask, "$10", "a great appliance!"));
        cardList.add(new PostCard("drawable://" + R.drawable.kitchen, "Kitchen", "Taryn", "Charles Commons", "drawable://" + R.drawable.ask, "$10", "a great appliance!"));

//        Log.d(TAG, "about to make Custom List AD");
//        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_activity, cardList);
//        if ((adapter != null) && (mListView != null)) {
//            mListView.setAdapter(adapter);
//        } else {
//            System.out.println("Null Reference");
//        }
}
}

*/