package com.rpis.client.navigator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rpis.client.AControl;
import com.rpis.client.ATab;
import com.rpis.client.R;
import com.rpis.client.locator.ServerListAdapter;
import com.rpis.client.locator.ServerListItem;
import com.rpis.service.comm.IRpisService;

import java.util.ArrayList;
import java.util.List;

public class NavigatorFragment extends ATab {
    public  interface IListener{
        void onControlSelected(AControl control);
    }

    public NavigatorFragment(IRpisService service, List<AControl> controlList, IListener listener) {
        super(service);

        mControls = controlList;
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigator, container, false);
        mList = (ListView)view.findViewById(R.id.controlsList);
        mList.setAdapter(mAdapter = new NavigatorListAdapter(getContext(), new ArrayList<NavigatorListItem>()));
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavigatorListItem item = (NavigatorListItem)adapterView.getItemAtPosition(i);

                mListener.onControlSelected(item.control);
            }
        });
        return view;
    }

    @Override
    public void onPageShow(){
        for(AControl control : mControls){
            mAdapter.insert( new NavigatorListItem(control.getName(), control), mAdapter.getCount());
        }
    }

    @Override
    public void onPageHide(){
        mAdapter.clear();
    }

    @Override
    public String getName() {
        return null;
    }

    private List<AControl> mControls;
    private ListView mList;
    private NavigatorListAdapter mAdapter;
    private  IListener mListener;
}
