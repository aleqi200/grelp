package com.grelp.grelp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.FourSquareTip;
import com.grelp.grelp.util.PrettyTimePrinter;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class FourSquareViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivTipProfileImage;
    private TextView tvTipText;
    private TextView tvTipProfile;
    private TextView tvTipTimestamp;
    private Context context;
    private FourSquareTip fourSquareTip;

    public FourSquareViewHolder(View view) {
        super(view);;
        this.context = view.getContext();
        ivTipProfileImage = (ImageView) view.findViewById(R.id.ivTipProfileImage);
        tvTipText = (TextView) view.findViewById(R.id.tvTipText);
        tvTipProfile = (TextView) view.findViewById(R.id.tvTipProfile);
        tvTipTimestamp = (TextView) view.findViewById(R.id.tvTipTimestamp);
    }

    public void bind(final FourSquareTip fourSquareTip) {
        this.fourSquareTip = fourSquareTip;
        tvTipText.setText(fourSquareTip.getText());
        tvTipProfile.setText(fourSquareTip.getUserName());
        tvTipTimestamp.setText(PrettyTimePrinter.getAbbreviatedTimeSpan(fourSquareTip.getTimestamp() * 1000));

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(0)
                .cornerRadiusDp(50)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(fourSquareTip.getPhotoUrl())
                .fit()
                .transform(transformation)
                .into(ivTipProfileImage);
    }
}
