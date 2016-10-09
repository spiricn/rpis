package com.rpis.client.locator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rpis.client.R;

import java.util.ArrayList;


public class ServerListAdapter extends ArrayAdapter<ServerListItem> {

    public ServerListAdapter(Context context, ArrayList<ServerListItem> values) {
        super(context, -1, values);
        mContext = context;
        mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.server_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView1);
        textView.setText(mValues.get(position).name);

        return rowView;
    }

    private final Context mContext;
    private ArrayList<ServerListItem> mValues;
}