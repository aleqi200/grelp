package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class YelpUser implements Parcelable {
    private final String id;
    private final String profileUrl;
    private final String name;

    public YelpUser(String id, String profileUrl, String name) {
        this.id = id;
        this.profileUrl = profileUrl;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getName() {
        return name;
    }

    public static YelpUser fromJSONObject(JSONObject userObject) throws JSONException {
        String id = userObject.getString("id");
        String profileUrl = userObject.getString("image_url");
        String name = userObject.getString("name");
        return new YelpUser(id, profileUrl, name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.profileUrl);
        dest.writeString(this.name);
    }

    protected YelpUser(Parcel in) {
        this.id = in.readString();
        this.profileUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<YelpUser> CREATOR = new Parcelable.Creator<YelpUser>() {
        public YelpUser createFromParcel(Parcel source) {
            return new YelpUser(source);
        }

        public YelpUser[] newArray(int size) {
            return new YelpUser[size];
        }
    };
}
