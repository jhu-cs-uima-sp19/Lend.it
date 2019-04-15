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
     */
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
*/

    }
}