package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class FourSquareTip implements Parcelable {
    private String text;
    private long timestamp;
    private int likes;
    private String photoUrl;

    public FourSquareTip(String text, long timestamp, int likes, String photoUrl) {
        this.text = text;
        this.timestamp = timestamp;
        this.likes = likes;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public static List<FourSquareTip> fromJSONObject(JSONObject tipObject) throws JSONException {
        JSONArray groups = tipObject.getJSONArray("groups");
        List<FourSquareTip> tips = new LinkedList<>();
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            JSONArray items = group.getJSONArray("items");
            for (int j = 0; j < items.length(); j++) {
                JSONObject item = items.getJSONObject(i);
                long timestamp = item.getLong("createdAt");
                String text = item.getString("text");
                int likes = item.getJSONObject("likes").getInt("count");
                JSONObject photo = item.getJSONObject("user").getJSONObject("photo");
                String photoUrl = null;
                if (photo != null) {
                    //Foursquare URLs need to be constructed from the prefix and suffix and come
                    //in two dimensions (30x30 or 110x110) which also needs to be added to the URI
                    //to retrieve the final photo
                    photoUrl = photo.getString("prefix") + "30x30" + photo.getString("suffix");
                }
                FourSquareTip tip = new FourSquareTip(text, timestamp, likes, photoUrl);
                tips.add(tip);
            }
        }
        return tips;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeLong(this.timestamp);
        dest.writeInt(this.likes);
        dest.writeString(this.photoUrl);
    }

    private FourSquareTip(Parcel in) {
        this.text = in.readString();
        this.timestamp = in.readLong();
        this.likes = in.readInt();
        this.photoUrl = in.readString();
    }

    public static final Creator<FourSquareTip> CREATOR = new Creator<FourSquareTip>() {
        public FourSquareTip createFromParcel(Parcel source) {
            return new FourSquareTip(source);
        }

        public FourSquareTip[] newArray(int size) {
            return new FourSquareTip[size];
        }
    };
}
