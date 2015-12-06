package com.grelp.grelp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.activities.GrouponDetailActivity;
import com.grelp.grelp.data.YelpAPI;
import com.grelp.grelp.models.Groupon;
import com.grelp.grelp.models.YelpBusiness;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by acampelo on 12/3/15.
 */
public class GrouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String LOG_TAG = GrouponViewHolder.class.getName();

    private static ConcurrentHashMap<String, Float> yelpRatings = new ConcurrentHashMap<>();

    private ImageView ivDealImage;
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvValue;
    private TextView tvSoldQuantity;
    private TextView tvDivision;
    private TextView tvDistance;
    private Context context;
    private Groupon groupon;
    private RatingBar rbYelpRating;

    public GrouponViewHolder(View view) {
        super(view);
        this.context = view.getContext();
        ivDealImage = (ImageView) view.findViewById(R.id.ivMapDealImage);
        tvTitle = (TextView) view.findViewById(R.id.tvMapDealTitle);
        tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        tvDivision = (TextView) view.findViewById(R.id.tvDivision);
        tvSoldQuantity = (TextView) view.findViewById(R.id.tvSoldMessage);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        tvValue = (TextView) view.findViewById(R.id.tvOptionPrice);
        rbYelpRating = (RatingBar) view.findViewById(R.id.ratingBar);
    }

    public void bind(final Groupon groupon) {
        this.groupon = groupon;
        if (groupon.getGrid4ImageUrl() != null) {
            Picasso.with(context).load(groupon.getGrid4ImageUrl()).into(ivDealImage);
        }
        tvTitle.setText(groupon.getTitle());

        tvDistance.setText(groupon.getDistance() + "mi");
        tvDivision.setText(groupon.getDivision().getName());
        tvValue.setText(groupon.getMinValue());
        tvValue.setPaintFlags(tvValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvPrice.setText(groupon.getMinPrice());
        tvSoldQuantity.setText(groupon.getSoldQuantity() + " Bought");

        if(yelpRatings.contains(groupon.getId())) {
            rbYelpRating.setRating(yelpRatings.get(groupon.getId()));
        } else {
            rbYelpRating.setRating(0);
            YelpAPI.getInstance().searchForBusinesses(groupon.getMerchant().getName(), groupon.getLat(),
                    groupon.getLng(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONArray businesses = response.getJSONArray("businesses");
                                if (businesses.length() == 0) {
                                    return;
                                }
                                JSONObject businessObject = businesses.getJSONObject(0);
                                YelpBusiness business = YelpBusiness.fromJSONObject(businessObject);
                                yelpRatings.put(groupon.getId(), (float) business.getRating());
                                rbYelpRating.setRating((float) business.getRating());
                            } catch (JSONException e) {
                                Log.e(LOG_TAG, "Error while parsing json object", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(LOG_TAG, "Error getting Yelp business: " + errorResponse.toString(), throwable);
                        }
                    }
            );
        }
    }

    @Override
    public void onClick(View view) {
        Intent grpnDetailIntent = new Intent(context, GrouponDetailActivity.class);
        grpnDetailIntent.putExtra("groupon", groupon);
        Pair<View, String> p1 = Pair.create((View)ivDealImage, "dealImage");
        Pair<View, String> p2 = Pair.create((View)tvTitle, "dealTitle");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, p1, p2);
        context.startActivity(grpnDetailIntent, options.toBundle());
    }
}
