package com.example.lendit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAccountEditable extends AppCompatActivity {
    Spinner building;
    List<String> spinnerBuildings;
    String username;
    EditText first;
    EditText last;
    Button apply;
    Button cancel;
    Button changePic;
    ImageView pic;
    Bundle b;
    Map<String, Object> profileData;

    private static final String TAG = "UserAccountEditableActivity";
    String photo = "appImages/add-picture-icon-13.png";
    Bitmap bitmap;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_editable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b = getIntent().getExtras();
        if (b != null) {
            username = b.getString("username");
        }

        apply = (Button) findViewById(R.id.applyChangesBTN);
        cancel = (Button) findViewById(R.id.cancelBTN);
        changePic = findViewById(R.id.changePicBTN);
        pic = findViewById(R.id.profilePic);

        first = (EditText) findViewById(R.id.firstNameET);
        last = (EditText) findViewById(R.id.lastNameET);
        building = (Spinner) findViewById(R.id.buildingSpinner);
        spinnerBuildings = new ArrayList<String>(Arrays.asList("Charles Commons", "McCoy", "Bradford", "AMRI", "AMRII", "AMRIIIA", "AMRIIIB", "Wolman", "The Charles", "Homewood", "The Academy", "The Social", "Uni West", "100 West"));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerBuildings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building.setAdapter(adapter);

        db.collection("users").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileData = documentSnapshot.getData();
                first.setText(profileData.get("first").toString());
                last.setText(profileData.get("last").toString());
                building.setSelection(adapter.getPosition(profileData.get("building").toString()));
                // populate with normal generic photo
                final long ONE_MEGABYTE = 1024 * 1024;
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
        });


        // listener for change pic button
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccountEditable.this.finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeChanges();
            }
        });
    }

    public void makeChanges() {
        //make database changes
        DocumentReference ref = db.collection("users").document(username);
        ref.update("first", first.getText().toString());
        ref.update("last", last.getText().toString());
        ref.update("building", building.getSelectedItem().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UserAccountEditable.this, "Updated Successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });

        UserAccountEditable.this.finish();
    }

    /**
     * Uploads photo to storage.
     */
    public void uploadPhoto() {
        Bitmap bitmap = ((BitmapDrawable) pic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storage.child("lendImages/" + photo).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }


    /**
     * Selects image from gallery or camera.
     */
    private void selectImage() {
        try {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Select Option");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        dialog.dismiss();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, PICK_IMAGE_CAMERA);
                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                pic.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                pic.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        photo = UUID.randomUUID().toString();
    }
}
