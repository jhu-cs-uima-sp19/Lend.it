package com.example.lendit;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePost extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    Button createLend;
    EditText lendDesc;
    // lend photo
    Button addPhoto;
    Button createAsk;
    EditText askDesc;
    EditText deposit;
    EditText askTitle;
    EditText lendTitle;
    String photo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        createLend = findViewById(R.id.createLendBTN);
        lendTitle = findViewById(R.id.lendTitleET);
        lendDesc = findViewById(R.id.lendDescriptionET);
        deposit = findViewById(R.id.depositET);
        addPhoto = findViewById(R.id.lendAddPhotoBTN);
        createAsk = findViewById(R.id.createAskBTN);
        askDesc = findViewById(R.id.lendDescriptionET);
        ;
        askTitle = findViewById(R.id.askTitleET);


        // listener for create lend button
//        createLend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createLend();
//            }
//        });
//
//        // listener for add photo button
//        addPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String photo = "";
//            }
//        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new LendFragment(), "LEND");
        adapter.addFragment(new AskFragment(), "ASK");
        viewPager.setAdapter(adapter);
    }

    public void createLend() {
        String uniqueID = UUID.randomUUID().toString();
        Map<String, Object> lend = new HashMap<>();
        lend.put("title", lendTitle.getText().toString());
        lend.put("description", lendDesc.getText().toString());
        lend.put("deposit", deposit.getText().toString());
        lend.put("photoID", photo);
        lend.put("id", uniqueID);
        // get username from intent that launched this activity?
        // profile.put("username", );
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

    }

    public void createAsk() {
        String uniqueID = UUID.randomUUID().toString();
        Map<String, Object> ask = new HashMap<>();
        ask.put("title", askTitle.getText().toString());
        ask.put("description", askDesc.getText().toString());
        ask.put("id", uniqueID);
        // get username from intent that launched this activity?
        // profile.put("username", );
        db.collection("asks").document(uniqueID).set(ask).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}







