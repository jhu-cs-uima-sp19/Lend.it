package com.example.lendit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RequestListAdapter extends ArrayAdapter<TransactionCard> {
    private final ArrayList<TransactionCard> transactions;
    private Context context;
    private String username;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "RequestListAdapter";

    public RequestListAdapter(Context c, ArrayList<TransactionCard> objects, String u) {
        super(c, 0, objects);
        this.context = c;
        this.transactions = objects;
        this.username = u;
    }

    private class ViewHolder {
        TextView building;
        TextView name;
        TextView timeFrom;
        TextView timeTo;
        TextView title;
        ImageView profilePic;
        Button approve;
        Button reject;
        Map<String, Object> transaction;

        public ViewHolder() {
        }
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TransactionCard p = transactions.get(position);
        Log.d(TAG, "ID " + p.transactionID);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.request_card_activity, parent, false);
            holder.building = convertView.findViewById(R.id.requestBuilding);
            holder.name = convertView.findViewById(R.id.requestName);
            holder.title = convertView.findViewById(R.id.requestTitle);
            holder.profilePic = convertView.findViewById(R.id.requestImage);
            holder.approve = convertView.findViewById(R.id.requestApprove);
            holder.reject = convertView.findViewById(R.id.requestReject);
            holder.timeFrom = convertView.findViewById(R.id.requestTimeFrom);
            holder.timeTo = convertView.findViewById(R.id.requestTimeTo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        db.collection("transactionRequests").document(p.transactionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.transaction = documentSnapshot.getData();
                Map<String, Object> t = documentSnapshot.getData();
                String otherName;
                if (t.get("borrower").toString().equals(username)) {
                    otherName = t.get("lender").toString();
                } else {
                    otherName = t.get("borrower").toString();
                }

                holder.timeFrom.setText("From: " + t.get("from"));
                holder.timeTo.setText("To: " + t.get("to"));
                holder.title.setText(t.get("postTitle").toString());

                db.collection("users").document(otherName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> u = documentSnapshot.getData();
                         holder.name.setText(u.get("first").toString() + " " + u.get("last").toString());
                        holder.building.setText(u.get("building").toString());
                        
                        final long ONE_MEGABYTE = 1024 * 1024;
                        storageRef.child(u.get("profileImg").toString()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.profilePic.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                });
            }
        });

        // set on click listener for approval - add to transaction db collection
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Confirm Transaction")
                        .setMessage("Are you sure you want to create this transaction?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        db.collection("transactionRequests").document(p.transactionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> t = documentSnapshot.getData();
                                Map<String, Object> transaction = new HashMap<String, Object>();
                                transaction.put("borrower", t.get("borrower").toString());
                                transaction.put("deposit", t.get("deposit").toString());
                                transaction.put("from", t.get("from"));
                                transaction.put("to", t.get("to"));
                                transaction.put("id", t.get("id").toString());
                                transaction.put("lender", t.get("lender").toString());
                                transaction.put("postTitle", t.get("postTitle").toString());
                                transaction.put("deposit", t.get("deposit").toString());
                                // no rating yet - will be updated once lend has transpired
                                transaction.put("borrowerRating", "");
                                transaction.put("lenderRating", "");
                                */
                                holder.transaction.put("borrowerRating", "");
                                holder.transaction.put("lenderRating", "");
                                db.collection("transactions").document(holder.transaction.get("id").toString()).set(holder.transaction);
                                db.collection("transactionRequests").document(p.transactionID)
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
        // set on click listener for rejection - delete from request db collection
        holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Confirm Transaction Rejection")
                            .setMessage("Are you sure you want to reject this transaction?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("transactionRequests").document(p.transactionID)
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
        return convertView;
    }
}