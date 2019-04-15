package com.example.lendit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.lendit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LendFragment extends Fragment {

    private static final String TAG = "CreateLendActivity";
    Button createLend;
    EditText lendDesc;
    // lend photo
    Button addPhoto;
    Button addPhotoFromGallery;
    EditText deposit;
    EditText lendTitle;
    String photo;
    ImageView display;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int SELECT_PHOTO = 100;
    //Bundle userData;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    //private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    CreatePost activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lend_fragment, container, false);
        createLend = rootView.findViewById(R.id.createLendBTN);
        lendTitle = rootView.findViewById(R.id.lendTitleET);
        lendDesc = rootView.findViewById(R.id.lendDescriptionET);
        deposit = rootView.findViewById(R.id.depositET);
        display = rootView.findViewById(R.id.displayIV);
        //Picasso.with(getContext()).load("gs://lendit-af5be.appspot.com/appImages/add-picture-icon-13.png").into(display);

        activity = (CreatePost) getActivity();

        final Map<String, String> userData = activity.getUserData();

        // listener for create lend button
        createLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createLend(userData);
            }
        });

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        return rootView;
    }

    public void createLend(Map<String, String> userData) {
        String uniqueID = UUID.randomUUID().toString();
        uploadPhoto();

        Map<String, Object> lend = new HashMap<>();
        lend.put("title", lendTitle.getText().toString());
        lend.put("description", lendDesc.getText().toString());
        lend.put("deposit", deposit.getText().toString());
        if (photo != null) {
            lend.put("photoID", photo);
        } else {
            lend.put("photoID", "gs://lendit-af5be.appspot.com/appImages/opploans-how-to-lend-to-family.jpg");
        }
        lend.put("id", uniqueID);
        lend.put("post_date", Calendar.getInstance().getTime());
        lend.put("username", userData.get("username"));
        lend.put("fullName", userData.get("fullName"));
        lend.put("building", userData.get("building"));
        lend.put("profileImg", userData.get("profileImg"));
        db.collection("lends").document(uniqueID).set(lend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void uploadPhoto() {
        Bitmap bitmap = ((BitmapDrawable) display.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storage.getReference().child("lendImages/" + photo).putBytes(data);
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


    // Select image from camera and gallery
    private void selectImage() {
        try {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
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
            Toast.makeText(getContext(), "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");
                display.setImageBitmap(bitmap);
                photo = UUID.randomUUID().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                display.setImageBitmap(bitmap);
                photo = UUID.randomUUID().toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
