package com.grelp.grelp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.FourSquareTip;
import com.grelp.grelp.util.PrettyTimePrinter;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class FourSquareTipAdapter extends ArrayAdapter<FourSquareTip>  {
    static class TipViewHolder {
        private ImageView ivTipProfileImage;
        private TextView tvTipText;
        private TextView tvTipProfile;
        private TextView tvTipTimestamp;
    }

    public FourSquareTipAdapter(Context context, List<FourSquareTip> objects) {
        super(context, R.layout.item_foursquare_tip, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FourSquareTip fourSquareTip = getItem(position);
        TipViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_foursquare_tip,
                    parent, false);
            viewHolder = new TipViewHolder();
            viewHolder.ivTipProfileImage = (ImageView) convertView.findViewById(R.id.ivTipProfileImage);
            viewHolder.tvTipText = (TextView) convertView.findViewById(R.id.tvTipText);
            viewHolder.tvTipProfile = (TextView) convertView.findViewById(R.id.tvTipProfile);
            viewHolder.tvTipTimestamp = (TextView) convertView.findViewById(R.id.tvTipTimestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TipViewHolder) convertView.getTag();
        }
        viewHolder.tvTipText.setText(fourSquareTip.getText());
        viewHolder.tvTipProfile.setText(fourSquareTip.getUserName());
        viewHolder.tvTipTimestamp.setText(PrettyTimePrinter.getAbbreviatedTimeSpan(fourSquareTip.getTimestamp() * 1000));

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(0)
                .cornerRadiusDp(50)
                .oval(false)
                .build();

        Picasso.with(getContext())
                .load(fourSquareTip.getPhotoUrl())
                .fit()
                .transform(transformation)
                .into(viewHolder.ivTipProfileImage);

        return convertView;
    }
}
