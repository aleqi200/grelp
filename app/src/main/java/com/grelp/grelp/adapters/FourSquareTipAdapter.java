package com.grelp.grelp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.FourSquareTip;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FourSquareTipAdapter extends ArrayAdapter<FourSquareTip>  {
    static class TipViewHolder {
        private ImageView ivTipImage;
        private ImageView ivTipProfileImage;
        private TextView tvTipText;
        private TextView tvTipProfile;
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
            viewHolder.ivTipImage = (ImageView) convertView.findViewById(R.id.ivTipImage);
            viewHolder.ivTipProfileImage = (ImageView) convertView.findViewById(R.id.ivTipProfileImage);
            viewHolder.tvTipText = (TextView) convertView.findViewById(R.id.tvTipText);
            viewHolder.tvTipProfile = (TextView) convertView.findViewById(R.id.tvTipProfile);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TipViewHolder) convertView.getTag();
        }
        viewHolder.tvTipText.setText(fourSquareTip.getText());
        viewHolder.tvTipProfile.setText(fourSquareTip.getUserName());
        Picasso.with(getContext()).load(fourSquareTip.getPhotoUrl()).into(viewHolder.ivTipProfileImage);
        return convertView;
    }
}
