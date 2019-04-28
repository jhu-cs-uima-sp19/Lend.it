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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import android.content.Intent;

public class UserCardListAdapter extends ArrayAdapter<UserCard> {
    private final ArrayList<UserCard> users;
    private Context context;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public UserCardListAdapter(Context c, ArrayList<UserCard> objects) {
        super(c, 0, objects);
        this.context = c;
        this.users = objects;
    }

    private class ViewHolder {
        private TextView userName;
        private TextView userBuilding;
        private ImageView userImage;

        public ViewHolder() {}
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserCard p = users.get(position);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_activity, parent, false);
            holder.userName = convertView.findViewById(R.id.userName);
            holder.userBuilding = convertView.findViewById(R.id.userBuilding);
            holder.userImage = convertView.findViewById(R.id.userImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(p.personName);
        holder.userBuilding.setText(p.building);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child(p.profileImgURL).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.userImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // making entire post clickable
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewProfile(p.username);
            }
        });
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewProfile(p.username);
            }
        });
        holder.userBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewProfile(p.username);
            }
        });

        return convertView;
    }

    public void toViewProfile(String other) {
        Intent i;
        i = new Intent(context, UserAccount.class);
        // passing selected user's username
        i.putExtra("username", other);
        context.startActivity(i);
    }
}