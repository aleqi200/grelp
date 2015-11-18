package com.grelp.grelp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YelpReview {
    private final double rating;
    private final String excerpt;
    private final long timeCreated;
    private final String ratingImageUrl;
    private final YelpUser user;

    public YelpReview(double rating, String excerpt, long timeCreated, String ratingImageUrl,
                      YelpUser user) {
        this.rating = rating;
        this.excerpt = excerpt;
        this.timeCreated = timeCreated;
        this.ratingImageUrl = ratingImageUrl;
        this.user = user;
    }

    public double getRating() {
        return rating;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public YelpUser getUser() {
        return user;
    }

    public String getRatingImageUrl() {
        return ratingImageUrl;
    }

    public static YelpReview fromJSONObject(JSONObject review) throws JSONException {
        String excerpt = review.getString("excerpt");
        double rating = review.getDouble("rating");
        long timeCreated = review.getLong("time_created");
        String ratingImageUrl = review.getString("rating_img_url_small");
        JSONObject userObject = review.getJSONObject("user");
        YelpUser user = YelpUser.fromJSONObject(userObject);
        return new YelpReview(rating, excerpt, timeCreated, ratingImageUrl, user);
    }

    public static List<YelpReview> fromJSONArray(JSONArray reviewArray) throws JSONException {
        List<YelpReview> reviews = new ArrayList<>();
        for (int i = 0; i < reviewArray.length(); i++) {
            YelpReview review = fromJSONObject(reviewArray.getJSONObject(i));
            reviews.add(review);
        }
        return reviews;
    }
}
