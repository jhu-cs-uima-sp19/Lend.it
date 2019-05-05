package com.example.lendit;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import android.content.Intent;

public class MessageCustomListAdapter extends ArrayAdapter<MessageCard> {
    private final ArrayList<MessageCard> chats;
    private Context context;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public MessageCustomListAdapter(Context c, ArrayList<MessageCard> objects) {
        super(c, 0, objects);
        this.context = c;
        this.chats = objects;
    }

    private class ViewHolder {
        private TextView userName;
        private TextView lastMessage;
        private ImageView otherImage;

        public ViewHolder() {}
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MessageCard p = chats.get(position);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_message, parent, false);
            holder.userName = convertView.findViewById(R.id.nameTextView);
            holder.lastMessage = convertView.findViewById(R.id.messageTextView);
            holder.otherImage = convertView.findViewById(R.id.person1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String TAG = "MessageListAdapter";
        Log.d(TAG,"in the list adapter");
        holder.userName.setText(p.personUsername);
        holder.lastMessage.setText(p.lastMessage);
        final long ONE_MEGABYTE = 1024 * 1024;
        if (p.personImgURL.equals("appImages/avatar.png")) {
            holder.otherImage.setImageResource(R.drawable.avatar);
        } else {
            storageRef.child(p.personImgURL).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.otherImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        // making entire post clickable
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChat(p.personUsername, p.myUsername);
            }
        });
        holder.lastMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChat(p.personUsername, p.myUsername);
            }
        });
        holder.otherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toChat(p.personUsername, p.myUsername);
            }
        });

        return convertView;
    }

    public void toChat(String otherUsername, String myUsername) {
        Intent i;
        i = new Intent(context, Chat.class);
        Bundle b = new Bundle();
        // passing selected user's username
        b.putString("username", myUsername);
        b.putString("postuser", otherUsername);
        context.startActivity(i);
    }
}