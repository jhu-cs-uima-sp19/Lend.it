package com.example.lendit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestFragment extends Fragment {
    String username;
    private static String TAG = "HomePageActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView mListView;
    ArrayList<TransactionCard> cardList = new ArrayList<TransactionCard>();
    Context context = this.getContext();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request_fragment, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listViewRequests);

        // populate w/ request fragments
        db.collection("transactionRequests").whereEqualTo("borrower", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "task successful");
                    for (QueryDocumentSnapshot s : task.getResult()) {
                        // give -1 as rating since none exists
                        cardList.add(new TransactionCard(-1, s.getData().get("id").toString()));
                    }
                    // populate w/ request fragments
                    db.collection("transactionsRequests").whereEqualTo("lender", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "task successful");
                                for (QueryDocumentSnapshot s : task.getResult()) {
                                    // give -1 as rating since none exists
                                    cardList.add(new TransactionCard(-1, s.getData().get("id").toString()));
                                }
                                RequestListAdapter adapter = new RequestListAdapter(context, cardList, username);
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
            }
        });

        return rootView;
    }


}
