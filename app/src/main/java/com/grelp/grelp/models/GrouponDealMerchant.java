package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Light-weight representation of merchant from GAPI deal#search
 * http://api.groupon.com/v2/deals/search
 * {
 *   "id": "a-touch-of-class-3",
 *   "uuid": "75bb8522-587f-f2a8-05de-361862fab397",
 *   "name": "A Touch of Class",
 *   "websiteUrl": "http:\/\/touchofclass1989.com\/index.html",
 *   "facebookUrl": null,
 *   "twitterUrl": null,
 *   "ratings": null
 * }
 */
public class GrouponDealMerchant implements Parcelable {
    private String id;
    private String uuid;
    private String name;
    private String websiteUrl;

    public GrouponDealMerchant(String id, String uuid, String name, String websiteUrl) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.websiteUrl = websiteUrl;
    }

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public static GrouponDealMerchant fromJSONObject(JSONObject merchantObject) throws JSONException {
        String merchantId = merchantObject.getString("id");
        String merchantUuid = merchantObject.getString("uuid");
        String merchantWebsiteUrl = merchantObject.getString("websiteUrl");
        String merchantName = merchantObject.getString("name");
        GrouponDealMerchant merchant = new GrouponDealMerchant(merchantId, merchantUuid, merchantName,
                merchantWebsiteUrl);
        return merchant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uuid);
        dest.writeString(this.name);
        dest.writeString(this.websiteUrl);
    }

    private GrouponDealMerchant(Parcel in) {
        this.id = in.readString();
        this.uuid = in.readString();
        this.name = in.readString();
        this.websiteUrl = in.readString();
    }

    public static final Creator<GrouponDealMerchant> CREATOR = new Creator<GrouponDealMerchant>() {
        public GrouponDealMerchant createFromParcel(Parcel source) {
            return new GrouponDealMerchant(source);
        }

        public GrouponDealMerchant[] newArray(int size) {
            return new GrouponDealMerchant[size];
        }
    };
}
