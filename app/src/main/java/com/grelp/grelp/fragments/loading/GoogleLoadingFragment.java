package com.grelp.grelp.fragments.loading;

import android.os.Bundle;
import android.widget.ImageView;

import com.grelp.grelp.R;

public class GoogleLoadingFragment extends LoadingFragment {
    
    @Override
    protected void setImage(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_google_maps);
    }

    public static GoogleLoadingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        GoogleLoadingFragment fragment = new GoogleLoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
