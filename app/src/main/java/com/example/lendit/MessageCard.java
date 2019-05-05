package com.example.lendit;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageCard implements Parcelable {

    public String personUsername;
    public String personImgURL;
    public String lastMessage;
    public String myUsername;

    MessageCard(String person, String profile, String u, String mu){
        this.personUsername = person;
        this.personImgURL = profile;
        this.lastMessage = u;
        this.myUsername = mu;
    }

    MessageCard(Parcel p){
        this.personUsername = p.readString();
        this.personImgURL = p.readString();
        this.lastMessage = p.readString();
        this.myUsername = p.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.personUsername);
        out.writeString(this.personImgURL);
        out.writeString(this.lastMessage);
        out.writeString(this.myUsername);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<MessageCard> CREATOR = new Creator<MessageCard>() {
        public MessageCard createFromParcel(Parcel in) {
            return new MessageCard(in);
        }

        public MessageCard[] newArray(int size) {
            return new MessageCard[size];
        }
    };
}
