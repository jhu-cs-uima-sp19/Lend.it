package com.example.lendit;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    TextView rating;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // all data fields in user profile
    Map<String, Object> profileData;
    List<DocumentSnapshot> asksData;
    List<DocumentSnapshot> lendsData;
    private static final String TAG = "UserAccountActivity";
    private ListView mListView;

    Map<String, Object> postInfo;
    String buildingName = "";
    double total = -1.0;

    QuerySnapshot neighborData;
    int transactionCount = 0;
    int count = 0;
    Context context = this;
    ImageView pic;
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    Button edit;
    String myUsername;

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
             myUsername = b.getString("myUsername");
        }
        edit = findViewById(R.id.editProfileBTN);

        pic = findViewById(R.id.profilePic);


        rating = findViewById(R.id.ratingTxtView);
        name = findViewById(R.id.nameTxt);
        building = findViewById(R.id.buildingTxt);
        numNeighbors = findViewById(R.id.neighborsNumTxt);
        numPosts = findViewById(R.id.myPostsNumTxt);
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

    @Override
    public void onStart() {
        super.onStart();
        final ArrayList<PostCard> cardList = new ArrayList();
        final ArrayList<UserCard> userCards = new ArrayList();
        // get users' profile data
        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
                buildingName = profileData.get("building").toString();
                String full_name = profileData.get("first").toString() + " " + profileData.get("last").toString();
                name.setText(full_name);
                building.setText(buildingName);
                final long ONE_MEGABYTE = 1024 * 1024;
                if (profileData.get("profileImg").toString().equals("appImages/avatar.png")) {
                    pic.setImageResource(R.drawable.avatar);
                } else {
                    storage.child(profileData.get("profileImg").toString()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            pic.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
        });

        // querying for users w/ same building field
        db.collection("users").whereEqualTo("building", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (QueryDocumentSnapshot s : task.getResult()) {
                         Map<String, Object> d = s.getData();

                           if (!d.get("username").equals(username)) {
                        count++;
                        userCards.add(new UserCard(d.get("first").toString() + " " + d.get("last").toString(), d.get("building").toString(), d.get("profileImg").toString(), d.get("username").toString()));
                    }
                }

                    numNeighbors.setText(Integer.toString(count));
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }

        });

        // get users' lend data (most recent at top)
        /*orderBy("post_date", Query.Direction.DESCENDING)*/
        db.collection("posts").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "task successful");
                    int count = 0;
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        cardList.add(new PostCard(s.getData().get("photo").toString(), s.getData().get("title").toString(), s.getData().get("deposit").toString(), s.getData().get("description").toString(), s.getData().get("username").toString(), s.getData().get("id").toString(), s.getData().get("post_date").toString()));
                        count++;
                    }
                    numPosts.setText("" + count);
                    mListView = findViewById(R.id.listViewProfileLends);
                    PostCardListAdapter adapter = new PostCardListAdapter(context, cardList, username);
                    if ((adapter != null) && (mListView != null)) {
                        mListView.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "not success");
                        System.out.println("Null Reference");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



        if (username.equals(myUsername)) {
            edit.setVisibility(View.VISIBLE);
            edit.setClickable(true);
            edit.setOnClickListener(new View.OnClickListener() {
                 @Override
                public void onClick(View view) {
                    Intent i = new Intent(UserAccount.this, UserAccountEditable.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
             numNeighbors.setClickable(true);
            numNeighbors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  Intent i;
                    i = new Intent(UserAccount.this, NeighborList.class);
                    i.putExtra("userList", userCards);
                    i.putExtra("username", username);
                    startActivity(i);
            }
        });
            rating.setVisibility(View.INVISIBLE);

    } else {
            edit.setVisibility(View.INVISIBLE);
            edit.setClickable(false);
            // num neighbors will only be clickable once it
            numNeighbors.setClickable(false);
            rating.setVisibility(View.VISIBLE);
            db.collection("transactions").whereEqualTo("borrower", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "task borrower successful");
                        for (QueryDocumentSnapshot s : task.getResult()) {
                            // know that we are the borrower
                            String r = s.get("borrowerRating").toString();
                            if (!r.equals("")) {
                                total += Double.parseDouble(r);
                                transactionCount++;
                            }
                            if (total < 0) {
                                rating.setText("Rating: N/A (No transactions completed)");
                            } else {
                                rating.setText("Rating: " + total / transactionCount);
                            }
                        }
                        // populate w/ request fragments
                        db.collection("transactions").whereEqualTo("lender", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "task lender successful");
                                    for (QueryDocumentSnapshot s : task.getResult()) {
                                        // know that we are the borrower
                                        String r = s.get("lenderRating").toString();
                                        if (!r.equals("")) {
                                            total += Double.parseDouble(r);
                                            transactionCount++;
                                        }
                                    }
                                    if (total < 0) {
                                        rating.setText("Rating: N/A (No transactions completed)");
                                    } else {
                                        rating.setText("Rating: " + total / transactionCount);
                                    }
                                } else{
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                        });

                    }
                    else{
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        }

    }
}
