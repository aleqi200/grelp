package com.grelp.grelp.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.activities.YelpActivity;
import com.grelp.grelp.models.YelpBusiness;
import com.grelp.grelp.util.StringUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by acampelo on 11/23/15.
 */
public class YelpDetailFragment extends Fragment {

    private ImageView businessImg;
    private ImageView ratingImg;
    private TextView numberOfReviews;
    private TextView category;
    private TextView businessTitle;
    private TextView moreReviews;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yelp_detail, container, false);
        businessImg = (ImageView) view.findViewById(R.id.imgVBusinessImg);
        ratingImg = (ImageView) view.findViewById(R.id.imgYelpRating);
        numberOfReviews = (TextView) view.findViewById(R.id.tvNumberOfReviews);
        businessTitle = (TextView) view.findViewById(R.id.tvBusinessName);
        category = (TextView) view.findViewById(R.id.tvCategory);
        moreReviews = (TextView) view.findViewById(R.id.tvMoreReviews);
        final YelpBusiness yelp = getArguments().getParcelable("yelp");
        if (yelp != null) {
            View.OnClickListener openReviewListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent yelpIntent = new Intent(getContext(), YelpActivity.class);
                    yelpIntent.putExtra("yelp", yelp);
                    startActivity(yelpIntent);
                }
            };
            Picasso.with(getContext()).load(yelp.getImageUrl()).into(businessImg);
            Picasso.with(getContext()).load(yelp.getRatingImgUrl()).into(ratingImg);
            Resources res = getResources();
            String reviewString = res.getQuantityString(R.plurals.number_of_reviews, yelp.getReviewCount(), yelp.getReviewCount());
            numberOfReviews.setText(reviewString);
            category.setText(StringUtil.join(", ", yelp.getCategories()));
            businessTitle.setText(yelp.getName());

            // set up listener to open reviews
            businessImg.setOnClickListener(openReviewListener);
            numberOfReviews.setOnClickListener(openReviewListener);
            moreReviews.setOnClickListener(openReviewListener);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static YelpDetailFragment newInstance(YelpBusiness yelpBusiness) {
        Bundle args = new Bundle();
        args.putParcelable("yelp", yelpBusiness);
        YelpDetailFragment fragment = new YelpDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
