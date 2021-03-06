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
        import com.google.firebase.Timestamp;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
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
        TextView from;
        TextView to;
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
            holder.from = convertView.findViewById(R.id.fromTime);
            holder.to = convertView.findViewById(R.id.toTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        db.collection("transactions").document(p.transactionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> t = documentSnapshot.getData();
                String otherName = "";
                String rating;

                if (t.get("borrower").toString().equals(username)) {
                    otherName = t.get("lender").toString();
                    rating = t.get("lenderRating").toString();
                } else {
                    otherName = t.get("borrower").toString();
                    rating = t.get("borrowerRating").toString();
                }

                if (rating.equals("")) {
                    rating = "N/A: Lend still in progress";
                }

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                Timestamp from = (Timestamp)t.get("from");
                Date fromDate = from.toDate();
                Timestamp to =  (Timestamp)t.get("to");
                Date toDate = to.toDate();
                holder.from.setText("From: " + sdf.format(fromDate));
                holder.to.setText("To: " + sdf.format(toDate));
                holder.title.setText(t.get("postTitle").toString());

                holder.rating.setText("Rating: " + rating);
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

        return convertView;
    }

}