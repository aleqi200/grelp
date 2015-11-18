package com.grelp.grelp.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.grelp.grelp.R;
import com.grelp.grelp.adapters.GrouponAdapter;
import com.grelp.grelp.listeners.InfiniteScrollListener;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.utility.GrouponClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.Header;

import java.util.LinkedList;
import java.util.List;

public class GrouponActivity extends AppCompatActivity {
    private static final String LOG_TAG = "GrouponActivity";

    private ListView lvGroupons;
    private GrouponAdapter grouponAdapter;
    private GrouponClient grouponClient;
    private List<Groupon> groupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupon);
        groupons = new LinkedList<>();
        grouponClient = GrouponClient.getInstance();
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
        lvGroupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent yelpIntent = new Intent(GrouponActivity.this, YelpActivity.class);
                yelpIntent.putExtra("businessId", "the-flying-falafel-san-francisco-3");
                startActivity(yelpIntent);
            }
        });
        getGroupons(0);
    }

    public void getGroupons(int offset) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG).show();
            return;
        }

        grouponClient.getDeals(new JsonHttpResponseHandler() {
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
                Log.e(LOG_TAG, "Error while retrieving groupons" + Log.getStackTraceString(throwable));
            }
        }, null, offset);
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
