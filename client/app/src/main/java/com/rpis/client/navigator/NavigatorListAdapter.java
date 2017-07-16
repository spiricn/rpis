package com.rpis.client.navigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rpis.client.R;

import java.util.ArrayList;


public class NavigatorListAdapter extends ArrayAdapter<NavigatorListItem> {

    public NavigatorListAdapter(Context context, ArrayList<NavigatorListItem> values) {
        super(context, -1, values);
        mContext = context;
        mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.nav_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView1);
        textView.setText(mValues.get(position).name);

        return rowView;
    }

    private final Context mContext;
    private ArrayList<NavigatorListItem> mValues;
}