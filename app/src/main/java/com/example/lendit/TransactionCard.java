package com.example.lendit;
import android.os.Parcel;
import android.os.Parcelable;

public class TransactionCard implements Parcelable {
    public String transactionID;
    public String postTitle;

    TransactionCard(String tID, String title){
        this.transactionID = tID;
        this.postTitle = title;
    }

    TransactionCard(Parcel p){
        this.transactionID = p.readString();
        this.postTitle = p.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.transactionID);
        out.writeString(this.postTitle);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<TransactionCard> CREATOR = new Parcelable.Creator<TransactionCard>() {
        public TransactionCard createFromParcel(Parcel in) {
            return new TransactionCard(in);
        }

        public TransactionCard[] newArray(int size) {
            return new TransactionCard[size];
        }
    };
}
