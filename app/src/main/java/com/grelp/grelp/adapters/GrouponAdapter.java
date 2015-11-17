package com.grelp.grelp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.Groupon;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class GrouponAdapter extends ArrayAdapter<Groupon> {

    private NumberFormat formatter = new DecimalFormat("#0.0");

    static class GrouponViewHolder {
        private ImageView ivDealImage;
        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvValue;
        private TextView tvSoldQuantity;
        private TextView tvDivision;
        private TextView tvDistance;
    }

    public GrouponAdapter(Context context, List<Groupon> objects) {
        super(context, R.layout.item_groupon, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Groupon groupon = getItem(position);
        GrouponViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_groupon,
                                                                    parent, false);
            viewHolder = new GrouponViewHolder();
            viewHolder.ivDealImage = (ImageView) convertView.findViewById(R.id.ivDealImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvDivision = (TextView) convertView.findViewById(R.id.tvDivision);
            viewHolder.tvSoldQuantity = (TextView) convertView.findViewById(R.id.tvSoldMessage);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.tvValue = (TextView) convertView.findViewById(R.id.tvValue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GrouponViewHolder) convertView.getTag();
        }
        if (groupon.getGrid4ImageUrl() != null) {
            Picasso.with(getContext()).load(groupon.getGrid4ImageUrl()).into(viewHolder.ivDealImage);
        }
        viewHolder.tvTitle.setText(groupon.getTitle());

        viewHolder.tvDistance.setText(formatter.format(groupon.getDistance()) + "mi");
        viewHolder.tvDivision.setText(groupon.getDivision());
        viewHolder.tvValue.setText(groupon.getMinValue());
        viewHolder.tvValue.setPaintFlags(viewHolder.tvValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.tvPrice.setText(groupon.getMinPrice());
        viewHolder.tvSoldQuantity.setText(groupon.getSoldQuantity() + " Bought");
        return convertView;
    }
}
