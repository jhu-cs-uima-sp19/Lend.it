package com.example.lendit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lendit.PostCard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 4/4/2017.
 */

public class CustomListAdapter extends ArrayAdapter<PostCard> {

    private static final String TAG = "CustomListAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    /*
     * Holds variables in a View

    private static class ViewHolder {
        TextView title;
        ImageView image;
        TextView person;
        TextView building;
        ImageView profilePic;
    }*/

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public CustomListAdapter(Context context, int resource, ArrayList<PostCard> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

        //sets up the image loader library
        setupImageLoader();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get post card information
        String title = getItem(position).getPostTitle();
        String person = getItem(position).getPersonName();
        String imgUrl = getItem(position).getImgURL();
        String building = getItem(position).getBuilding();
        String profileImg = getItem(position).getProfileImg();

        try {


            if (convertView == null){
               convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            }

            TextView titleTxt = (TextView) convertView.findViewById(R.id.cardTitle);
            ImageView postImgView = (ImageView) convertView.findViewById(R.id.cardImage);
            TextView personTxt = (TextView) convertView.findViewById(R.id.posterName);
            TextView buildingTxt = (TextView) convertView.findViewById(R.id.building);
            ImageView profilePicView = (ImageView) convertView.findViewById(R.id.posterImage);

            titleTxt.setText(title);
            buildingTxt.setText(building);
            personTxt.setText(person);

            Picasso.with(getContext()).load(imgUrl).into(postImgView);
            Picasso.with(getContext()).load(profileImg).into(profilePicView);



/*
            lastPosition = position;

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, image, options);
*/
            return convertView;
        }
/*
        try{
            //create the view result for showing the animation
            final View result;

            //ViewHolder object
            ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
                holder.image = (ImageView) convertView.findViewById(R.id.cardImage);
                holder.person = (TextView) convertView.findViewById(R.id.posterName);
                holder.building = (TextView) convertView.findViewById(R.id.building);
                holder.profilePic = (ImageView) convertView.findViewById(R.id.posterImage);
                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            lastPosition = position;

            // holder.title.setText(item);

            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, holder.image, options);

            return convertView;
        }*/catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}