package com.grelp.grelp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.YelpReview;
import com.squareup.picasso.Picasso;

import java.util.List;

public class YelpAdapter extends ArrayAdapter<YelpReview> {

    static class YelpViewHolder {
        ImageView ivYelpProfile;
        ImageView ivRating;
        TextView tvYelpUserName;
        TextView tvReviewTimestamp;
        TextView tvReviewText;
    }

    public YelpAdapter(Context context, List<YelpReview> reviews) {
        super(context, R.layout.item_yelp_review, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YelpReview review = getItem(position);
        YelpViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_yelp_review,
                    parent, false);
            viewHolder = new YelpViewHolder();
            viewHolder.ivYelpProfile = (ImageView) convertView.findViewById(R.id.ivYelpProfile);
            viewHolder.ivRating = (ImageView) convertView.findViewById(R.id.ivRating);
            viewHolder.tvReviewText = (TextView) convertView.findViewById(R.id.tvReviewText);
            viewHolder.tvYelpUserName = (TextView) convertView.findViewById(R.id.tvYelpUserName);
            viewHolder.tvReviewTimestamp = (TextView) convertView.findViewById(R.id.tvReviewTimestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (YelpViewHolder) convertView.getTag();
            Picasso.with(getContext()).load(review.getUser().getProfileUrl()).into(viewHolder.ivYelpProfile);
            Picasso.with(getContext()).load(review.getRatingImageUrl()).into(viewHolder.ivRating);
            viewHolder.tvYelpUserName.setText(review.getUser().getName());
            viewHolder.tvReviewTimestamp.setText(review.getTimeCreated() + "");
            viewHolder.tvReviewText.setText(review.getExcerpt());

        }
        return convertView;
    }
}
