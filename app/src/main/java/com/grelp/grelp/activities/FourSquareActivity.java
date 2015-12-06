package com.grelp.grelp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.adapters.FourSquareTipAdapter;
import com.grelp.grelp.data.FourSquareClient;
import com.grelp.grelp.models.FourSquareTip;
import com.grelp.grelp.models.FourSquareVenue;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class FourSquareActivity extends AppCompatActivity {
    private static final String LOG_TAG = "FourSquareActivity";
    private ImageView ivFourSquare;
    private TextView tvRatingCount;
    private TextView tvFourSquareRatings;
    private ListView lvFourSquareTips;
    private FourSquareVenue venue;
    private List<FourSquareTip> tips;
    private FourSquareTipAdapter fourSquareTipAdapter;
    private FourSquareClient fourSquareClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square);
        venue = getIntent().getParcelableExtra("venue");
        setupViews();
        fourSquareClient = FourSquareClient.getInstance();
        tips = new LinkedList<>();
        fourSquareTipAdapter = new FourSquareTipAdapter(this, tips);
        lvFourSquareTips.setAdapter(fourSquareTipAdapter);
        getFourSquareTipsForVenue();
    }

    private void setupViews() {
        ivFourSquare = (ImageView) findViewById(R.id.ivFourSquare);
        tvRatingCount = (TextView) findViewById(R.id.tvRatingCount);
        tvFourSquareRatings = (TextView) findViewById(R.id.tvFourSquareRatings);
        lvFourSquareTips = (ListView) findViewById(R.id.lvFourSquareTips);

        tvRatingCount.setText(venue.getRating());
        tvFourSquareRatings.setText(venue.getRatingCount());
        //Picasso.with(this).load(venue.getFourSquareUrl()).into(ivFourSquare);
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
                    tips.addAll(FourSquareTip.fromJSONObject(responseObject.getJSONObject("tips")));
                    fourSquareTipAdapter.notifyDataSetChanged();
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
