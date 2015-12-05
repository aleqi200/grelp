package com.grelp.grelp.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.oauth.OAuthService;

public class FourSquareClient {

    private static final String API_KEY = "OZJ2DF30CQNSNBMNY0IE3HRXRHPSIC1HRYMQ2WJLHLM13V5R";
    private static final String API_SECRET = "XA5VIFQ5MDCKDKKG2PCQ1EJEADLV1WUAEVH3XML4WE3G2GBQ";
    private static final String ACCESS_TOKEN = "FC2T5EFYE5YRXF44IEU31VNVGPUG4G5OF01X0VBSCBRPXWZC";
    private static final String VENUES_URL = "https://api.foursquare.com/v2/venues/search";
    private static final String CALLBACK_URL = "oauth://grelp";
    private static FourSquareClient fourSquareClient;
    private AsyncHttpClient client;
    private OAuthService service;

    private FourSquareClient() {
        client = new AsyncHttpClient();
        service = new ServiceBuilder()
                .provider(Foursquare2Api.class)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback("http://localhost:9000/")
                .build();
    }

    public static synchronized FourSquareClient getInstance() {
        if (fourSquareClient == null) {
            fourSquareClient = new FourSquareClient();
        }
        return fourSquareClient;
    }

    public void searchForVenues(String term, double lat, double lng, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.add("client_id", API_KEY);
        params.add("client_secret", API_SECRET);
        params.add("v", "20151205");
        params.add("ll", lat + "," + lng);
        params.add("query", term);
        params.add("oauth_token", ACCESS_TOKEN);
        get(handler, VENUES_URL, params);
    }

    public void searchForVenueById(String id, AsyncHttpResponseHandler handler) {
        String apiUrl = "https://api.foursquare.com/v2/venues/" + id;
        RequestParams params = new RequestParams();
        params.add("v", "20151204");
        params.add("oauth_token", ACCESS_TOKEN);
        get(handler, apiUrl, params);
    }

    public void get(AsyncHttpResponseHandler handler, String apiUrl, RequestParams params) {
        new AsyncHttpClient().get(apiUrl, params, handler);
    }

}
