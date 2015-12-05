package com.grelp.grelp.fragments;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.grelp.grelp.R;
import com.grelp.grelp.adapters.GrouponArrayAdapter;
import com.grelp.grelp.listeners.EndlessRecyclerOnScrollListener;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.data.GrouponClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DealListFragment extends Fragment {
    private static final String LOG_TAG = "DealList";

    private Location location;

    private RecyclerView rvGroupons;
    private GrouponArrayAdapter grouponAdapter;
    private GrouponClient grouponClient;
    private LinearLayoutManager mLayoutManager;


    public static DealListFragment newInstance(Location location) {
        DealListFragment fragment = new DealListFragment();
        Bundle args = new Bundle();
        args.putParcelable("location", location);
        fragment.setArguments(args);
        return fragment;
    }

    public DealListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getParcelable("location");
        }

        grouponClient = GrouponClient.getInstance();
        getGroupons(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_deal_list, container, false);

        rvGroupons = (RecyclerView) v.findViewById(R.id.lvGroupons);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvGroupons.setLayoutManager(mLayoutManager);
        grouponAdapter = new GrouponArrayAdapter(getContext());
        rvGroupons.setAdapter(grouponAdapter);
        rvGroupons.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public boolean onLoadMore(int offset) {
                getGroupons(offset);
                return true;
            }
        });

        return v;
    }

    public void getGroupons(int offset) {
        Log.d(LOG_TAG, "getGroupons(offset = " + offset + ")");

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Network not available", Toast.LENGTH_LONG).show();
            return;
        }

        grouponClient.getDeals(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    JSONArray dealsArray = response.getJSONArray("deals");
                    grouponAdapter.addAll(Groupon.fromJSONArray(dealsArray));
                    grouponAdapter.notifyDataSetChanged();
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
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
