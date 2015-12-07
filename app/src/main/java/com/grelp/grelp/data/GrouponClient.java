package com.grelp.grelp.data;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.grelp.grelp.models.Groupon;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GrouponClient {
    private static final String LOG_TAG = GrouponClient.class.getName();

    private static final String GROUPON_URL = "https://api.groupon.com";
    private static final String DEALS_SEARCH_URI = "v2/deals/search";
    private static final String MERCHANT_SHOW_URI = "v2/merchants";
    private static final String MERCHANT_SERVICE_URI = "merchantservice/v2.0/merchants";
    private static final String DEALS_SEARCH_CONTEXT = "mobile_local";
    private static final String GAPI_CLIENT_ID = "44aa9e633a98fd614f0e7f495e3e6442";
    private static final String CONSUMER_ID = "5b2a214e-5c83-11e3-bbd1-0025906127f6";
    private static final String MAX_RESULTS = "20";
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

    public void getDeals(AsyncHttpResponseHandler handler, LatLng location, int offset, String query) {
        RequestParams requestParams = new RequestParams();
        String dateTime = dateFormat.format(new Date());
        requestParams.add("datetime", dateTime);
        requestParams.add("context", DEALS_SEARCH_CONTEXT);
        requestParams.add("client_id", GAPI_CLIENT_ID);
        requestParams.add("consumer_id", CONSUMER_ID);
        if (query != null) {
            requestParams.add("query", query);
        }
        if (location != null) {
            requestParams.add("lat", "" + location.latitude);
            requestParams.add("lng", "" + location.longitude);
        } else {
            requestParams.add("filter", "division:san-francisco");
        }
        requestParams.add("limit", MAX_RESULTS);
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

    private void get(AsyncHttpResponseHandler handler, String apiUrl) {
        httpClient.get(apiUrl, handler);
    }

    private void get(AsyncHttpResponseHandler handler, String apiUrl, RequestParams params) {
        httpClient.get(apiUrl, params, handler);
    }

    public interface GrouponListener {
        void handleGroupons(ArrayList<Groupon> groupons);
    }

    public void getGroupons(String query, LatLng location, int offset,
                            final GrouponListener listener) {
        getDeals(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    JSONArray dealsArray = response.getJSONArray("deals");
                    ArrayList<Groupon> groupons = Groupon.fromJSONArray(dealsArray);
                    listener.handleGroupons(groupons);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error while parsing json object: " + response, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.e(LOG_TAG, "Error while retrieving groupons" + Log.getStackTraceString(throwable));
                listener.handleGroupons(null);
            }
        }, location, offset, query);
    }
}
