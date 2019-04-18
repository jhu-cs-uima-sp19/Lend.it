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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<PostCard> {
    public interface OnItemClickedListener {
        void onItemClick(PostCard item);
    }

    private static final String TAG = "CustomListAdapter";
    private final ArrayList<PostCard> posts;
    private Context context;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public CustomListAdapter(Context c, ArrayList<PostCard> objects) {
        super(c, 0, objects);
        this.context = c;
        this.posts = objects;
    }

    //implements View.OnClickListener
    private class ViewHolder {
        private TextView titleTxt;
        private TextView personTxt;
        private TextView buildingTxt;
        private ImageView postImgView;
        private ImageView profilePicView;

        public ViewHolder() {}

        /*@Override
        public void onClick(View v) {}

        public void setOnClickListener(View.OnClickListener v) {

        }*/
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
        holder.titleTxt.setText(p.getPostTitle());
        holder.personTxt.setText(p.getPersonName());
        holder.buildingTxt.setText(p.getBuilding());
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child(p.getImgURL()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
        storageRef.child(p.getProfileImg()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        holder.postImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewPost.class);
                i.putExtra("post", p);
                context.startActivity(i);
            }
        });

        return convertView;
    }
}
            /* dummy data
            personTxt.setText(person);
            if (person.equals("Ryan")) {
                postImgView.setImageResource(R.drawable.bath);
            } else if (person.equals("Taryn")) {
                postImgView.setImageResource(R.drawable.kitchen);
            } else if (person.equals("Ravina")) {
                postImgView.setImageResource(R.drawable.stove);
            }
            profilePicView.setImageResource(R.drawable.ic_person_black_24dp);
*/


/*
public class CustomListAdapter extends ArrayAdapter<PostCard> {

    private static final String TAG = "CustomListAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects

    public CustomListAdapter(Context context, int resource, ArrayList<PostCard> objects) {
        super(context, resource, objects);
        Log.d(TAG, "Created Custome List AD");
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get post card information
        String title = getItem(position).getPostTitle();
        String person = getItem(position).getPersonName();
        String imgURL = getItem(position).getImgURL();
        String building = getItem(position).getBuilding();
        String profileImg = getItem(position).getProfileImg();

        try {
            if (convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            }

            TextView titleTxt = convertView.findViewById(R.id.cardTitle);
            final ImageView postImgView = convertView.findViewById(R.id.cardImage);
            TextView personTxt = convertView.findViewById(R.id.posterName);
            TextView buildingTxt = convertView.findViewById(R.id.posterBuilding);
            final ImageView profilePicView = convertView.findViewById(R.id.posterImage);

            titleTxt.setText(title);
            buildingTxt.setText(building);
            personTxt.setText(person);

            // post image
            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.child(imgURL).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    postImgView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            // profile image
            storageRef.child(profileImg).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profilePicView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            return convertView;
        } catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

            /* dummy data
            personTxt.setText(person);
            if (person.equals("Ryan")) {
                postImgView.setImageResource(R.drawable.bath);
            } else if (person.equals("Taryn")) {
                postImgView.setImageResource(R.drawable.kitchen);
            } else if (person.equals("Ravina")) {
                postImgView.setImageResource(R.drawable.stove);
            }
            profilePicView.setImageResource(R.drawable.ic_person_black_24dp);


    }
}*/