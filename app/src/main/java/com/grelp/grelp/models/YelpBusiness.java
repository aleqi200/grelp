package com.grelp.grelp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class YelpBusiness {

    private final String imageUrl;
    private final String name;
    private final int reviewCount;
    private final double rating;
    private final List<YelpReview> yelpReviewList;

    public YelpBusiness(String imageUrl, String name, int reviewCount, double rating,
                        List<YelpReview> yelpReviewList) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.yelpReviewList = yelpReviewList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public double getRating() {
        return rating;
    }

    public List<YelpReview> getYelpReviewList() {
        return yelpReviewList;
    }

    public static YelpBusiness fromJSONObject(JSONObject jsonObject) throws JSONException {
        String imageUrl = jsonObject.getString("image_url");
        String name = jsonObject.getString("name");
        int reviewCount = jsonObject.getInt("review_count");
        double rating = jsonObject.getDouble("rating");
        JSONArray reviewArray = jsonObject.getJSONArray("reviews");
        List<YelpReview> reviews = YelpReview.fromJSONArray(reviewArray);
        return new YelpBusiness(imageUrl, name, reviewCount, rating, reviews);
    }
}
