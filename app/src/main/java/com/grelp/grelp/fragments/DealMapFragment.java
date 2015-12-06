package com.grelp.grelp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grelp.grelp.R;
import com.grelp.grelp.activities.GrouponDetailActivity;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.data.GrouponClient;
import com.grelp.grelp.util.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DealMapFragment extends Fragment implements GrouponClient.GrouponListener
{
    private final static String LOG_TAG = "DealMap";

    private ArrayList<Groupon> groupons = new ArrayList<>();
    private GoogleMap map;
    private Marker currentlyClickedMarker;
    private LatLng latLng = NetworkUtil.DEFAULT_LOCATION;

    private GrouponClient grouponClient = GrouponClient.getInstance();

    public DealMapFragment() {
    }

    public static DealMapFragment newInstance() {
        DealMapFragment fragment = new DealMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                loadMap(map);
            }
        });

        getChildFragmentManager().beginTransaction().add(R.id.mpDeals, mapFragment).commit();
        getChildFragmentManager().executePendingTransactions();
        return view;
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity().getLayoutInflater()));
            map.setOnInfoWindowClickListener(new OnItemClick());
            map.setMyLocationEnabled(true);

            // Map is ready
            //Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            grouponClient.getGroupons(latLng, 0, this);
        } else {
            Log.e(LOG_TAG, "Error - Map was null!!");
        }
    }

    private void createMarker(int i, final Groupon groupon) {
        //add marker to Map
        LatLng point = new LatLng(groupon.getLat(), groupon.getLng());

        Marker marker = map.addMarker(new MarkerOptions()
                .position(point)
                .title(groupon.getTitle())
                .snippet("" + i) // store the index in the array
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_groupon_round_logo)));

        dropPinEffect(marker);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void handleGroupons(ArrayList<Groupon> groupons) {
        this.groupons = groupons;
        map.clear();

        int i = 0;

        if(groupons != null && !groupons.isEmpty()) {
            for (Groupon groupon : groupons) {
                createMarker(i, groupon);
                i++;
            }

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(groupons.get(0).getLat(), groupons.get(0).getLng()), 5);
            map.animateCamera(cameraUpdate);
        }
    }

    public void updateLocation(LatLng latLng) {
        this.latLng = latLng;
        if(map != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
            map.animateCamera(cameraUpdate);
            grouponClient.getGroupons(latLng, 0, this);
        }
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        LayoutInflater inflater = null;

        public CustomInfoWindowAdapter(LayoutInflater inflater) {
            //Inflating the InfoWindow view.
            this.inflater = inflater;
            view = inflater.inflate(R.layout.map_deal_item, null);
        }

        @Override
        public View getInfoContents(final Marker marker) {
            //Re-show InfoWindow if it already shown
            if (currentlyClickedMarker != null && currentlyClickedMarker.isInfoWindowShown() ) {
                currentlyClickedMarker.hideInfoWindow();
                currentlyClickedMarker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            currentlyClickedMarker = marker;
            TextView venueName = (TextView) view.findViewById(R.id.tvMapDealTitle);
            TextView venueAddress = (TextView) view.findViewById(R.id.tvMapDistance);
            ImageView venueLogo = (ImageView) view.findViewById(R.id.ivMapDealImage);
            //Get the Image from web using Picasso and update the info contents
            Groupon groupon = groupons.get(Integer.parseInt(marker.getSnippet()));
            Picasso.with(getActivity()).load(groupon.getGrid4ImageUrl()).into(venueLogo, new Callback() {
                @Override
                public void onSuccess() {
                    getInfoContents(marker);
                }

                @Override
                public void onError() {
                }
            });
            venueName.setText(groupon.getShortAnnouncementTitle());
            venueAddress.setText(groupon.getDistance() + " mi");
            return view;
        }
    }

    private class OnItemClick implements GoogleMap.OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            Groupon groupon = groupons.get(Integer.parseInt(marker.getSnippet()));
            Intent grpnDetailIntent = new Intent(getContext(), GrouponDetailActivity.class);
            grpnDetailIntent.putExtra("groupon", groupon);
            getContext().startActivity(grpnDetailIntent);
        }
    }
}
