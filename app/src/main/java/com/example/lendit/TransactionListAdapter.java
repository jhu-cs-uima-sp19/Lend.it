package com.example.lendit;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.support.annotation.NonNull;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import java.util.ArrayList;
        import java.util.Map;

        import static android.support.constraint.Constraints.TAG;

public class TransactionListAdapter extends ArrayAdapter<TransactionCard> {
    private final ArrayList<TransactionCard> transactions;
    private Context context;
    private String username;
    final String TAG = "TransactionListAdapter";

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TransactionListAdapter(Context c, ArrayList<TransactionCard> objects, String u) {
        super(c, 0, objects);
        this.context = c;
        this.transactions = objects;
        this.username = u;
    }

    private class ViewHolder {
        TextView building;
        TextView name;
        TextView rating;
        TextView title;
        ImageView profilePic;
        public ViewHolder() {}
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
            convertView = inflater.inflate(R.layout.transaction_card_activity, parent, false);
            holder.building = convertView.findViewById(R.id.otherBuilding);
            holder.name = convertView.findViewById(R.id.otherName);
            holder.rating = convertView.findViewById(R.id.otherRating);
            holder.title = convertView.findViewById(R.id.transactionTitle);
            holder.profilePic = convertView.findViewById(R.id.otherImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        db.collection("transactions").document(p.transactionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> t = documentSnapshot.getData();
                String otherName = "";

                if (t.get("borrower").toString().equals(username)) {
                    otherName = t.get("lender").toString();
                } else {
                    otherName = t.get("borrower").toString();
                }
                holder.name.setText(otherName);
                holder.rating.setText("Rating " + t.get("rating").toString());

                db.collection("posts").document(t.get("postID").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> p = documentSnapshot.getData();
                        holder.title.setText(p.get("title").toString());
                    }
                });
/*
                    db.collection("users").document(otherName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> u = documentSnapshot.getData();
                            holder.building.setText(u.get("building").toString());
                            final long ONE_MEGABYTE = 1024 * 1024;
                            storageRef.child(u.get("photo").toString()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                    });*/


                // hard code
                holder.building.setText("Charles Commons");
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.child("appImages/logo.png").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        return convertView;
    }

}