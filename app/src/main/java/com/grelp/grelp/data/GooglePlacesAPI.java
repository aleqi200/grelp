package com.grelp.grelp.data;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class GooglePlacesAPI implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private boolean debug = false;

    public GooglePlacesAPI(Context context) {
        this.mContext = context;

        // Now that map has loaded, let's get our location!
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(debug) {
            Toast.makeText(mContext, "GoogleApiClient connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(debug) {
            Toast.makeText(mContext, "GoogleApiClient connection suspended", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(debug) {
            Toast.makeText(mContext, "GoogleApiClient connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void findPlace(double lat, double lng, String name, final PlaceListener placeListener) {
        LatLngBounds bounds = LatLngBounds.builder().include(new LatLng(lat, lng)).build();

        PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, name, bounds, null);

        result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
            @Override
            public void onResult(AutocompletePredictionBuffer autocompletePredictions) {
                if(autocompletePredictions.getStatus().isSuccess() && autocompletePredictions.getCount() > 0) {
                    String placeId = autocompletePredictions.get(0).getPlaceId();

                    PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);

                    placeBufferPendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if(places.getStatus().isSuccess()) {
                                Place place = places.get(0);
                                Log.d("GOOGLE_PLACES_API", "Found place: " + place);
                                placeListener.foundPlace(place);
                            } else {
                                if(debug) {
                                    Toast.makeText(mContext, "failed to get place: " +
                                            places.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
                                }
                                placeListener.foundPlace(null);
                            }

                            places.release();
                        }
                    });
                } else {
                    if(debug) {
                        Toast.makeText(mContext, "failed to get autocomplete predictions: " +
                                autocompletePredictions.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                    placeListener.foundPlace(null);
                }
                autocompletePredictions.release();
            }
        });
    }

    public interface PlaceListener {
        void foundPlace(Place place);
    }
}
