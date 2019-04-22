package com.example.lendit;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPost extends AppCompatActivity {
    private static String TAG = "ViewPostActivity";
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    PostCard p;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        Intent i = getIntent();
        p = i.getParcelableExtra("post");
        username = i.getStringExtra("username");

        TextView title = findViewById(R.id.itemName);
        title.setText(p.getPostTitle());
        TextView username = findViewById(R.id.posted_by_TV);
        username.setText(p.getPersonName());
        TextView building = findViewById(R.id.building_tv);
        building.setText(p.getBuilding());
        TextView description = findViewById(R.id.item_descrip_text);
        description.setText(p.getDescription());
        // if posted by you, make button invisible and unclickable
        Button message = findViewById(R.id.message_giver);
        if (username.equals(p.getUsername())) {
            message.setClickable(false);
            message.setVisibility(View.INVISIBLE);
        }

        final ImageView profile = findViewById(R.id.profilePic);
        final ImageView pic = findViewById(R.id.uploadedPic);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child(p.getImgURL()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
        storageRef.child(p.getProfileImg()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

    public void sendMSG(View v) {
        Intent i = new Intent(ViewPost.this, MessageActivity.class);
        //need to send name?
        startActivity(i);

    }
}