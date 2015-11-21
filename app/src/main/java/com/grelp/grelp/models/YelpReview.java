package com.grelp.grelp.models;

import com.grelp.grelp.util.PrettyTimePrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YelpReview {
    private final double rating;
    private final String excerpt;
    private final String timeCreated;
    private final String ratingImageUrl;
    private final YelpUser user;

    public YelpReview(double rating, String excerpt, String timeCreated, String ratingImageUrl,
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

    public String getTimeCreated() {
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
        String timeCreated = PrettyTimePrinter.getAbbreviatedTimeSpan(review.getLong("time_created"));
        String ratingImageUrl = review.getString("rating_image_small_url");
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
