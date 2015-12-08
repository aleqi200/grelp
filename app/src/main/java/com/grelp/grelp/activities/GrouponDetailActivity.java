package com.grelp.grelp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.grelp.grelp.R;
import com.grelp.grelp.data.GooglePlacesAPI;
import com.grelp.grelp.data.FourSquareClient;
import com.grelp.grelp.data.GrouponClient;
import com.grelp.grelp.data.YelpAPI;
import com.grelp.grelp.fragments.FourSquareDetailFragment;
import com.grelp.grelp.fragments.GooglePlacesFragment;
import com.grelp.grelp.fragments.NoDataFragment;
import com.grelp.grelp.fragments.YelpDetailFragment;
import com.grelp.grelp.fragments.YelpDetailFragmentMinimum;
import com.grelp.grelp.fragments.loading.FoursquareLoadingFragment;
import com.grelp.grelp.fragments.loading.GoogleLoadingFragment;
import com.grelp.grelp.fragments.loading.YelpLoadingFragment;
import com.grelp.grelp.models.FourSquareVenue;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.models.GrouponMerchant;
import com.grelp.grelp.models.GrouponOption;
import com.grelp.grelp.models.ParcelablePlace;
import com.grelp.grelp.models.RedemptionLocation;
import com.grelp.grelp.models.YelpBusiness;
import com.grelp.grelp.util.StringUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class GrouponDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GrouponDetail";
    private GrouponClient grouponClient;
    private FourSquareClient fourSquareClient;
    private GooglePlacesAPI googlePlacesAPI;

    private Groupon groupon;
    private GrouponMerchant merchant;
    private FourSquareVenue fourSquareVenue;

    private ImageView ivDetailedImage;
    private TextView tvDetailedTitle;
    private TextView tvMerchantName;
    private TextView tvLocationSelected;
    private TextView tvNumberOfLocations;
    private LinearLayout llDealOptions;
    private Button btnBuy;

    private ParcelablePlace place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupon_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupon = getIntent().getParcelableExtra("groupon");

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(null);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.yelp_fragment, YelpLoadingFragment.newInstance());
        transaction.replace(R.id.fs_fragment, FoursquareLoadingFragment.newInstance());
        transaction.replace(R.id.places_fragment, GoogleLoadingFragment.newInstance());
        transaction.commit();
        grouponClient = GrouponClient.getInstance();
        googlePlacesAPI = new GooglePlacesAPI(this);
        fourSquareClient = FourSquareClient.getInstance();
        setupViews();
        getGrouponMerchant();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        ivDetailedImage = (ImageView) findViewById(R.id.ivDetailedImage);
        tvDetailedTitle = (TextView) findViewById(R.id.tvDetailedTitle);
        tvMerchantName = (TextView) findViewById(R.id.tvMerchantName);
        tvLocationSelected = (TextView) findViewById(R.id.tvLocationSelected);
        tvNumberOfLocations = (TextView) findViewById(R.id.tvNumberOfLocations);
        llDealOptions = (LinearLayout) findViewById(R.id.llDealOptions);
        btnBuy = (Button) findViewById(R.id.btnBuy);

        //Setup groupon detailed view
        tvDetailedTitle.setText(groupon.getTitle());
        tvMerchantName.setText(groupon.getMerchant().getName());
        LinkedList<RedemptionLocation> locations = new LinkedList<>(groupon.getUniqueRedemptionLocations());
        RedemptionLocation firstRedemptionLocation = locations.getFirst();
        String streetPlusCity = StringUtil.join(", ", firstRedemptionLocation.getStreetAddress1(), firstRedemptionLocation.getStreetAddress2());
        String locationString = StringUtil.join(" ", streetPlusCity, firstRedemptionLocation.getState(), firstRedemptionLocation.getPostalCode());
