package com.grelp.grelp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.GrouponOption;

import java.util.List;

public class GrouponOptionAdapter extends ArrayAdapter<GrouponOption> {

    static class GrouponOptionViewHolder {
        private TextView tvOptionTitle;
        private TextView tvOptionDiscount;
        private TextView tvBoughtQuantity;
        private TextView tvOptionValue;
        private TextView tvOptionPrice;
        //private RadioButton rdOptionButton;
    }

    public GrouponOptionAdapter(Context context, List<GrouponOption> objects) {
        super(context, R.layout.item_groupon_option, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GrouponOption option = getItem(position);
        GrouponOptionViewHolder optionViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_groupon_option,
                    parent, false);
            optionViewHolder = new GrouponOptionViewHolder();
            optionViewHolder.tvOptionTitle = (TextView) convertView.findViewById(R.id.tvOptionTitle);
            optionViewHolder.tvOptionDiscount = (TextView) convertView.findViewById(R.id.tvOptionDiscount);
            optionViewHolder.tvBoughtQuantity = (TextView) convertView.findViewById(R.id.tvBoughtQuantity);
            optionViewHolder.tvOptionValue = (TextView) convertView.findViewById(R.id.tvOptionValue);
            optionViewHolder.tvOptionPrice = (TextView) convertView.findViewById(R.id.tvOptionPrice);
            //optionViewHolder.rdOptionButton = (RadioButton) convertView.findViewById(R.id.rdOptionButton);
            convertView.setTag(optionViewHolder);
        } else {
            optionViewHolder = (GrouponOptionViewHolder) convertView.getTag();
        }
        optionViewHolder.tvOptionTitle.setText(option.getTitle());
        optionViewHolder.tvBoughtQuantity.setText(option.getSoldQuantityMessage());
        optionViewHolder.tvOptionDiscount.setText(option.getDiscountPercent());
        optionViewHolder.tvOptionPrice.setText(option.getPrice());
        optionViewHolder.tvOptionValue.setText(option.getValue());
        optionViewHolder.tvOptionValue.setPaintFlags(optionViewHolder.tvOptionValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        return convertView;
    }
}
