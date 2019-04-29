package com.example.lendit;

import android.os.Parcel;
import android.os.Parcelable;

public class PostCard implements Parcelable {

    public String imgURL;
    public String postTitle;
    public String deposit;
    public String description;
    public String username;
    public String postID;
    public String date;

    PostCard(String img, String item, String dep, String desc, String u, String id, String d){
        this.imgURL = img;
        this.postTitle = item;
        this.deposit = dep;
        this.description = desc;
        this.username = u;
        this.postID = id;
        this.date = d;
    }

    PostCard(Parcel p){
        this.imgURL = p.readString();
        this.postTitle = p.readString();
        this.deposit = p.readString();
        this.description = p.readString();
        this.username = p.readString();
        this.postID = p.readString();
        this.date = p.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.imgURL);
        out.writeString(this.postTitle);
        out.writeString(this.deposit);
        out.writeString(this.description);
        out.writeString(this.username);
        out.writeString(this.postID);
        out.writeString(this.date);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<PostCard> CREATOR = new Parcelable.Creator<PostCard>() {
        public PostCard createFromParcel(Parcel in) {
            return new PostCard(in);
        }

        public PostCard[] newArray(int size) {
            return new PostCard[size];
        }
    };
}
