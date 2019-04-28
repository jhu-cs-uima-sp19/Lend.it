package com.example.lendit;

import android.os.Parcel;
import android.os.Parcelable;

public class PostCard implements Parcelable {

    public String imgURL;
    public String postTitle;
    public String personName;
    public String building;
    public String profileImgURL;
    public String deposit;
    public String description;
    public String username;

    // Constructor for lend
    PostCard(String img, String item, String person, String building, String profile, String dep, String desc, String u){
        this.imgURL = "lendImages/" + img;
        this.postTitle = item;
        this.personName = person;
        this.building = building;
        this.profileImgURL = profile;
        this.deposit = dep;
        this.description = desc;
        this.username = u;
    }

    PostCard(Parcel p){
        this.imgURL = p.readString();
        this.postTitle = p.readString();
        this.personName = p.readString();
        this.building = p.readString();
        this.profileImgURL = p.readString();
        this.deposit = p.readString();
        this.description = p.readString();
        this.username = p.readString();
    }

    // Constructor for ask
    PostCard(String item, String person, String building, String profile, String desc, String u){
        this.postTitle = item;
        this.personName = person;
        this.building = building;
        this.profileImgURL = profile;
        this.description = desc;
        // default ask image
        this.imgURL = "appImages/opploans-how-to-lend-to-family.jpeg";
        // default deposit amount - will never be accessed
        this.deposit = "0";
        this.username = u;
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.imgURL);
        out.writeString(this.postTitle);
        out.writeString(this.personName);
        out.writeString(this.building);
        out.writeString(this.profileImgURL);
        out.writeString(this.deposit);
        out.writeString(this.description);
        out.writeString(this.username);
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
