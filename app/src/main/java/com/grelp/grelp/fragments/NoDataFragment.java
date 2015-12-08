package com.grelp.grelp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grelp.grelp.R;

public class NoDataFragment extends Fragment {

    private TextView tvNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_data, container, false);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
        String service = getArguments().getString("service");
        tvNoData.setText(getResources().getString(R.string.no_data_avail_for, service));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static NoDataFragment newInstance(String service) {
        Bundle args = new Bundle();
        args.putString("service", service);
        NoDataFragment fragment = new NoDataFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
