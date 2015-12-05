package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RedemptionLocation implements Parcelable {
    private UUID id;
    private Double lat;
    private Double lng;
    private String name;
    private String streetAddress1;
    private String streetAddress2;
    private String state;
    private String postalCode;

    private RedemptionLocation(Builder builder) {
        this.id = builder.id;
        this.lat = builder.lat;
        this.lng = builder.lng;
        this.name = builder.name;
        this.streetAddress1 = builder.streetAddress1;
        this.streetAddress2 = builder.streetAddress2;
        this.state = builder.state;
        this.postalCode = builder.postalCode;
    }

    public UUID getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedemptionLocation that = (RedemptionLocation) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class Builder {

        private UUID id;
        private Double lat;
        private Double lng;
        private String name;
        private String streetAddress1;
        private String streetAddress2;
        private String state;
        private String postalCode;

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLng(Double lng) {
            this.lng = lng;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setStreetAddress1(String streetAddress1) {
            this.streetAddress1 = streetAddress1;
            return this;
        }

        public Builder setStreetAddress2(String streetAddress2) {
            this.streetAddress2 = streetAddress2;
            return this;
        }

        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public RedemptionLocation build() {
            return new RedemptionLocation(this);
        }
    }

    /*
     name: "A Touch of Color Salon",
     streetAddress1: "871 Coleman Avenue",
     streetAddress2: "Suite 105",
     state: "CA",
     city: "San Jose",
     neighborhood: "Central San Jose",
     postalCode: "95110",
     country: "US",
     phoneNumber: "+14082072182",
     id: 60351730,
     uuid: "2c6507fd-758e-b06a-9053-014cba1875e8",
     ordering: 0,
     lat: 37.3454592,
     lng: -121.9149784
     */
    public static RedemptionLocation fromJsonObject(JSONObject jsonObject) throws JSONException {
        return new Builder().setId(UUID.fromString(jsonObject.getString("uuid")))
                .setLat(jsonObject.getDouble("lat"))
                .setLng(jsonObject.getDouble("lng"))
                .setName(jsonObject.getString("name"))
                .setStreetAddress1(jsonObject.getString("streetAddress1"))
                .setStreetAddress2(jsonObject.getString("streetAddress2"))
                .setState(jsonObject.getString("state"))
                .setPostalCode(jsonObject.getString("postalCode"))
                .build();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeString(this.name);
        dest.writeString(this.streetAddress1);
        dest.writeString(this.streetAddress2);
        dest.writeString(this.state);
        dest.writeString(this.postalCode);
    }

    protected RedemptionLocation(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.name = in.readString();
        this.streetAddress1 = in.readString();
        this.streetAddress2 = in.readString();
        this.state = in.readString();
        this.postalCode = in.readString();
    }

    public static final Parcelable.Creator<RedemptionLocation> CREATOR = new Parcelable.Creator<RedemptionLocation>() {
        public RedemptionLocation createFromParcel(Parcel source) {
            return new RedemptionLocation(source);
        }

        public RedemptionLocation[] newArray(int size) {
            return new RedemptionLocation[size];
        }
    };
}
