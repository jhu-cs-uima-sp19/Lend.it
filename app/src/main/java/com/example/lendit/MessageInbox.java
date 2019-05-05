package com.example.lendit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class MessageInbox extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MessageInbox";
    /*
    String[] nameArray = {"Taryn Wong" };

    String[] messageArray = {
            "Can I borrow your USB?",


    };

    Integer[] imageArray = {R.drawable.avatar,
            R.drawable.avatar,
    };*/

    ListView listView;
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Bundle bundle;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //MessageCustomListAdapter whatever = new MessageCustomListAdapter(this, nameArray, messageArray, imageArray);
        listView = (ListView) findViewById(R.id.listViewMessages);
        //listView.setAdapter(whatever);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        final TextView navUser = (TextView) hView.findViewById(R.id.nameTxt);
        //ImageView imgvw = (ImageView) hView.findViewById(R.id.profpic);
        bundle = getIntent().getExtras();
        username = bundle.getString("username");
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

    @Override
    public void onStart() {
        super.onStart();
        db.collection("messages").whereEqualTo("user1", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<MessageCard> messageList = new ArrayList<MessageCard>();
                    for (final QueryDocumentSnapshot s : task.getResult()) {
                        ArrayList<String> users = (ArrayList<String>) s.getData().get("chatters");
                        final ArrayList<Map<String, Object>> messages = (ArrayList<Map<String, Object>>) s.getData().get("messages");
                        final String theirName;
                            db.collection("users").document(s.get("user2").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String, Object> profileData = documentSnapshot.getData();
                                    Log.d(TAG, "message" + messages.get(messages.size() - 1).get("content").toString());
                                    messageList.add(new MessageCard(s.get("user2").toString(), profileData.get("profileImg").toString(), messages.get(messages.size() - 1).get("content").toString(), username));
                                    MessageCustomListAdapter adapter = new MessageCustomListAdapter(MessageInbox.this, messageList);
                                    Log.d(TAG, "MESSAGE LIST SIZE" + messageList.size());
                                    if ((adapter != null) && (listView != null)) {
                                        listView.setAdapter(adapter);
                                    } else {
                                        System.out.println("Null Reference");
                                    }
                                }
                            });
                        }
                } else{
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
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_inbox, menu);
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
            MessageInbox.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void toAccount(View v) {
        Intent i;
        Bundle bundle = new Bundle();
        i = new Intent(MessageInbox.this, UserAccount.class);
        bundle.putString("username", username);
        i.putExtras(bundle);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;
        Bundle bundle = new Bundle();
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            i = new Intent(MessageInbox.this, HomePage.class);
            bundle.putString("username", username);
            i.putExtras(bundle);
            startActivity(i);
        } else if (id == R.id.nav_msg) {


        } else if (id == R.id.nav_acc) { //Account
            i = new Intent(MessageInbox.this, UserAccount.class);
            bundle.putString("username", username);
             bundle.putString("myUsername", username);
            i.putExtras(bundle);
            startActivity(i);
        } else if (id == R.id.nav_lends) {

        }  else if (id == R.id.nav_logout)  {
            i = new Intent(MessageInbox.this, MainActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
