package com.grelp.grelp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.grelp.grelp.R;
import com.grelp.grelp.data.YelpAPI;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GrouponDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupon_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView textView = (TextView) findViewById(R.id.tvYelpBusiness);
        YelpAPI.getInstance().searchByBusinessId("the-flying-falafel-san-francisco-3", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String name = response.getString("name");
                    textView.setText(name);
                } catch (JSONException e) {
                    Toast.makeText(GrouponDetailActivity.this, "could not read json response", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(GrouponDetailActivity.this, "failed request", Toast.LENGTH_LONG).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

}
