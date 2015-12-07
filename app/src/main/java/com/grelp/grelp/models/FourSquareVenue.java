package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class FourSquareVenue implements Parcelable {
    private String id;
    private String name;
    private String fourSquareUrl;
    private int likes;
    private int rating;
    private int ratingCount;
    private List<FourSquareTip> tips;
    private List<FourSquarePhrase> phrases;

    public FourSquareVenue(String id, String name, String fourSquareUrl, int likes, int rating,
                           int ratingCount, List<FourSquareTip> tips, List<FourSquarePhrase> phrases) {
        this.id = id;
        this.name = name;
        this.fourSquareUrl = fourSquareUrl;
        this.likes = likes;
        this.rating = rating;
        this.tips = tips;
        this.ratingCount = ratingCount;
        this.phrases = phrases;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFourSquareUrl() {
        return fourSquareUrl;
    }

    public int getLikes() {
        return likes;
    }

    public String getRating() {
        return rating + "";
    }

    public String getRatingCount() {
        return "Based on " + ratingCount + " ratings";
    }

    public List<FourSquareTip> getTips() {
        return tips;
    }

    public List<FourSquarePhrase> getPhrases() {
        return phrases;
    }

    public static FourSquareVenue fromJSONObject(JSONObject venue) throws JSONException {
        String id = venue.getString("id");
        String name = venue.getString("name");
        int rating = venue.optInt("rating", 0);
        int ratingCount = venue.optInt("ratingSignals", 0);
        int likes = venue.getJSONObject("likes").getInt("count");
        List<FourSquareTip> tips = FourSquareTip.fromJSONArray(
                venue.getJSONObject("tips").getJSONArray("groups"));
        JSONArray phraseArray = venue.optJSONArray("phrases");
        List<FourSquarePhrase> phrases = new LinkedList<>();
        if (phraseArray != null) {
            for (int i = 0; i < phraseArray.length(); i++) {
                JSONObject phraseObject = phraseArray.getJSONObject(i);
                phrases.add(new FourSquarePhrase(phraseObject.getString("phrase"), phraseObject.getInt("count")));
            }
        }
        JSONObject photoObject = venue.getJSONObject("bestPhoto");
        String fourSquareUrl = photoObject.getString("prefix") + "300x200" + photoObject.getString("suffix");
        FourSquareVenue fourSquareVenue = new FourSquareVenue(id, name, fourSquareUrl,
                likes, rating, ratingCount, tips, phrases);
        return fourSquareVenue;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.fourSquareUrl);
        dest.writeInt(this.likes);
        dest.writeInt(this.rating);
        dest.writeInt(this.ratingCount);
        dest.writeTypedList(tips);
        dest.writeTypedList(phrases);
    }

    private FourSquareVenue(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.fourSquareUrl = in.readString();
        this.likes = in.readInt();
        this.rating = in.readInt();
        this.ratingCount = in.readInt();
        this.tips = in.createTypedArrayList(FourSquareTip.CREATOR);
        this.phrases = in.createTypedArrayList(FourSquarePhrase.CREATOR);
    }

    public static final Creator<FourSquareVenue> CREATOR = new Creator<FourSquareVenue>() {
        public FourSquareVenue createFromParcel(Parcel source) {
            return new FourSquareVenue(source);
        }

        public FourSquareVenue[] newArray(int size) {
            return new FourSquareVenue[size];
        }
    };
}
