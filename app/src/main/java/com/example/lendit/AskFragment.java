package com.example.lendit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AskFragment extends Fragment {

    private static final String TAG = "CreateAskActivity";
    Button createAsk;
    EditText askDesc;
    EditText askTitle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ask_fragment, container, false);

        createAsk = rootView.findViewById(R.id.createAskBTN);
        askDesc = rootView.findViewById(R.id.lendDescriptionET);
        askTitle = rootView.findViewById(R.id.askTitleET);

        // listener for create lend button

        createAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAsk();
            }
        });


        return rootView;
    }


    public void createAsk() {
        String uniqueID = UUID.randomUUID().toString();
        Map<String, Object> ask = new HashMap<>();
        ask.put("title", askTitle.getText().toString());
        ask.put("description", askDesc.getText().toString());
        ask.put("id", uniqueID);
        ask.put("post_date", Calendar.getInstance().getTime());
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
