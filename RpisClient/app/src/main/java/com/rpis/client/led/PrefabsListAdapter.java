package com.rpis.client.led;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rpis.client.R;

import java.util.ArrayList;


public class PrefabsListAdapter extends ArrayAdapter<PrefabsListItem> {

    public PrefabsListAdapter(Context context, ArrayList<PrefabsListItem> values) {
        super(context, -1, values);
        mContext = context;
        mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.prefabs_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView1);
        textView.setText(mValues.get(position).prefab.getName());

        return rowView;
    }

    private final Context mContext;
    private ArrayList<PrefabsListItem> mValues;
}