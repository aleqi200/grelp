package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FourSquarePhrase implements Parcelable {
    private String phrase;
    private int count;

    public FourSquarePhrase(String phrase, int count) {
        this.phrase = phrase;
        this.count = count;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phrase);
        dest.writeInt(this.count);
    }

    private FourSquarePhrase(Parcel in) {
        this.phrase = in.readString();
        this.count = in.readInt();
    }

    public static final Creator<FourSquarePhrase> CREATOR = new Creator<FourSquarePhrase>() {
        public FourSquarePhrase createFromParcel(Parcel source) {
            return new FourSquarePhrase(source);
        }

        public FourSquarePhrase[] newArray(int size) {
            return new FourSquarePhrase[size];
        }
    };
}
