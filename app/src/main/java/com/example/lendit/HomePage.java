package com.example.lendit;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String username;
    private static String TAG = "HomePageActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView mListView;
    Context context;
    Date rightNow;
    Timestamp toCompare;
    Timestamp getToTime;
    Spinner filters;
    List<String> spinnerFilters;
    String[] ratingList = {"1", "2", "3", "4", "5"};
    String rating = "RATING ISN'T UPDATING";

    StorageReference storage = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        context = this;
        rightNow = new Date();
        toCompare = new Timestamp(rightNow);
        filters = (Spinner) findViewById(R.id.filters);
        spinnerFilters = new ArrayList<String>(Arrays.asList("Availability", "Neighbors", "Post Date"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerFilters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filters.setAdapter(adapter);


        //Log.d(TAG, "Current Date and Time " + rightNow.toString());

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
        final TextView navUser = (TextView) hView.findViewById(R.id.userFullName);
        TextView title = (TextView) hView.findViewById(R.id.appTitle);
        title.setText("lend.it");

        showStartDialog();

        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> profileData = documentSnapshot.getData();
                final ImageView img = (ImageView) findViewById(R.id.navImg);
                final long ONE_MEGABYTE = 1024 * 1024;
                storage.child(profileData.get("profileImg").toString()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        img.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                navUser.setText(profileData.get("first").toString() + " " + profileData.get("last").toString());
            }
        });
    }

    private void showStartDialog() {
        db.collection("transactions").whereEqualTo("borrower", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (final QueryDocumentSnapshot s : task.getResult()) {
                        Log.d(TAG, "toTime " + s.get("to"));
                        getToTime = (Timestamp) s.get("to");
                        // if you're the borrower and you have not given the other person a rating (lender rating == "" when first created)
                        if ((toCompare.compareTo(getToTime) > 0) && (s.get("lenderRating").toString().equals(""))) {
                            // give -1 as rating since none exists
                            new AlertDialog.Builder(context)
                                    .setTitle("LEND IS DUE")
                                    .setMessage(s.get("postTitle").toString() + " is due! Have this item been returned?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // rating popup
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Rate the other user:")
                                                    .setSingleChoiceItems(ratingList, 0, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            rating = ratingList[which];
                                                        }
                                                    })
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // rating popup
                                                            DocumentReference ref = db.collection("transactions").document(s.get("id").toString());
                                                            ref.update("lenderRating", rating);
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    // need to be able to edit transaction
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // need to edit your transaction to extend time or report other user
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .create().show();
                                        }
                                    })
                                    // need to be able to edit transaction
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // need to edit your transaction to extend time or report other user
                                            new AlertDialog.Builder(context)
                                                    .setTitle("The appliance was not returned")
                                                    /*
                                                    .setItems(ratingList, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            rating = ratingList[which];
                                                        }
                                                    })
                                                     // need to be able to edit transaction
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // rating popup
                                                            DocumentReference ref = db.collection("transactions").document(s.get("id").toString());
                                                            ref.update("lenderRating", rating);
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                   */
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // need to edit your transaction to extend time or report other user
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .create().show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        db.collection("transactions").whereEqualTo("lender", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (final QueryDocumentSnapshot s : task.getResult()) {
                        Log.d(TAG, "toTime " + s.get("to"));
                        getToTime = (Timestamp) s.get("to");
                        // if you're the borrower and you have not given the other person a rating (lender rating == "" when first created)
                        if ((toCompare.compareTo(getToTime) > 0) && (s.get("borrowerRating").toString().equals(""))) {
                            // give -1 as rating since none exists
                            new AlertDialog.Builder(context)
                                    .setTitle("LEND IS DUE")
                                    .setMessage(s.get("postTitle").toString() + " is due! Have this item been returned?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // rating popup
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Rate the other user:")
                                                    .setSingleChoiceItems(ratingList, 0, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            rating = ratingList[which];
                                                        }
                                                    })
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // rating popup
                                                            DocumentReference ref = db.collection("transactions").document(s.get("id").toString());
                                                            ref.update("borrowerRating", rating);
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    // need to be able to edit transaction
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // need to edit your transaction to extend time or report other user
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .create().show();
                                        }
                                    })
                                    // need to be able to edit transaction
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // need to edit your transaction to extend time or report other user
                                            new AlertDialog.Builder(context)
                                                    .setTitle("The appliance was not returned")
                                                    /*
                                                    .setItems(ratingList, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            rating = ratingList[which];
                                                        }
                                                    })
                                                     // need to be able to edit transaction
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // rating popup
                                                            DocumentReference ref = db.collection("transactions").document(s.get("id").toString());
                                                            ref.update("lenderRating", rating);
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                   */
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // need to edit your transaction to extend time or report other user
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .create().show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


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
        final ArrayList<PostCard> cardList = new ArrayList();

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
        bundle.putString("myUsername", username);
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
             bundle.putString("myUsername", username);
            i.putExtras(bundle);
            startActivity(i);
        } else if (id == R.id.nav_lends) {
            i = new Intent(HomePage.this, TransactionLog.class);
            bundle.putString("username", username);
            i.putExtra("username", username);
            i.putExtras(bundle);
            startActivity(i);

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
