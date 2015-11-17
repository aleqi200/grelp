package com.grelp.grelp.model;

public class Merchant {
    private final String id;
    private final String uuid;
    private final String name;
    private final String websiteUrl;

    public Merchant(String id, String uuid, String name, String websiteUrl) {
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
}
