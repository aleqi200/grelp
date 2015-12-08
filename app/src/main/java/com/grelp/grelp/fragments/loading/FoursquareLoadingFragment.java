package com.grelp.grelp.fragments.loading;

import android.os.Bundle;
import android.widget.ImageView;

import com.grelp.grelp.R;

public class FoursquareLoadingFragment extends LoadingFragment {
    
    @Override
    protected void setImage(ImageView imageView) {
        imageView.setImageResource(R.drawable.fs_logo);
    }

    public static FoursquareLoadingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FoursquareLoadingFragment fragment = new FoursquareLoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
