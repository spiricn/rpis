
package com.rpis.client.nav;

import com.rpis.client.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class NavigationListAdapter extends ArrayAdapter<NavigationMenuItem> {

    public NavigationListAdapter(Context context, NavigationMenuItem[] activities) {
        super(context, -1, activities);
        mContext = context;
        mValues = activities;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.nav_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView1);
        textView.setText(mValues[position].name);

        return rowView;
    }

    private final Context mContext;
    private final NavigationMenuItem[] mValues;
}
