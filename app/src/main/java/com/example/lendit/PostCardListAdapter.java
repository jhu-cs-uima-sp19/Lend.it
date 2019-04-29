package com.example.lendit;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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

import android.content.Intent;

public class PostCardListAdapter extends ArrayAdapter<PostCard> {
    private static final String TAG = "CustomListAdapter";
    private final ArrayList<PostCard> posts;
    private Context context;
    private String username;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PostCardListAdapter(Context c, ArrayList<PostCard> objects, String u) {
        super(c, 0, objects);
        this.context = c;
        this.posts = objects;
        this.username = u;
    }

    private class ViewHolder {
        private TextView titleTxt;
        private TextView personTxt;
        private TextView buildingTxt;
        private ImageView postImgView;
        private ImageView profilePicView;

        public ViewHolder() {}
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PostCard p = posts.get(position);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_activity, parent, false);
            holder.titleTxt = convertView.findViewById(R.id.cardTitle);
            holder.personTxt = convertView.findViewById(R.id.posterName);
            holder.buildingTxt = convertView.findViewById(R.id.posterBuilding);
            holder.postImgView = convertView.findViewById(R.id.cardImage);
            holder.profilePicView = convertView.findViewById(R.id.posterImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleTxt.setText(p.postTitle);

        db.collection("users").document(p.username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object>  profileData = documentSnapshot.getData();
                holder.buildingTxt.setText(profileData.get("building").toString());
                holder.personTxt.setText(profileData.get("first").toString() + " " + profileData.get("last").toString());
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.child(p.imgURL).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.postImgView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                storageRef.child(profileData.get("profileImg").toString()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.profilePicView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });

        // making entire post clickable
        holder.postImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               toViewPost(p);
            }
        });
        holder.titleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPost(p);
            }
        });
        holder.personTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPost(p);
            }
        });
        holder.buildingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPost(p);
            }
        });
        holder.profilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPost(p);
            }
        });

        return convertView;
    }

    public void toViewPost(PostCard p) {
        Intent i;
        i = new Intent(context, ViewPost.class);
        i.putExtra("post", p);
        i.putExtra("username", this.username);
        context.startActivity(i);
    }
}