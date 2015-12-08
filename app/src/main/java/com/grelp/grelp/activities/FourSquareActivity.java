package com.grelp.grelp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.adapters.FourSquareTipsAdapter;
import com.grelp.grelp.data.FourSquareClient;
import com.grelp.grelp.models.FourSquareTip;
import com.grelp.grelp.models.FourSquareVenue;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class FourSquareActivity extends AppCompatActivity {
    private static final String LOG_TAG = "FourSquareActivity";
    private ImageView ivFourSquare;
    private TextView tvRatingCount;
    private TextView tvFourSquareRatings;
    private RecyclerView lvFourSquareTips;
    private FourSquareVenue venue;
    private LinearLayoutManager mLayoutManager;
    private FourSquareTipsAdapter fourSquareTipAdapter;
    private FourSquareClient fourSquareClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square);
        venue = getIntent().getParcelableExtra("venue");
        setupViews();
        fourSquareClient = FourSquareClient.getInstance();
        mLayoutManager = new LinearLayoutManager(this);
        lvFourSquareTips.setLayoutManager(mLayoutManager);
        fourSquareTipAdapter = new FourSquareTipsAdapter(this);
        lvFourSquareTips.setAdapter(fourSquareTipAdapter);
        getFourSquareTipsForVenue();
    }

    private void setupViews() {
        ivFourSquare = (ImageView) findViewById(R.id.ivFourSquare);
        tvRatingCount = (TextView) findViewById(R.id.tvRatingCount);
        tvFourSquareRatings = (TextView) findViewById(R.id.tvFourSquareRatings);
        lvFourSquareTips = (RecyclerView) findViewById(R.id.lvFourSquareTips);

        tvRatingCount.setText(venue.getRating());
        //tvFourSquareRatings.setText(venue.getRatingCount());
        Picasso.with(this).load(venue.getFourSquareUrl()).into(ivFourSquare);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_four_square, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getFourSquareTipsForVenue() {
        fourSquareClient.getTipsForVenue(venue.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject responseObject = response.getJSONObject("response");
                    fourSquareTipAdapter.addAll(FourSquareTip.fromJSONObject(responseObject.getJSONObject("tips")));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error while parsing json object:" + response, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(LOG_TAG, "Error while parsing json object:" + responseString, throwable);
            }
        });
    }
}