//        if (locationString.length() > 32) {
//            locationString = locationString.substring(0, 32) + "...";
//        }
        tvLocationSelected.setText(locationString);
        Resources res = getResources();
        String locationsCount = res.getQuantityString(R.plurals.number_of_locations, locations.size(), locations.size());
        tvNumberOfLocations.setText(locationsCount);
        Picasso.with(GrouponDetailActivity.this).load(groupon.getGrid4ImageUrl()).into(ivDetailedImage);
        for (GrouponOption option : groupon.getOptions()) {
            View childView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_groupon_option, null);
            ((TextView) childView.findViewById(R.id.tvOptionTitle)).setText(option.getTitle());
            ((TextView) childView.findViewById(R.id.tvBoughtQuantity)).setText(option.getSoldQuantityMessage());
            ((TextView) childView.findViewById(R.id.tvOptionDiscount)).setText(option.getDiscountPercent());
            ((TextView) childView.findViewById(R.id.tvOptionPrice)).setText(option.getPrice());
            TextView tvOptionValue = (TextView) childView.findViewById(R.id.tvOptionValue);
            tvOptionValue.setText(option.getValue());
            tvOptionValue.setPaintFlags(tvOptionValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            llDealOptions.addView(childView);
        }

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriString = "groupon:///dispatch/us/deal/" + groupon.getId();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
            }
        });
    }

    private void getGrouponMerchant() {

        grouponClient.getMerchant(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    merchant = GrouponMerchant.fromJSONObject(response.getJSONObject("merchant"));
                    searchForMerchantOnYelp();
                    searchForMerchantOnGooglePlaces();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Error while parsing json object: " + errorResponse, throwable);
            }

        }, groupon.getMerchant().getId());
    }

    private void searchForMerchantOnGooglePlaces() {
        googlePlacesAPI.findPlace(merchant.getLat(), merchant.getLng(), merchant.getName(), new GooglePlacesAPI.PlaceListener() {

            @Override
            public void foundPlace(Place place) {
                if (place != null) {
                    Log.d("GOOGLE_PLACES_API", "Got place: " + place.getName() + ", rating: " + place.getRating());
                    GrouponDetailActivity.this.place = new ParcelablePlace(place);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
                    transaction.replace(R.id.places_fragment, GooglePlacesFragment.newInstance(place));
                    transaction.commit();
                } else {
                    noDataFor(R.id.places_fragment, "Google Places");
                    Log.d("GOOGLE_PLACES_API", "Failed to find place via google: " + merchant.getName());
                }
            }
        });
    }

    protected void noDataFor(int places_fragment, String service) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
        transaction.replace(places_fragment, NoDataFragment.newInstance(service));
        transaction.commit();
    }

    private void searchForMerchantOnYelp() {
        Pair<Double, Double> latlng = getBestLocation();
        double lat = latlng.first;
        double lng = latlng.second;

        YelpAPI.getInstance().searchForBusinesses(merchant.getName(), lat, lng,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray businesses = response.getJSONArray("businesses");
                            if (businesses.length() == 0) {
                                noDataFor(R.id.yelp_fragment, "Yelp");
                                noDataFor(R.id.fs_fragment, "Four Square");
                                return;
                            }
                            JSONObject businessObject = businesses.getJSONObject(0);
                            String businessId = businessObject.getString("id");
                            getYelpReviews(businessId);
                            getFourSquareVenue();
                        } catch (JSONException e) {
                            noDataFor(R.id.yelp_fragment, "Yelp");
                            Log.e(LOG_TAG, "Error while parsing json object: " + response, e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        noDataFor(R.id.yelp_fragment, "Yelp");
                        Log.e(LOG_TAG, "Error while parsing json object: " + errorResponse, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        noDataFor(R.id.yelp_fragment, "Yelp");
                        Log.e(LOG_TAG, "Error while parsing json object: " + responseString, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        noDataFor(R.id.yelp_fragment, "Yelp");
                        Log.e(LOG_TAG, "Error while parsing json object: " + errorResponse, throwable);
                    }
                });

    }

    private void getYelpReviews(final String businessId) {
        YelpAPI.getInstance().searchByBusinessId(businessId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    YelpBusiness business = YelpBusiness.fromJSONObject(response);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
                    transaction.replace(R.id.yelp_fragment, YelpDetailFragmentMinimum.newInstance(business));
                    transaction.commit();

                } catch (JSONException e) {
                    noDataFor(R.id.yelp_fragment, "Yelp");
                    Log.e(LOG_TAG, "Error while parsing json object: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                noDataFor(R.id.yelp_fragment, "Yelp");
                Log.e(LOG_TAG, "Error while parsing json object: " + errorResponse, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                noDataFor(R.id.yelp_fragment, "Yelp");
                Log.e(LOG_TAG, "Error while parsing json object: " + responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                noDataFor(R.id.yelp_fragment, "Yelp");
                Log.e(LOG_TAG, "Error while parsing json object: " + errorResponse, throwable);
            }
        });
    }

    private void getFourSquareVenue() {
        Pair<Double, Double> latlng = getBestLocation();
        double lat = latlng.first;
        double lng = latlng.second;

        fourSquareClient.searchForVenues(merchant.getName(), lat, lng, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray venues = response.getJSONObject("response").getJSONArray("venues");
                    if (venues.length() == 0) {
                        noDataFor(R.id.fs_fragment, "Foursquare");
                        return;
                    }
                    final JSONObject venue = venues.getJSONObject(0);
                    fourSquareClient.searchForVenueById(venue.getString("id"), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                fourSquareVenue = FourSquareVenue.fromJSONObject(
                                        response.getJSONObject("response").getJSONObject("venue"));
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
                                transaction.replace(R.id.fs_fragment, FourSquareDetailFragment.newInstance(fourSquareVenue));
                                transaction.commit();
                            } catch (JSONException e) {
                                noDataFor(R.id.fs_fragment, "Foursquare");
                                Log.e(LOG_TAG, "Error while parsing json object:", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            noDataFor(R.id.fs_fragment, "Foursquare");
                            Log.e(LOG_TAG, "Error while parsing json object", throwable);
                        }
                    });
                } catch (JSONException e) {
                    noDataFor(R.id.fs_fragment, "Foursquare");
                    Log.e(LOG_TAG, "Error while parsing json object", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                noDataFor(R.id.fs_fragment, "Foursquare");
                Log.e(LOG_TAG, "Error while parsing json object", throwable);
            }
        });
    }

    private Pair<Double, Double> getBestLocation() {
        double lat = merchant.getLat();
        double lng = merchant.getLng();
        LinkedList<RedemptionLocation> locations = new LinkedList<>(groupon.getUniqueRedemptionLocations());
        if (locations.size() > 1) {
            Log.d("multiple_locations", groupon.getId());
        }
        RedemptionLocation firstLocation = locations.getFirst();// only first one for now need to handle multiple locations
        if (lat == 0 || lng == 0) {
            lat = firstLocation.getLat(); // get from redemption location first
            lng = firstLocation.getLng();
            if (lat == 0 || lng == 0) {
                lat = groupon.getDivision().getLat(); // resort to division then at last
                lng = groupon.getDivision().getLng();
            }
        }
        return Pair.create(lat, lng);
    }

    public void getTips(View view) {
        Intent fourSquareIntent = new Intent(this, FourSquareActivity.class);
        fourSquareIntent.putExtra("venue", fourSquareVenue);
        startActivity(fourSquareIntent);
    }

    public void onClickOpenGoogle(View view) {
        Uri gmmIntentUri = Uri.parse("geo:" + place.latLng.latitude + "," + place.latLng.longitude + "?q=" + place.name);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void onClickPhone(View view) {
        Uri uri = Uri.parse("tel:" + place.phoneNumber);
        Intent callIntent = new Intent (Intent.ACTION_DIAL, uri);
        startActivity(callIntent);
    }
}
