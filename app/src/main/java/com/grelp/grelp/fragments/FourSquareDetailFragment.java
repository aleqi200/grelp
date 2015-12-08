package com.grelp.grelp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.FourSquarePhrase;
import com.grelp.grelp.models.FourSquareTip;
import com.grelp.grelp.models.FourSquareVenue;
import com.grelp.grelp.util.StringUtil;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class FourSquareDetailFragment extends Fragment {

    private TextView tvFourSquareRatings;
    private TextView tvRatingCount;
    private TextView tvFourSquareTip;
    private TextView tvFSUserName;
    private ImageView ivFourSquareProfile;
    private TextView tvFSPhrases;
    private TextView tvOpenFourSquare;

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
        if (fourSquareVenue == null) {
            view.setVisibility(View.GONE);
        }
        tvFourSquareRatings = (TextView) view.findViewById(R.id.tvFourSquareRatings);
        tvRatingCount = (TextView) view.findViewById(R.id.tvRatingCount);
        tvFourSquareTip = (TextView) view.findViewById(R.id.tvFourSquareTip);
        ivFourSquareProfile = (ImageView) view.findViewById(R.id.ivFourSquareProfile);
        tvFSUserName = (TextView) view.findViewById(R.id.tvFSUserName);
        tvFSPhrases = (TextView) view.findViewById(R.id.tvFSPhrases);
        tvOpenFourSquare = (TextView) view.findViewById(R.id.tvOpenFourSquare);

        tvRatingCount.setText(fourSquareVenue.getRating());
        tvFourSquareRatings.setText("(" + fourSquareVenue.getRatingCount() + ")");

        View.OnClickListener openReviewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriString = "https://foursquare.com/venue/" + fourSquareVenue.getId();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
            }
        };
        tvOpenFourSquare.setOnClickListener(openReviewListener);

        if (fourSquareVenue.getTips() != null && fourSquareVenue.getTips().size() > 0) {
            FourSquareTip tip = fourSquareVenue.getTips().get(0);
            tvFourSquareTip.setText(tip.getText());
            tvFSUserName.setText(tip.getUserName());
            if (tip.getPhotoUrl() != null) {

                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(0)
                        .cornerRadiusDp(75)
                        .oval(false)
                        .build();

                Picasso.with(getContext())
                        .load(tip.getPhotoUrl())
                        .fit()
                        .transform(transformation)
                        .into(ivFourSquareProfile);
            }
        } else {
            view.setVisibility(View.GONE);
        }

        if (fourSquareVenue.getPhrases() != null && fourSquareVenue.getPhrases().size() > 0) {
            List<FourSquarePhrase> phrases = fourSquareVenue.getPhrases();
            tvFSPhrases.setText(StringUtil.join(", ", phrases));
        } else {
            tvFSPhrases.setVisibility(View.GONE);
        }
        return view;
    }

}
