package com.grelp.grelp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.FourSquareTip;
import com.grelp.grelp.models.FourSquareVenue;
import com.squareup.picasso.Picasso;

public class FourSquareDetailFragment extends Fragment {

    private TextView tvFourSquareRatings;
    private TextView tvRatingCount;
    private TextView tvFourSquareTip;
    private ImageView ivFourSquareProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static FourSquareDetailFragment newInstance(FourSquareVenue venue) {
        Bundle args = new Bundle();
        args.putParcelable("fourSquare", venue);
        FourSquareDetailFragment fragment = new FourSquareDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fs_detail, container, false);
        final FourSquareVenue fourSquareVenue = getArguments().getParcelable("fourSquare");

        tvFourSquareRatings = (TextView) view.findViewById(R.id.tvFourSquareRatings);
        tvRatingCount = (TextView) view.findViewById(R.id.tvRatingCount);
        tvFourSquareTip = (TextView) view.findViewById(R.id.tvFourSquareTip);
        ivFourSquareProfile = (ImageView) view.findViewById(R.id.ivFourSquareProfile);

        tvRatingCount.setText(fourSquareVenue.getRating());
        tvFourSquareRatings.setText(fourSquareVenue.getRatingCount());
        if (fourSquareVenue.getTips() != null && fourSquareVenue.getTips().size() > 0) {
            FourSquareTip tip = fourSquareVenue.getTips().get(0);
            tvFourSquareTip.setText(tip.getText());
            if (tip.getPhotoUrl() != null) {
                Picasso.with(getContext()).load(tip.getPhotoUrl()).into(ivFourSquareProfile);
            }
        }
        return view;
    }

}
