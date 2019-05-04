package com.example.lendit;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class ViewPost extends AppCompatActivity {
    private static String TAG = "ViewPostActivity";
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    PostCard p;
    String username;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> profileData;
    Map<String, Object> postData;
    TextView name;
    TextView building;
    TextView title;
    TextView description;
    TextView deposit;
    Button delete;
    Switch availability;
    TextView availableTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        p = i.getParcelableExtra("post");
        username = i.getStringExtra("username");

        title = findViewById(R.id.itemName);
        availability = findViewById(R.id.availableToggle);
        availableTV = findViewById(R.id.availabilityTV);
        description = findViewById(R.id.item_descrip_text);

        final TextView name = findViewById(R.id.posted_by_TV);
        final TextView building = findViewById(R.id.building_tv);
        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
            }
        });

        final ImageView profile = findViewById(R.id.profilePic);
        final long ONE_MEGABYTE = 1024 * 1024;
        db.collection("users").document(p.username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                postData = documentSnapshot.getData();
                name.setText(postData.get("first").toString() + " " + postData.get("last").toString());
                building.setText(postData.get("building").toString());
                Log.d(TAG, postData.get("first").toString());
                if (postData.get("profileImg").toString().equals("appImages/avatar.png")) {
                    profile.setImageResource(R.drawable.avatar);
                } else {
                    storageRef.child(postData.get("profileImg").toString()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            profile.setImageBitmap(bitmap);
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

        //building.setText(postData.get("building").toString());

        // if posted by you, make button invisible and unclickable
        Button requestTransaction = findViewById(R.id.requestTransaction);
        Button message = findViewById(R.id.message_giver);
        Button editPost = findViewById(R.id.editPostBTN);
        delete = findViewById(R.id.delete);
        if (username.equals(p.username)) {
            message.setClickable(false);
            message.setVisibility(View.INVISIBLE);
            requestTransaction.setClickable(false);
            requestTransaction.setVisibility(View.INVISIBLE);
            editPost.setClickable(true);
            editPost.setVisibility(View.VISIBLE);
            delete.setClickable(true);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewPost.this)
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete this post?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("posts").document(p.postID)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                    ViewPost.this.finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        delete.setVisibility(View.VISIBLE);
        availableTV.setVisibility(View.VISIBLE);
        availability.setVisibility(View.VISIBLE);

    } else {
            message.setClickable(true);
            message.setVisibility(View.VISIBLE);
            requestTransaction.setClickable(true);
            requestTransaction.setVisibility(View.VISIBLE);
            editPost.setClickable(false);
            editPost.setVisibility(View.INVISIBLE);
            delete.setClickable(false);
            delete.setVisibility(View.INVISIBLE);
            availableTV.setVisibility(View.INVISIBLE);
            availability.setVisibility(View.INVISIBLE);
        }

        deposit = findViewById(R.id.deposit_TV);
        // if deposit is 0, don't show field (for asks and for lends)
//        if (p.deposit == "0") {
//            deposit.setVisibility(View.INVISIBLE);
//        } else {
//            deposit.setVisibility(View.VISIBLE);
//            deposit.setText("$"+p.deposit);
//        }


        final ImageView pic = findViewById(R.id.uploadedPic);
        if (p.imgURL.equals("appImages/ask.JPG")) {
            pic.setImageResource(R.drawable.ask);
        } else {
            storageRef.child(p.imgURL).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        requestTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewPost.this, CreateLendTransaction.class);
                i.putExtra("username", username);
                i.putExtra("post", p);
                startActivity(i);

            }
        });

        availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                DocumentReference ref = db.collection("posts").document(p.postID);
                if (isChecked) {
                    //make database changes
                    ref.update("available", true);
                    availableTV.setText("Available");
                } else {
                    ref.update("available", false);
                    availableTV.setText("Unavailable");
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        db.collection("posts").document(p.postID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                postData = documentSnapshot.getData();
                title.setText(postData.get("title").toString());
                description.setText(postData.get("description").toString());

                if (postData.get("available").toString().equals("true")) {
                    availability.setChecked(true);
                    availableTV.setText("Available");
                } else {
                    availability.setChecked(false);
                    availableTV.setText("Unavailable");
                }

                if (postData.get("deposit").toString() == "0") {
                    deposit.setVisibility(View.INVISIBLE);
                } else {
                    deposit.setVisibility(View.VISIBLE);
                    deposit.setText("$"+postData.get("deposit").toString());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_post, menu);
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
            ViewPost.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editPost(View v) {
        Intent i = new Intent(ViewPost.this, ViewPostEditable.class);
        i.putExtra("username", username);

        startActivity(i);
    }

    public void sendMSG(View v) {
        Intent i = new Intent(ViewPost.this, Chat.class);
        //need to send name?
        i.putExtra("username", username);
        i.putExtra("postuser", p.username);
        startActivity(i);

    }
}
