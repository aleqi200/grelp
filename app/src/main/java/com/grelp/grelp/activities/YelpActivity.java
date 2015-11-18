package com.grelp.grelp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.grelp.grelp.R;
import com.grelp.grelp.adapters.YelpAdapter;
import com.grelp.grelp.models.YelpReview;
import com.grelp.grelp.models.YelpUser;

import java.util.ArrayList;
import java.util.List;

public class YelpActivity extends AppCompatActivity {

    private ListView lvYelpReviews;
    private YelpAdapter yelpAdapter;
    private List<YelpReview> yelpReviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp);
        lvYelpReviews = (ListView) findViewById(R.id.lvYelpReviews);
        yelpReviewList = new ArrayList<>();
        yelpAdapter = new YelpAdapter(this, yelpReviewList);
        lvYelpReviews.setAdapter(yelpAdapter);
        String businessId = getIntent().getStringExtra("businessId");
        getYelpReviews(businessId);
    }

    private void getYelpReviews(String businessId) {
        yelpAdapter.addAll(getDummyYelpRating());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yelp, menu);
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

    public static YelpReview getDummyYelpRating() {
        YelpUser user = new YelpUser("3KNNxsQa4uooK5FAj7bVaQ",
                "http://s3-media3.fl.yelpcdn.com/photo/hk31BkJvJ8qcqoUvZ38rmQ/ms.jpg", "Hilary C.");
        YelpReview review = new YelpReview(4.0,
                "One of the owners is a former Sherpa from Nepal who has summitted Mt. Everest " +
                        "twice. While the restaurant is in a seeder part of the City, " +
                        "it's also on one...", 1440895245,
                "http://s3-media4.fl.yelpcdn.com/assets/2/www/img/f62a5be2f902/ico/stars/v1/stars_small_4.png",
                user);
        return review;
    }
}
