package com.grelp.grelp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.grelp.grelp.R;
import com.grelp.grelp.adapters.GrouponArrayAdapter;
import com.grelp.grelp.data.GrouponClient;
import com.grelp.grelp.listeners.EndlessRecyclerOnScrollListener;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.util.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DealListFragment extends Fragment {
    private static final String LOG_TAG = "DealList";

    private LatLng latLng;
    private RecyclerView rvGroupons;
    private GrouponArrayAdapter grouponAdapter;
    private GrouponClient grouponClient;
    private LinearLayoutManager mLayoutManager;
    private AlertDialog dialog;
    private String query = null;

    public static DealListFragment newInstance() {
        DealListFragment fragment = new DealListFragment();
        return fragment;
    }

    public DealListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        grouponClient = GrouponClient.getInstance();
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
                getGroupons(offset, query);
                return true;
            }
        });
        dialog = new SpotsDialog(getContext());
        dialog.show();

        return v;
    }

    public void setLocation(LatLng latLng) {
        this.latLng = latLng;
        getGroupons(0, query);
    }

    public void getGroupons(int offset, String query) {
        Log.d(LOG_TAG, "getGroupons(offset = " + offset + ")");

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_LONG).show();
            return;
        }
        if (offset == 0 && grouponAdapter.getItemCount() > 0) {
            return;
        }

        grouponClient.getGroupons(query, latLng, offset, new GrouponClient.GrouponListener() {
            @Override
            public void handleGroupons(ArrayList<Groupon> groupons) {
                if (groupons != null) {
                    grouponAdapter.addAll(groupons);
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groupon_fragment_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DealListFragment.this.query = query;
                grouponAdapter.clear();
                getGroupons(0, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
}
