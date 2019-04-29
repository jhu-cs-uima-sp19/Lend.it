package com.example.lendit;
import android.os.Parcel;
import android.os.Parcelable;

public class TransactionCard implements Parcelable {

    public double rating;
    public String transactionID;
    String username;

    TransactionCard(double r, String tID, String u){
        this.rating = r;;
        this.transactionID = tID;
        this.username = u;
    }

    TransactionCard(Parcel p){
        this.rating = p.readDouble();
        this.transactionID = p.readString();
        this.username = p.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(this.rating);
        out.writeString(this.transactionID);
        out.writeString(this.username);
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
