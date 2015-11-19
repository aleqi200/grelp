package com.grelp.grelp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//TODO: Incorporate deal options into the model
public class Groupon {
    private final String id;
    private final String uuid;
    private final String title;
    private final String announcementTitle;
    private final String soldQuantity;
    private final String grid4ImageUrl;
    private final String division;
    private final double distance;
    private final double lat;
    private final double lng;
    private final String minPrice;
    private final String minValue;

    public Groupon(String id, String uuid, String title, String announcementTitle,
                   String soldQuantity, String grid4ImageUrl, String division, double distance,
                   String minPrice, String minValue, double lat, double lng) {
        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.announcementTitle = announcementTitle;
        this.soldQuantity = soldQuantity;
        this.grid4ImageUrl = grid4ImageUrl;
        this.distance = distance;
        this.division = division;
        this.minPrice = minPrice;
        this.minValue = minValue;
        this.lat = lat;
        this.lng = lng;
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

    public String getSoldQuantity() {
        return soldQuantity;
    }

    public String getGrid4ImageUrl() {
        return grid4ImageUrl;
    }

    public String getDivision() {
        return division;
    }

    public double getDistance() {
        return distance;
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

    public static Groupon fromJSONObject(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        String uuid = jsonObject.getString("uuid");
        String title = jsonObject.getString("title");
        String announcementTitle = jsonObject.getString("announcementTitle");
        String soldQuantity = jsonObject.getString("soldQuantityMessage");
        String grid4ImageUrl = jsonObject.getString("grid4ImageUrl");

        JSONObject divisionObject = jsonObject.getJSONObject("division");
        String division = divisionObject.getString("name");

        JSONArray locations = jsonObject.getJSONArray("locations");
        JSONObject locationObject = locations.length() > 0 ? locations.getJSONObject(0) : null;
        double distance =  locationObject != null ? locationObject.optDouble("distance", 32) : 32.0;

        JSONArray options = jsonObject.getJSONArray("options");
        double min = Double.MAX_VALUE;
        String minp = null,minv = null;
        double lat = 0.0, lng = 0.0;
        for (int i = 0; i < options.length(); i++) {
            JSONObject optionObject = options.getJSONObject(i);
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

        Groupon groupon = new Groupon(id, uuid, title, announcementTitle, soldQuantity,
                                      grid4ImageUrl, division, distance, minp, minv, lat, lng);
        return groupon;
    }

    public static List<Groupon> fromJSONArray(JSONArray jsonArray) throws JSONException {
        List<Groupon> groupons = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Groupon groupon = fromJSONObject(jsonArray.getJSONObject(i));
            groupons.add(groupon);
        }
        return groupons;
    }
}
