package com.grelp.grelp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.grelp.grelp.R;
import com.grelp.grelp.fragments.DealListFragment;
import com.grelp.grelp.fragments.DealMapFragment;
import com.grelp.grelp.util.NetworkUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;

    private DealListFragment dealListFragment;
    private DealMapFragment dealMapFragment;

    private GoogleApiClient mGoogleApiClient;
    private static final long UPDATE_INTERVAL = 1000 * 30;  /* 30 secs */
    private static final long FASTEST_INTERVAL = 5000; /* 5 secs */
    private static final long MIN_DISTANCE = 200; /* 200m */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        connectClient();

        showDealList();
    }

    protected void connectClient() {
        // Connect the client.
        if (NetworkUtil.isGooglePlayServicesAvailable(this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST) && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_list_view) {
            showDealList();
        }
        if (id == R.id.action_show_map) {
            showMap();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission() {
        Log.d(LOG_TAG, "Checking location permission...");
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        Log.d(LOG_TAG, "Requesting location permission...");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d(LOG_TAG, "GPS permission allows us to access location data. " +
                    "Please allow in App Settings for additional functionality.");
        } else {
            Log.d(LOG_TAG, "Requesting location permission 2...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    private void showDealList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
        if (dealListFragment == null) {
            dealListFragment = DealListFragment.newInstance();
        }
        transaction.replace(R.id.container, dealListFragment);
        transaction.commit();
        fragmentManager.executePendingTransactions();

        if (mGoogleApiClient.isConnected()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                dealListFragment.setLocation(latLng);
            }
        }
    }

    private void showMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
        if (dealMapFragment == null) {
            dealMapFragment = DealMapFragment.newInstance();
        }
        transaction.replace(R.id.container, dealMapFragment);
        transaction.commit();
        fragmentManager.executePendingTransactions();

        if (mGoogleApiClient.isConnected()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                dealMapFragment.updateLocation(latLng);
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format));
            return rootView;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(getCurrentFocus(), "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class)); // refresh
                } else {
                    Snackbar.make(getCurrentFocus(), "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
        connectClient();
    }

    public void onStop() {
        Log.d(LOG_TAG, "onStop");
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
                 * If the result code is Activity.RESULT_OK, try to connect again, or else use the
                 * default location
                 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                    default:
                        dealListFragment.setLocation(NetworkUtil.DEFAULT_LOCATION);
                }

        }
    }

    /*
    * Called by Location Services when the request to connect the client
    * finishes successfully. At this point, you can request the current
    * location or start periodic updates
    */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //LocationServices.FusedLocationApi.flushLocations(mGoogleApiClient);
        if (!checkPermission()) {
            requestPermission();
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (dealMapFragment != null && dealMapFragment.getActivity() != null) {
                dealMapFragment.updateLocation(latLng);
            }
        if (dealListFragment != null && dealListFragment.getActivity() != null) {
                dealListFragment.setLocation(latLng);
            }
        }

        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        Log.d(LOG_TAG, "Starting location updates");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.d(LOG_TAG, msg);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(dealMapFragment != null && dealMapFragment.getActivity() != null) {
            dealMapFragment.updateLocation(latLng);
        }
        if(dealListFragment != null && dealListFragment.getActivity() != null) {
            dealListFragment.setLocation(latLng);
        }
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
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
                connectionResult.startResolutionForResult(this,
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
            Toast.makeText(this, "Sorry. Location services are not available", Toast.LENGTH_LONG).show();
        }
    }


}
