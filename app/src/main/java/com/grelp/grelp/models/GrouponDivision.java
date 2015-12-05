package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class GrouponDivision implements Parcelable {
    private String id;
    private String name;
    private double lat;
    private double lng;

    private GrouponDivision(Builder builder) {
        this.id = builder.id;
        this.lat = builder.lat;
        this.lng = builder.lng;
        this.name = builder.name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    /*
        id: "san-jose",
        name: "San Jose",
        timezone: "Pacific Standard Time",
        timezoneOffsetInSeconds: -28800,
        timezoneIdentifier: "America/Los_Angeles",
        lat: 37.3394,
        lng: -121.895
     *
     */
    public static GrouponDivision fromJSONObject(JSONObject jsonObject) throws JSONException {
        return new Builder().setId(jsonObject.getString("id"))
                .setLat(jsonObject.getDouble("lat"))
                .setLng(jsonObject.getDouble("lng"))
                .setName(jsonObject.getString("name"))
                .build();
    }

    public static class Builder {
        private String id;
        private double lat;
        private double lng;
        private String name;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLng(double lng) {
            this.lng = lng;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public GrouponDivision build() {
            return new GrouponDivision(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }

    protected GrouponDivision(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public static final Parcelable.Creator<GrouponDivision> CREATOR = new Parcelable.Creator<GrouponDivision>() {
        public GrouponDivision createFromParcel(Parcel source) {
            return new GrouponDivision(source);
        }

        public GrouponDivision[] newArray(int size) {
            return new GrouponDivision[size];
        }
    };
}
