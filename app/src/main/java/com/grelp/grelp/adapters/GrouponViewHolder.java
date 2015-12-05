package com.grelp.grelp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.activities.GrouponDetailActivity;
import com.grelp.grelp.models.Groupon;
import com.squareup.picasso.Picasso;


/**
 * Created by acampelo on 12/3/15.
 */
public class GrouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView ivDealImage;
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvValue;
    private TextView tvSoldQuantity;
    private TextView tvDivision;
    private TextView tvDistance;
    private Context context;
    private Groupon groupon;

    public GrouponViewHolder(View view) {
        super(view);
        this.context = view.getContext();
        ivDealImage = (ImageView) view.findViewById(R.id.ivDealImage);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        tvDivision = (TextView) view.findViewById(R.id.tvDivision);
        tvSoldQuantity = (TextView) view.findViewById(R.id.tvSoldMessage);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        tvValue = (TextView) view.findViewById(R.id.tvOptionPrice);
    }

    public void bind(Groupon groupon) {
        this.groupon = groupon;
        if (groupon.getGrid4ImageUrl() != null) {
            Picasso.with(context).load(groupon.getGrid4ImageUrl()).into(ivDealImage);
        }
        tvTitle.setText(groupon.getTitle());

        tvDistance.setText(groupon.getDistance() + "mi");
        tvDivision.setText(groupon.getDivision().getName());
        tvValue.setText(groupon.getMinValue());
        tvValue.setPaintFlags(tvValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvPrice.setText(groupon.getMinPrice());
        tvSoldQuantity.setText(groupon.getSoldQuantity() + " Bought");
    }

    @Override
    public void onClick(View view) {
        Intent grpnDetailIntent = new Intent(context, GrouponDetailActivity.class);
        grpnDetailIntent.putExtra("groupon", groupon);
        context.startActivity(grpnDetailIntent);
    }
}
