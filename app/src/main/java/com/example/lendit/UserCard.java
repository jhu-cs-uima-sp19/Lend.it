package com.example.lendit;

import android.os.Parcel;
import android.os.Parcelable;

public class UserCard implements Parcelable {

    public String personName;
    public String building;
    public String profileImgURL;
    public String username;
    public String myUsername;

    UserCard(String person, String building, String profile, String u, String mu){
        this.personName = person;
        this.building = building;
        this.profileImgURL = profile;
        this.username = u;
        this.myUsername = mu;
    }

    UserCard(Parcel p){
        this.personName = p.readString();
        this.building = p.readString();
        this.profileImgURL = p.readString();
        this.username = p.readString();
        this.myUsername = p.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.personName);
        out.writeString(this.building);
        out.writeString(this.profileImgURL);
        out.writeString(this.username);
        out.writeString(this.myUsername);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<UserCard> CREATOR = new Parcelable.Creator<UserCard>() {
        public UserCard createFromParcel(Parcel in) {
            return new UserCard(in);
        }

        public UserCard[] newArray(int size) {
            return new UserCard[size];
        }
    };
}
