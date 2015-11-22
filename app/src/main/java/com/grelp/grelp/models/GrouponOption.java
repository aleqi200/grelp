package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by acampelo on 11/21/15.
 */
public class GrouponOption implements Parcelable {
    private UUID id;
    private String title;
    private String price;
    private List<RedemptionLocation> redemptionLocations;

    public GrouponOption(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.price = builder.price;
        this.redemptionLocations = builder.redemptionLocations;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public List<RedemptionLocation> getRedemptionLocations() {
        return redemptionLocations;
    }

    public static class Builder {

        private UUID id;
        private String title;
        private String price;
        private List<RedemptionLocation> redemptionLocations;

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder setRedemptionLocations(List<RedemptionLocation> redemptionLocations) {
            this.redemptionLocations = redemptionLocations;
            return this;
        }

        public GrouponOption build() {
            return new GrouponOption(this);
        }
    }


    public static GrouponOption fromJsonObject(JSONObject jsonObject) throws JSONException {
        Builder builder = new Builder()
                .setId(UUID.fromString(jsonObject.getString("uuid")))
                .setTitle(jsonObject.getString("title"));
        builder.setPrice(jsonObject.getJSONObject("price").getString("formattedAmount"));
        JSONArray redemptionLocations = jsonObject.getJSONArray("redemptionLocations");
        List<RedemptionLocation> locationList = new ArrayList<>();
        for (int i = 0; i < redemptionLocations.length(); i++) {
            JSONObject locationObject = redemptionLocations.getJSONObject(i);
            locationList.add(RedemptionLocation.fromJsonObject(locationObject));
        }
        builder.setRedemptionLocations(locationList);
        return builder.build();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.title);
        dest.writeString(this.price);
        dest.writeTypedList(redemptionLocations);
    }

    protected GrouponOption(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.title = in.readString();
        this.price = in.readString();
        this.redemptionLocations = in.createTypedArrayList(RedemptionLocation.CREATOR);
    }

    public static final Parcelable.Creator<GrouponOption> CREATOR = new Parcelable.Creator<GrouponOption>() {
        public GrouponOption createFromParcel(Parcel source) {
            return new GrouponOption(source);
        }

        public GrouponOption[] newArray(int size) {
            return new GrouponOption[size];
        }
    };
}
