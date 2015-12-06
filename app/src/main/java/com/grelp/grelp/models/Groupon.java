package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Groupon implements Parcelable {
    private final static NumberFormat formatter = new DecimalFormat("#0.0");

    private final String id;
    private final String uuid;
    private final String title;
    private final String announcementTitle;
    private final String shortAnnouncementTitle;
    private final String soldQuantity;
    private final String smallImageUrl;
    private final String grid4ImageUrl;
    private final GrouponDivision division;
    private final double distance;
    private final double lat;
    private final double lng;
    private final String minPrice;
    private final String minValue;
    private final GrouponDealMerchant merchant;
    private final List<GrouponOption> options;

    public Groupon(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.title = builder.title;
        this.announcementTitle = builder.announcementTitle;
        this.shortAnnouncementTitle = builder.shortAnnouncementTitle;
        this.soldQuantity = builder.soldQuantity;
        this.smallImageUrl = builder.smallImageUrl;
        this.grid4ImageUrl = builder.grid4ImageUrl;
        this.distance = builder.distance;
        this.division = builder.division;
        this.minPrice = builder.minPrice;
        this.minValue = builder.minValue;
        this.lat = builder.lat;
        this.lng = builder.lng;
        this.merchant = builder.merchant;
        this.options = builder.options;
    }

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public String getShortAnnouncementTitle() {
        return shortAnnouncementTitle;
    }

    public String getSoldQuantity() {
        return soldQuantity;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public String getGrid4ImageUrl() {
        return grid4ImageUrl;
    }

    public GrouponDivision getDivision() {
        return division;
    }

    public String getDistance() {
        return formatter.format(distance);
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMinValue() {
        return minValue;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public GrouponDealMerchant getMerchant() {
        return merchant;
    }

    public List<GrouponOption> getOptions() {
        return options;
    }

    public Collection<RedemptionLocation> getUniqueRedemptionLocations() {
        Set<RedemptionLocation> redemptionLocations = new HashSet<>();
        if (getOptions() != null && !getOptions().isEmpty()) {
            for (GrouponOption option : getOptions()) {
                redemptionLocations.addAll(option.getRedemptionLocations());
            }
        }
        return redemptionLocations;
    }

    public static Groupon fromJSONObject(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        String uuid = jsonObject.getString("uuid");
        String title = jsonObject.getString("title");
        String announcementTitle = jsonObject.getString("announcementTitle");
        String shortAnnouncementTitle = jsonObject.getString("shortAnnouncementTitle");
        String soldQuantity = jsonObject.getString("soldQuantityMessage");
        String smallImageUrl = jsonObject.getString("smallImageUrl");
        String grid4ImageUrl = jsonObject.getString("grid4ImageUrl");

        JSONObject divisionObject = jsonObject.getJSONObject("division");
        GrouponDivision division = GrouponDivision.fromJSONObject(divisionObject);

        JSONArray locations = jsonObject.getJSONArray("locations");
        JSONObject locationObject = locations.length() > 0 ? locations.getJSONObject(0) : null;
        double distance = locationObject != null ? locationObject.optDouble("distance", 32) : 32.0;

        JSONArray options = jsonObject.getJSONArray("options");
        double min = Double.MAX_VALUE;
        String minp = null, minv = null;
        double lat = 0.0, lng = 0.0;
        List<GrouponOption> optionList = new ArrayList<>(options.length());
        for (int i = 0; i < options.length(); i++) {
            JSONObject optionObject = options.getJSONObject(i);
            optionList.add(GrouponOption.fromJsonObject(optionObject));
            JSONObject priceObject = optionObject.getJSONObject("price");
            JSONObject valueObject = optionObject.getJSONObject("value");
            JSONArray redemptionLocations = optionObject.getJSONArray("redemptionLocations");
            lat = redemptionLocations.getJSONObject(0).getDouble("lat");
            lng = redemptionLocations.getJSONObject(0).getDouble("lng");
            if (priceObject.getDouble("amount") < min) {
                minp = priceObject.getString("formattedAmount");
                minv = valueObject.getString("formattedAmount");
            }
        }

        JSONObject merchantObject = jsonObject.getJSONObject("merchant");
        GrouponDealMerchant merchant = GrouponDealMerchant.fromJSONObject(merchantObject);

        Groupon groupon = new Groupon.Builder()
                .withId(id)
                .withUuid(uuid)
                .withAnnouncementTitle(announcementTitle)
                .withShortAnnouncementTitle(shortAnnouncementTitle)
                .withTitle(title)
                .withSoldQuantity(soldQuantity)
                .withSmallImageUrl(smallImageUrl)
                .withGrid4ImageUrl(grid4ImageUrl)
                .withDistance(distance)
                .withDivision(division)
                .withLat(lat)
                .withLng(lng)
                .withMerchant(merchant)
                .withMinPrice(minp)
                .withMinValue(minv)
                .withOptions(optionList)
                .build();
        return groupon;
    }

    public static ArrayList<Groupon> fromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Groupon> groupons = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Groupon groupon = fromJSONObject(jsonArray.getJSONObject(i));
            groupons.add(groupon);
        }
        return groupons;
    }

    public static class Builder {
        private String id;
        private String uuid;
        private String title;
        private String announcementTitle;
        private String shortAnnouncementTitle;
        private String soldQuantity;
        private String smallImageUrl;
        private String grid4ImageUrl;
        private GrouponDivision division;
        private double distance;
        private double lat;
        private double lng;
        private String minPrice;
        private String minValue;
        private GrouponDealMerchant merchant;
        private List<GrouponOption> options;

        public Groupon build() {
            return new Groupon(this);
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withAnnouncementTitle(String announcementTitle) {
            this.announcementTitle = announcementTitle;
            return this;
        }

        public Builder withSoldQuantity(String soldQuantity) {
            this.soldQuantity = soldQuantity;
            return this;
        }

        public Builder withSmallImageUrl(String smallImageUrl) {
            this.smallImageUrl = smallImageUrl;
            return this;
        }

        public Builder withGrid4ImageUrl(String grid4ImageUrl) {
            this.grid4ImageUrl = grid4ImageUrl;
            return this;
        }

        public Builder withDivision(GrouponDivision division) {
            this.division = division;
            return this;
        }

        public Builder withLat(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder withLng(double lng) {
            this.lng = lng;
            return this;
        }

        public Builder withDistance(double distance) {
            this.distance = distance;
            return this;
        }

        public Builder withMinPrice(String minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder withMinValue(String minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder withMerchant(GrouponDealMerchant merchant) {
            this.merchant = merchant;
            return this;
        }

        public Builder withOptions(List<GrouponOption> options) {
            this.options = options;
            return this;
        }

        public Builder withShortAnnouncementTitle(String shortAnnouncementTitle) {
            this.shortAnnouncementTitle = shortAnnouncementTitle;
            return this;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uuid);
        dest.writeString(this.title);
        dest.writeString(this.announcementTitle);
        dest.writeString(this.shortAnnouncementTitle);
        dest.writeString(this.soldQuantity);
        dest.writeString(this.smallImageUrl);
        dest.writeString(this.grid4ImageUrl);
        dest.writeParcelable(this.division, 0);
        dest.writeDouble(this.distance);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.minPrice);
        dest.writeString(this.minValue);
        dest.writeParcelable(this.merchant, 0);
        dest.writeTypedList(this.options);
    }

    private Groupon(Parcel in) {
        this.id = in.readString();
        this.uuid = in.readString();
        this.title = in.readString();
        this.announcementTitle = in.readString();
        this.shortAnnouncementTitle = in.readString();
        this.soldQuantity = in.readString();
        this.smallImageUrl = in.readString();
        this.grid4ImageUrl = in.readString();
        this.division = in.readParcelable(GrouponDivision.class.getClassLoader());
        this.distance = in.readDouble();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.minPrice = in.readString();
        this.minValue = in.readString();
        this.merchant = in.readParcelable(GrouponDealMerchant.class.getClassLoader());
        this.options = in.createTypedArrayList(GrouponOption.CREATOR);
    }

    public static final Creator<Groupon> CREATOR = new Creator<Groupon>() {
        public Groupon createFromParcel(Parcel source) {
            return new Groupon(source);
        }

        public Groupon[] newArray(int size) {
            return new Groupon[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Groupon groupon = (Groupon) o;

        return !(uuid != null ? !uuid.equals(groupon.uuid) : groupon.uuid != null);

    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
