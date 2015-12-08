package com.grelp.grelp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.grelp.grelp.R;
import com.grelp.grelp.models.ParcelablePlace;
import com.grelp.grelp.models.YelpBusiness;

public class GooglePlacesFragment extends Fragment {
    private ParcelablePlace place;

    private TextView tvPhone;
    private RatingBar rbRating;
    private TextView tvNoRating;

    public GooglePlacesFragment() {
        // Required empty public constructor
    }

    public static GooglePlacesFragment newInstance(Place googlePlace) {
        GooglePlacesFragment fragment = new GooglePlacesFragment();
        Bundle args = new Bundle();
        args.putParcelable("google_place", new ParcelablePlace(googlePlace));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            place = getArguments().getParcelable("google_place");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_google_places, container, false);

        tvPhone = (TextView) v.findViewById(R.id.tvPhone);
        rbRating = (RatingBar) v.findViewById(R.id.rbRating);
        tvNoRating = (TextView) v.findViewById(R.id.tvNoRating);

        tvPhone.setText(place.phoneNumber);
        if(place.rating >= 0) {
            rbRating.setVisibility(View.VISIBLE);
            tvNoRating.setVisibility(View.GONE);
            rbRating.setRating(place.rating * 10);
        } else {
            rbRating.setVisibility(View.GONE);
            tvNoRating.setVisibility(View.VISIBLE);
        }

        return v;
    }
}
