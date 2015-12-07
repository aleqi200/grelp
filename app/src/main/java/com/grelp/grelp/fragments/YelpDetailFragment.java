package com.grelp.grelp.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.grelp.grelp.util.StringUtil;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class YelpDetailFragment extends Fragment {

    private ImageView businessImg;
    private ImageView ratingImg;
    private TextView numberOfReviews;
    private TextView category;
    private TextView businessTitle;
    private TextView phoneNumber;
    private ImageView ivYelpProfile;
    private ImageView ivRating;
    private TextView tvYelpUserName;
    private TextView tvReviewTimestamp;
    private TextView tvReviewText;
    private View yelpReviewsHolder;

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
        phoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
        ivYelpProfile = (ImageView) view.findViewById(R.id.ivYelpProfile);
        ivRating = (ImageView) view.findViewById(R.id.ivRating);
        tvReviewText = (TextView) view.findViewById(R.id.tvReviewText);
        tvYelpUserName = (TextView) view.findViewById(R.id.tvYelpUserName);
        tvReviewTimestamp = (TextView) view.findViewById(R.id.tvReviewTimestamp);
        yelpReviewsHolder = view.findViewById(R.id.yelpReviewsHolder);
        final YelpBusiness yelp = getArguments().getParcelable("yelp");
        if (yelp != null) {
            View.OnClickListener openReviewListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uriString = "yelp:///biz/" + yelp.getId();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
                }
            };
            Picasso.with(getContext()).load(yelp.getImageUrl()).into(businessImg);
            Picasso.with(getContext()).load(yelp.getRatingImgUrl()).into(ratingImg);
            Resources res = getResources();
            String reviewString = res.getQuantityString(R.plurals.number_of_reviews, yelp.getReviewCount(), yelp.getReviewCount());
            numberOfReviews.setText(reviewString);
            category.setText(StringUtil.join(", ", yelp.getCategories()));
            businessTitle.setText(yelp.getName());
            boolean hasPhone = getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phoneNumber.setText(PhoneNumberUtils.formatNumber(yelp.getPhone(), "US"));
            } else {
                phoneNumber.setText(yelp.getPhone());
            }
            if (hasPhone) {
                phoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            callIntent.setData(Uri.parse("tel:" + PhoneNumberUtils.normalizeNumber(yelp.getPhone())));
                        } else {
                            callIntent.setData(Uri.parse("tel:" + yelp.getPhone()));
                        }
                        startActivity(callIntent);
                    }
                });
            }

            // set up listener to open reviews
            businessImg.setOnClickListener(openReviewListener);
            numberOfReviews.setOnClickListener(openReviewListener);
            if (yelp.getYelpReviewList() != null && !yelp.getYelpReviewList().isEmpty()) {
                Log.d("yelp review count", String.valueOf(yelp.getYelpReviewList().size()));
                YelpReview review = yelp.getYelpReviewList().get(0);

                //Applying rounded transformation to yelp profile image
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(0)
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build();

                Picasso.with(getContext())
                        .load(review.getUser().getProfileUrl())
                        .fit()
                        .transform(transformation)
                        .into(ivYelpProfile);
                Picasso.with(getContext()).load(review.getRatingImageUrl()).into(ivRating);
                tvYelpUserName.setText(review.getUser().getName());
                tvReviewTimestamp.setText(review.getTimeCreated());
                tvReviewText.setText(review.getExcerpt());
            } else {
                yelpReviewsHolder.setVisibility(View.INVISIBLE);
            }
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
