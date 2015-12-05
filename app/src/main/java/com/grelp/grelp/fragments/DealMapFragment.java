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

public class DealMapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static LatLng DEFAULT_LOCATION = new LatLng(37.4292, -122.1381);
    private final static String LOG_TAG = "DealMap";

    private ArrayList<Groupon> groupons = new ArrayList<>();
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private static final long UPDATE_INTERVAL = 60000 * 60;  /* 60 secs */
    private static final long FASTEST_INTERVAL = 5000; /* 5 secs */
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean useGPSLocation = false;
    private Marker currentlyClickedMarker;

    public DealMapFragment() {
    }

    public static DealMapFragment newInstance(ArrayList<Groupon> groupons) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("groupons", groupons);
        DealMapFragment fragment = new DealMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupons = getArguments().getParcelableArrayList("groupons");
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
        addGroupons(this.groupons);
        return view;
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity().getLayoutInflater()));
            map.setOnInfoWindowClickListener(new OnItemClick());
            // Map is ready
            boolean locationPermissionGiven = checkPermission();
            if (!locationPermissionGiven) {
                requestPermission();
            }
            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(locationPermissionGiven);
            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
            int i = 0;
            for (Groupon groupon : groupons) {
                createMarker(i, groupon);
                i++;
            }
            if (!groupons.isEmpty()) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(groupons.get(0).getLat(), groupons.get(0).getLng()), 5);
                map.animateCamera(cameraUpdate);
            }
        } else {
            Log.e(LOG_TAG, "Error - Map was null!!");
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d(LOG_TAG, "GPS permission allows us to access location data. " +
                    "Please allow in App Settings for additional functionality.");
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    public void addGroupons(ArrayList<Groupon> newGroupons) {
        Log.d(LOG_TAG, "adding groupons to map: " + newGroupons.size());

        groupons.addAll(newGroupons);

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

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    public void onStart() {
        super.onStart();
        connectClient();
    }

    public void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Handle results returned to the FragmentActivity by Google Play services
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                /*
                 * If the result code is Activity.RESULT_OK, try to connect again
                 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getActivity().getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
    * Called by Location Services when the request to connect the client
    * finishes successfully. At this point, you can request the current
    * location or start periodic updates
    */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (!useGPSLocation) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 5);
            map.animateCamera(cameraUpdate);
            return;
        }
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            Toast.makeText(getActivity(), "Current location was null, enable GPS on emulator!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.d(LOG_TAG, msg);

    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
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
            if ( currentlyClickedMarker != null && currentlyClickedMarker.isInfoWindowShown() ) {
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
