package com.grelp.grelp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grelp.grelp.R;
import com.grelp.grelp.models.Groupon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GrouponArrayAdapter extends RecyclerView.Adapter<GrouponViewHolder> {

    private List<Groupon> groupons;
    private Context context;

    public GrouponArrayAdapter(Context context) {
        this.groupons = new ArrayList<>();
        this.context = context;
    }

    @Override
    public GrouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupon,
                parent, false);
        GrouponViewHolder grouponViewHolder = new GrouponViewHolder(view);
        view.setOnClickListener(grouponViewHolder);
        return grouponViewHolder;
    }

    @Override
    public void onBindViewHolder(GrouponViewHolder holder, int position) {
        holder.bind(groupons.get(position));
    }

    public void addAll(List<Groupon> groupons) {
        this.groupons.addAll(groupons);
    }

    @Override
    public int getItemCount() {
        return groupons.size();
    }

    public Groupon getItem(int position) {
        return groupons.get(position);
    }
}
