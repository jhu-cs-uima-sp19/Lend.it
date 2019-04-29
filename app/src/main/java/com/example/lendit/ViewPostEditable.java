package com.example.lendit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPostEditable extends AppCompatActivity {
    String username;
    EditText desc;
    EditText deposit;
    EditText title;
    Button apply;
    Button cancel;
    Bundle b;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storage = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_editable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b = getIntent().getExtras();
        if (b != null) {
            username = b.getString("username");
        }

        apply = (Button) findViewById(R.id.applyChangesBTN);
        cancel = (Button) findViewById(R.id.cancelBTN);

        desc = (EditText) findViewById(R.id.item_descrip_text);
        deposit = (EditText) findViewById(R.id.depositET);
        title = (EditText) findViewById(R.id.title_ET);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPostEditable.this.finish();
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
        DocumentReference ref = db.collection("lendTransactions").document(username);
//        ref.update("first", first.getText().toString());
//        ref.update("last", last.getText().toString());
//        ref.update("building", building.getSelectedItem().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(ViewPostEditable.this, "Updated Successfully",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        ViewPostEditable.this.finish();
    }

}
