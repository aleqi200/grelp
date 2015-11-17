package com.grelp.grelp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.grelp.grelp.adapter.GrouponAdapter;
import com.grelp.grelp.listener.InfiniteScrollListener;
import com.grelp.grelp.model.Groupon;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GrouponActivity extends AppCompatActivity {
    private static final String GROUPON_API = "http://api.groupon.com/v2/deals/search";
    private static final String LOG_TAG = "GrouponActivity";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:DDZ");
    private ListView lvGroupons;
    private GrouponAdapter grouponAdapter;
    private List<Groupon> groupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupon);
        groupons = new LinkedList<>();
        lvGroupons = (ListView) findViewById(R.id.lvGroupons);
        grouponAdapter = new GrouponAdapter(this, groupons);
        lvGroupons.setAdapter(grouponAdapter);
        lvGroupons.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getGroupons(page);
                return true;
            }
        });
        getGroupons(0);
    }

    public void getGroupons(int offset) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG);
            return;
        }
        Location location = getLocation();
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        String dateTime = dateFormat.format(new Date());
        requestParams.add("context", "mobile_local");
        requestParams.add("datetime", dateTime);
        requestParams.add("client_id", "d62dac53d0601f8c740dcc12613396b38a1a5f8a");
        requestParams.add("consumer_id", "5b2a214e-5c83-11e3-bbd1-0025906127f6");
        if (location != null) {
            requestParams.add("lat", "" + location.getLatitude());
            requestParams.add("lng", "" + location.getLongitude());
        } else {
            requestParams.add("filter", "division:san-francisco");
        }
        requestParams.add("max_results", "" + 200);
        requestParams.add("filter_start_time", dateTime);
        requestParams.add("filter_end_time", dateTime);
        requestParams.add("show", "default,locations");
        requestParams.add("offset", "" + offset);

        httpClient.get(GROUPON_API, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    JSONArray dealsArray = response.getJSONArray("deals");
                    grouponAdapter.addAll(Groupon.fromJSONArray(dealsArray));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error while parsing json object: " + response, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject response) {
                Log.e(LOG_TAG, "Error while retrieving groupons from: " + GROUPON_API, throwable);
            }
        });
    }

    //Check to see if network is available before making external service calls
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private Location getLocation() {
        /*LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location;*/
        return null;
    }
}
