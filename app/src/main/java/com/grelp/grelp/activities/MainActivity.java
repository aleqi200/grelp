package com.grelp.grelp.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.fragments.DealListFragment;
import com.grelp.grelp.fragments.DealMapFragment;
import com.grelp.grelp.models.Groupon;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DealListFragment.OnItemsAdded {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private ArrayList<Groupon> groupons = new ArrayList<>();
    private DealListFragment dealListFragment;
    private DealMapFragment dealMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        showDealList();
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
            if (dealMapFragment == null) {
                dealMapFragment = DealMapFragment.newInstance(groupons);
            }
            transaction.replace(R.id.container, dealMapFragment);
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDealList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_fade_in, R.anim.animation_fade_out);
        if (dealListFragment == null) {
            dealListFragment = DealListFragment.newInstance(null, groupons);
        }
        transaction.replace(R.id.container, dealListFragment);
        transaction.commit();
    }

    @Override
    public void onAdded(ArrayList<Groupon> grouponsAdded) {
        //Fragment mapsFragment = mSectionsPagerAdapter.getItem(1);
        if (groupons.isEmpty()) {
            groupons.addAll(grouponsAdded);
        }
//        if (mapsFragment != null && mapsFragment instanceof DealMapFragment) {
//            // If article frag is available, we're in two-pane layout...
//
//            // Call a method in the ArticleFragment to update its content
//            ((DealMapFragment) mapsFragment).addGroupons(grouponsAdded);
//        }
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
                } else {
                    Snackbar.make(getCurrentFocus(), "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
}
