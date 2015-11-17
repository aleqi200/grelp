package com.grelp.grelp.utility;

import android.location.Location;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GrouponClient {

    private static final String GROUPON_URL = "http://api.groupon.com";
    private static final String DEALS_SEARCH_URI = "v2/deals/search";
    private static final String MERCHANT_SHOW_URI = "v2/merchants";
    private static final String MERCHANT_SERVICE_URI = "merchantservice/v2.0/merchants";
    private static final String DEALS_SEARCH_CONTEXT = "mobile_local";
    private static final String GAPI_CLIENT_ID = "44aa9e633a98fd614f0e7f495e3e6442";
    private static final String CONSUMER_ID = "5b2a214e-5c83-11e3-bbd1-0025906127f6";
    private static final String MAX_RESULTS = "50";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:DDZ");

    private static GrouponClient client;

    private AsyncHttpClient httpClient;

    private GrouponClient() {
        httpClient = new AsyncHttpClient();
    }

    public static synchronized GrouponClient getInstance() {
        if (client == null) {
            client = new GrouponClient();
        }
        return client;
    }

    private String getApiUrl(String uri, String... resources) {
        StringBuffer buffer = new StringBuffer(uri);
        for (String resource : resources) {
            buffer.append("/");
            buffer.append(resource);
        }
        return buffer.toString();
    }

    public void getDeals(AsyncHttpResponseHandler handler, Location location, int offset) {
        RequestParams requestParams = new RequestParams();
        String dateTime = dateFormat.format(new Date());
        requestParams.add("datetime", dateTime);
        requestParams.add("context", DEALS_SEARCH_CONTEXT);
        requestParams.add("client_id", GAPI_CLIENT_ID);
        requestParams.add("consumer_id", CONSUMER_ID);
        if (location != null) {
            requestParams.add("lat", "" + location.getLatitude());
            requestParams.add("lng", "" + location.getLongitude());
        } else {
            requestParams.add("filter", "division:san-francisco");
        }
        requestParams.add("max_results", MAX_RESULTS);
        requestParams.add("filter_start_time", dateTime);
        requestParams.add("filter_end_time", dateTime);
        requestParams.add("show", "default,locations");
        requestParams.add("offset", "" + offset);

        get(handler, getApiUrl(GROUPON_URL, DEALS_SEARCH_URI), requestParams);
    }

    public void getMerchant(AsyncHttpResponseHandler handler, String merchantId) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("client_id", GAPI_CLIENT_ID);

        get(handler, getApiUrl(GROUPON_URL, MERCHANT_SHOW_URI, merchantId), requestParams);
    }

    public void getMerchantFromMerchantService(AsyncHttpResponseHandler handler, String merchantUuid) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("client_id", GAPI_CLIENT_ID);

        get(handler, getApiUrl(GROUPON_URL, MERCHANT_SERVICE_URI, merchantUuid), requestParams);
    }

    public void get(AsyncHttpResponseHandler handler, String apiUrl) {
        httpClient.get(apiUrl, handler);
    }

    public void get(AsyncHttpResponseHandler handler, String apiUrl, RequestParams params) {
        httpClient.get(apiUrl, params, handler);
    }
}
