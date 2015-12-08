package com.grelp.grelp.fragments.loading;

import android.os.Bundle;
import android.widget.ImageView;

import com.grelp.grelp.R;

public class YelpLoadingFragment extends LoadingFragment {
    
    @Override
    protected void setImage(ImageView imageView) {
        imageView.setImageResource(R.mipmap.ic_yelp_icon);
    }

    public static YelpLoadingFragment newInstance() {

        Bundle args = new Bundle();
        
        YelpLoadingFragment fragment = new YelpLoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
