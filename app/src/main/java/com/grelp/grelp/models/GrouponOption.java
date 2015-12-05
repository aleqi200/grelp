package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrouponOption implements Parcelable {
    private UUID id;
    private String title;
    private String price;
    private String value;
    private String discountPercent;
    private String soldQuantityMessage;
    private List<RedemptionLocation> redemptionLocations;

    public GrouponOption(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.price = builder.price;
        this.value = builder.value;
        this.discountPercent = builder.discountPercent;
        this.soldQuantityMessage = builder.soldQuantityMessage;
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

    public String getValue() {
        return value;
    }

    public String getDiscountPercent() {
        return "Discount " + discountPercent + "%";
    }

    public String getSoldQuantityMessage() {
        return "Bought " + soldQuantityMessage;
    }

    public static class Builder {

        private UUID id;
        private String title;
        private String price;
        private String value;
        private String discountPercent;
        private String soldQuantityMessage;
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

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setSoldQuantityMessage(String soldQuantityMessage) {
            this.soldQuantityMessage = soldQuantityMessage;
            return this;
        }

        public Builder setDiscountPercent(String discountPercent) {
            this.discountPercent = discountPercent;
            return this;
        }

        public GrouponOption build() {
            return new GrouponOption(this);
        }
    }


    public static GrouponOption fromJsonObject(JSONObject jsonObject) throws JSONException {
        Builder builder = new Builder()
                .setId(UUID.fromString(jsonObject.getString("uuid")))
                .setTitle(jsonObject.getString("title"))
                .setSoldQuantityMessage(jsonObject.getString("soldQuantityMessage"))
                .setDiscountPercent(jsonObject.getString("discountPercent"));
        builder.setPrice(jsonObject.getJSONObject("price").getString("formattedAmount"));
        builder.setValue(jsonObject.getJSONObject("value").getString("formattedAmount"));
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
        dest.writeString(this.value);
        dest.writeString(this.discountPercent);
        dest.writeString(this.soldQuantityMessage);
        dest.writeTypedList(redemptionLocations);
    }

    private GrouponOption(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.title = in.readString();
        this.price = in.readString();
        this.value = in.readString();
        this.discountPercent = in.readString();
        this.soldQuantityMessage = in.readString();
        this.redemptionLocations = in.createTypedArrayList(RedemptionLocation.CREATOR);
    }

    public static final Creator<GrouponOption> CREATOR = new Creator<GrouponOption>() {
        public GrouponOption createFromParcel(Parcel source) {
            return new GrouponOption(source);
        }

        public GrouponOption[] newArray(int size) {
            return new GrouponOption[size];
        }
    };
}
