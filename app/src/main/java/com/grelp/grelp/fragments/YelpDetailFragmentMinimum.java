package com.grelp.grelp.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.YelpBusiness;
import com.grelp.grelp.models.YelpReview;
import com.grelp.grelp.util.ApplicationUtil;
import com.grelp.grelp.util.StringUtil;
import com.squareup.picasso.Picasso;

public class YelpDetailFragmentMinimum extends Fragment {


    private ImageView ratingImg;
    private TextView numberOfReviews;
    private TextView category;

    private ImageView ivYelpProfile;
    private ImageView ivRating;
    private TextView tvYelpUserName;
    private TextView tvReviewText;
    private TextView tvOpenYelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yelp_detail_minimum, container, false);

        ratingImg = (ImageView) view.findViewById(R.id.imgYelpRating);
        numberOfReviews = (TextView) view.findViewById(R.id.tvNumberOfReviews);

        category = (TextView) view.findViewById(R.id.tvCategory);
        ivYelpProfile = (ImageView) view.findViewById(R.id.ivYelpProfile);
        ivRating = (ImageView) view.findViewById(R.id.ivRating);
        tvReviewText = (TextView) view.findViewById(R.id.tvReviewText);
        tvYelpUserName = (TextView) view.findViewById(R.id.tvYelpUserName);
        tvOpenYelp = (TextView) view.findViewById(R.id.tvOpenYelp);
        final YelpBusiness yelp = getArguments().getParcelable("yelp");
        if (yelp != null) {
            View.OnClickListener openReviewListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uriString = "yelp:///biz/" + yelp.getId();
                    if (!ApplicationUtil.isAppInstalled(getActivity().getPackageManager(), "com.yelp.android")) {
                        uriString = "http://www.yelp.com/biz/" + yelp.getId();
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
                }
            };
            tvOpenYelp.setOnClickListener(openReviewListener);
            Picasso.with(getContext()).load(yelp.getRatingImgUrl()).into(ratingImg);
            Resources res = getResources();
            String reviewNumber = res.getString(R.string.num_reviews_parenthesis, yelp.getReviewCount());
            numberOfReviews.setText(reviewNumber);
            category.setText(StringUtil.join(", ", yelp.getCategories()));
            if (yelp.getYelpReviewList() != null && !yelp.getYelpReviewList().isEmpty()) {
                Log.d("yelp review count", String.valueOf(yelp.getYelpReviewList().size()));
                YelpReview review = yelp.getYelpReviewList().get(0);
                Picasso.with(getContext()).load(review.getUser().getProfileUrl()).into(ivYelpProfile);
                Picasso.with(getContext()).load(review.getRatingImageUrl()).into(ivRating);
                tvYelpUserName.setText(review.getUser().getName());
                tvReviewText.setText(review.getExcerpt());
            }
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static YelpDetailFragmentMinimum newInstance(YelpBusiness yelpBusiness) {
        Bundle args = new Bundle();
        args.putParcelable("yelp", yelpBusiness);
        YelpDetailFragmentMinimum fragment = new YelpDetailFragmentMinimum();
        fragment.setArguments(args);
        return fragment;
    }
}
