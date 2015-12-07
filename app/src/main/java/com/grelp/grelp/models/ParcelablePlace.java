package com.grelp.grelp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

public class ParcelablePlace implements Parcelable {
    public String id;
    public String name;
    public String address;
    public LatLng latLng;
    public Uri websiteUri;
    public String phoneNumber;
    public float rating;
    public int priceLevel;

    protected ParcelablePlace(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.websiteUri = Uri.parse(in.readString());
        this.phoneNumber = in.readString();
        this.rating = in.readFloat();
        this.priceLevel = in.readInt();
    }

    public ParcelablePlace(Place place) {
        this.id = place.getId();
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.latLng = place.getLatLng();
        this.websiteUri = place.getWebsiteUri();
        this.phoneNumber = place.getPhoneNumber().toString();
        this.rating = place.getRating();
        this.priceLevel = place.getPriceLevel();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeParcelable(latLng, 0);
        dest.writeString(websiteUri.toString());
        dest.writeString(phoneNumber);
        dest.writeFloat(rating);
        dest.writeInt(priceLevel);
    }

    public static final Creator<ParcelablePlace> CREATOR = new Creator<ParcelablePlace>() {
        public ParcelablePlace createFromParcel(Parcel source) {
            return new ParcelablePlace(source);
        }

        public ParcelablePlace[] newArray(int size) {
            return new ParcelablePlace[size];
        }
    };
}
