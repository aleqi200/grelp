package com.grelp.grelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YelpBusiness implements Parcelable {

    private final String id;
    private final String imageUrl;
    private final String ratingImgUrl;
    private final String name;
    private final int reviewCount;
    private final String phone;
    private final double rating;
    private final List<String> categories;
    private final List<YelpReview> yelpReviewList;

    public YelpBusiness(String id, String imageUrl, String ratingImgUrl,
                        String name, String phone, int reviewCount, double rating,
                        List<YelpReview> yelpReviewList, List<String> categories) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.ratingImgUrl = ratingImgUrl;
        this.name = name;
        this.phone = phone;
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.yelpReviewList = yelpReviewList;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRatingImgUrl() {
        return ratingImgUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
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

    public List<String> getCategories() {
        return categories;
    }

    public static YelpBusiness fromJSONObject(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        String imageUrl = jsonObject.getString("image_url");
        String ratingImgUrl = jsonObject.getString("rating_img_url");
        String name = jsonObject.getString("name");
        String phone = jsonObject.getString("display_phone");
        if (phone == null) {
            phone = jsonObject.getString("phone");
        }
        int reviewCount = jsonObject.getInt("review_count");
        double rating = jsonObject.getDouble("rating");
        JSONArray reviewArray = jsonObject.getJSONArray("reviews");
        List<YelpReview> reviews = YelpReview.fromJSONArray(reviewArray);
        JSONArray categoriesArray = jsonObject.getJSONArray("categories");
        List<String> categories = new ArrayList<>(categoriesArray.length());
        for (int i = 0; i < categoriesArray.length(); i++) {
            JSONArray categoryPair = categoriesArray.getJSONArray(i);
            if (categoryPair.length() > 1) {
                categories.add(categoryPair.getString(0));
            }
        }
        return new YelpBusiness(id, imageUrl, ratingImgUrl, name, phone, reviewCount, rating, reviews, categories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.imageUrl);
        dest.writeString(this.ratingImgUrl);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeInt(this.reviewCount);
        dest.writeDouble(this.rating);
        dest.writeStringList(this.categories);
        dest.writeTypedList(yelpReviewList);
    }

    protected YelpBusiness(Parcel in) {
        this.id = in.readString();
        this.imageUrl = in.readString();
        this.ratingImgUrl = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.reviewCount = in.readInt();
        this.rating = in.readDouble();
        this.categories = in.createStringArrayList();
        this.yelpReviewList = in.createTypedArrayList(YelpReview.CREATOR);
    }

    public static final Creator<YelpBusiness> CREATOR = new Creator<YelpBusiness>() {
        public YelpBusiness createFromParcel(Parcel source) {
            return new YelpBusiness(source);
        }

        public YelpBusiness[] newArray(int size) {
            return new YelpBusiness[size];
        }
    };
}
