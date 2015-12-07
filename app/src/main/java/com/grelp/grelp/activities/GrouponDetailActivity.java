package com.grelp.grelp.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
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
import com.grelp.grelp.fragments.YelpDetailFragment;
import com.grelp.grelp.fragments.YelpDetailFragmentMinimum;
import com.grelp.grelp.models.FourSquareVenue;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.models.GrouponMerchant;
import com.grelp.grelp.models.GrouponOption;
import com.grelp.grelp.models.RedemptionLocation;
import com.grelp.grelp.models.YelpBusiness;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private LinearLayout llDealOptions;
    private ImageView btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupon_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupon = getIntent().getParcelableExtra("groupon");
        toolbar.setTitle(groupon.getShortAnnouncementTitle());
        grouponClient = GrouponClient.getInstance();
        googlePlacesAPI = new GooglePlacesAPI(this);
        fourSquareClient = FourSquareClient.getInstance();
        setupViews();
        getGrouponMerchant();
    }

    private void setupViews() {
        ivDetailedImage = (ImageView) findViewById(R.id.ivDetailedImage);
        tvDetailedTitle = (TextView) findViewById(R.id.tvDetailedTitle);
        llDealOptions = (LinearLayout) findViewById(R.id.llDealOptions);
        btnBuy = (ImageView) findViewById(R.id.btnBuy);

        //Setup groupon detailed view
        tvDetailedTitle.setText(groupon.getTitle());
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
                    Log.d("GOOGLE_PLACES_API", "Got place: " + place.getName());
                } else {
                    Log.d("GOOGLE_PLACES_API", "Failed to find place via google: " + merchant.getName());
                }
            }
        });
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
                                return;
                            }
                            JSONObject businessObject = businesses.getJSONObject(0);
                            String businessId = businessObject.getString("id");
                            getYelpReviews(businessId);
                            getFourSquareVenue();
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error while parsing json object: " + response, e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
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
                    Log.e(LOG_TAG, "Error while parsing json object: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
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
                                Log.e(LOG_TAG, "Error while parsing json object:", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e(LOG_TAG, "Error while parsing json object", throwable);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error while parsing json object", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
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
        if (lng == 0) {

        }
        return Pair.create(lat, lng);
    }

    public void getTips(View view) {
        Intent fourSquareIntent = new Intent(this, FourSquareActivity.class);
        fourSquareIntent.putExtra("venue", fourSquareVenue);
        startActivity(fourSquareIntent);
    }
}
