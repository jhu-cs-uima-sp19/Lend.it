package com.example.lendit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestFragment extends Fragment {
    String username;
    private static String TAG = "RequestFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView mListView;
    boolean firstOpening;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request_fragment, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listViewRequests);

        /* hard code
        cardList.add(new TransactionCard("71754437-98dc-4fc9-854e-fe3364e0fa24"));
        cardList.add(new TransactionCard("8ade8990-caaf-4809-8c78-2a8ac7f0b39f"));
        cardList.add(new TransactionCard("914f76f2-47e5-4028-993b-8d8cad7a87c7"));
        cardList.add(new TransactionCard("ab9ab2e5-42b0-4eed-b97a-988c5d626cc3"));
        RequestListAdapter adapter = new RequestListAdapter(getActivity(), cardList, username);
        if ((adapter != null) && (mListView != null)) {
            mListView.setAdapter(adapter);
        } else {
            Log.d(TAG, "null");
            System.out.println("Null Reference");
        }*/


        return rootView;
    }

    public void repopulateList() {
        final ArrayList<TransactionCard> cardList = new ArrayList<TransactionCard>();
        db.collection("transactionRequests").whereEqualTo("lender", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "task borrower successful");
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        // give -1 as rating since none exists
                        if (s.exists()) {
                            cardList.add(new TransactionCard(s.getData().get("id").toString(), s.get("postTitle").toString()));
                        }
                    }
                    RequestListAdapter adapter = new RequestListAdapter(getActivity(), cardList, username);
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
    public void onStart() {
        super.onStart();
        firstOpening = true;
        Intent i = getActivity().getIntent();
        username = i.getStringExtra("username");

        db.collection("transactionRequests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshot,
                                @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                } else {repopulateList();}
               /* if (firstOpening) {
                    firstOpening = false;
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Transaction confirmed!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    repopulateList();
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }*/
            }
        });

        // populate w/ request fragments
        repopulateList();

    }


}
