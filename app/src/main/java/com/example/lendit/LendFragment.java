package com.example.lendit;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toolbar;

import com.example.lendit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LendFragment extends Fragment {

    private static final String TAG = "CreateLendActivity";
    Button createLend;
    EditText lendDesc;
    // lend photo
    Button addPhoto;
    EditText deposit;
    EditText lendTitle;
    String photo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    //Bundle userData;
    FirebaseStorage storage = FirebaseStorage.getInstance();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lend_fragment, container, false);
        //setHasOptionsMenu(true);
        createLend = rootView.findViewById(R.id.createLendBTN);
        lendTitle = rootView.findViewById(R.id.lendTitleET);
        lendDesc = rootView.findViewById(R.id.lendDescriptionET);
        deposit = rootView.findViewById(R.id.depositET);
        addPhoto = rootView.findViewById(R.id.lendAddPhotoBTN);
        final CreatePost activity = (CreatePost) getActivity();
        final Map<String, String> userData = activity.getUserData();
        // listener for create lend button
        createLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLend(userData);
                //activity.launchHome();
            }
        });

        // listener for add photo button
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });

        return rootView;
    }

    public void createLend(Map<String, String> userData) {
        String uniqueID = UUID.randomUUID().toString();
        Map<String, Object> lend = new HashMap<>();
        lend.put("title", lendTitle.getText().toString());
        lend.put("description", lendDesc.getText().toString());
        lend.put("deposit", deposit.getText().toString());
        lend.put("photoID", photo);
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

    public void addPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageLabel.setImageBitmap(imageBitmap);
//            encodeBitmapAndSaveToFirebase(imageBitmap);
//        }
//    }
//
//    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                .getReference(Constants.FIREBASE_CHILD_RESTAURANTS)
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(mRestaurant.getPushId())
//                .child("imageUrl");
//        ref.setValue(imageEncoded);
//    }
}
