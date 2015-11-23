package com.grelp.grelp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Merchant object retrieved from API merchant#show
 * https://api.groupon.com/v2/merchants/
 */
public class GrouponMerchant {
    private final String id;
    private final String uuid;
    private final String name;
    private final double lat;
    private final double lng;
    private final String merchantImageUrl;

    public GrouponMerchant(String id, String uuid, String name, double lat,
                           double lng, String merchantImageUrl) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.merchantImageUrl = merchantImageUrl;
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

    public String getUuid() {
        return uuid;
    }

    public String getMerchantImageUrl() {
        return merchantImageUrl;
    }

    public static GrouponMerchant fromJSONObject(JSONObject merchantObject) throws JSONException {
        String id = merchantObject.getString("id");
        String uuid = merchantObject.getString("uuid");
        String name = merchantObject.getString("name");
        JSONArray merchantImages = merchantObject.getJSONArray("pictures");
        String merchantImageUrl = null;
        if (merchantImages.length() > 0) {
            merchantImageUrl = merchantImages.getJSONObject(0).getString("url");
        }
        double lat = 0;
        double lng = 0;
        if (merchantObject.has("locations")) {
            JSONArray locations = merchantObject.getJSONArray("locations");
            if ( locations.length() > 0) {
                JSONObject merchantLocation = locations.getJSONObject(0);
                lat = merchantLocation.getDouble("lat");
                lng = merchantLocation.getDouble("lng");
            }
        }
        return new GrouponMerchant(id, uuid, name, lat, lng, merchantImageUrl);
    }
}
