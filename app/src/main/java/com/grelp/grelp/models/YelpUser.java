package com.grelp.grelp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class YelpUser {
    private final String id;
    private final String profileUrl;
    private final String name;

    public YelpUser(String id, String profileUrl, String name) {
        this.id = id;
        this.profileUrl = profileUrl;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getName() {
        return name;
    }

    public static YelpUser fromJSONObject(JSONObject userObject) throws JSONException {
        String id = userObject.getString("id");
        String profileUrl = userObject.getString("image_url");
        String name = userObject.getString("name");
        return new YelpUser(id, profileUrl, name);
    }
}
