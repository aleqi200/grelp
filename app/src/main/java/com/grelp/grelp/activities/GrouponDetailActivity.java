package com.grelp.grelp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.data.GrouponClient;
import com.grelp.grelp.data.YelpAPI;
import com.grelp.grelp.fragments.YelpDetailFragment;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.models.GrouponMerchant;
import com.grelp.grelp.models.RedemptionLocation;
import com.grelp.grelp.models.YelpBusiness;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;

public class GrouponDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GrouponDetail";
    private GrouponClient grouponClient;
    private Groupon groupon;
    private GrouponMerchant merchant;
    private ImageView ivDetailedImage;
    private TextView tvDetailedTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupon_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupon = getIntent().getParcelableExtra("groupon");
        toolbar.setTitle(groupon.getShortAnnouncementTitle());
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        grouponClient = GrouponClient.getInstance();
        setupViews();
        getGrouponMerchant();
    }

    private void setupViews() {
        ivDetailedImage = (ImageView) findViewById(R.id.ivDetailedImage);
        tvDetailedTitle = (TextView) findViewById(R.id.tvDetailedTitle);
    }

    private void getGrouponMerchant() {

        grouponClient.getMerchant(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    merchant = GrouponMerchant.fromJSONObject(response.getJSONObject("merchant"));
                    searchForMerchantOnYelp();

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

    private void searchForMerchantOnYelp() {
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
        YelpAPI.getInstance().searchForBusinesses(merchant.getName(), lat, lng,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject businessObject = response.getJSONArray("businesses").getJSONObject(0);
                            String businessId = businessObject.getString("id");
                            getYelpReviews(businessId);
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
                    tvDetailedTitle.setText(groupon.getTitle());

                    Picasso.with(GrouponDetailActivity.this).load(groupon.getGrid4ImageUrl()).into(ivDetailedImage);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.yelp_fragment, YelpDetailFragment.newInstance(business))
                            .commit();

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
}
