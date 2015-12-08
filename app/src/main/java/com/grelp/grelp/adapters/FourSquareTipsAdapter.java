package com.grelp.grelp.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grelp.grelp.R;
import com.grelp.grelp.models.FourSquareTip;

import java.util.ArrayList;
import java.util.List;

public class FourSquareTipsAdapter extends RecyclerView.Adapter<FourSquareViewHolder> {

    private List<FourSquareTip> tips;
    private Context context;

    public FourSquareTipsAdapter(Context context) {
        this.tips = new ArrayList<>();
        this.context = context;
    }

    @Override
    public FourSquareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foursquare_tip,
                parent, false);
        FourSquareViewHolder fourSquareViewHolder = new FourSquareViewHolder(view);
        return fourSquareViewHolder;
    }

    @Override
    public void onBindViewHolder(FourSquareViewHolder holder, int position) {
        holder.bind(tips.get(position));
    }

    public void addAll(List<FourSquareTip> tips) {
        this.tips.addAll(tips);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    public FourSquareTip getItem(int position) {
        return tips.get(position);
    }

    public void clear() {
        tips.clear();
        notifyDataSetChanged();
    }
}