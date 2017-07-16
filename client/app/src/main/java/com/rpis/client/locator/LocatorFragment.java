
package com.rpis.client.locator;

import com.rpis.client.ATab;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.ServerInfo;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rpis.client.R;

import java.util.ArrayList;

public class LocatorFragment extends ATab {
    private static final String TAG = LocatorFragment.class.getSimpleName();

    public interface IListener{
        void onServerSelected(ServerInfo server);
    }

    public LocatorFragment(IRpisService service, IListener listener){
        super(service);

        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator, container, false);

        mList = (ListView)view.findViewById(R.id.serverList);
        mList.setAdapter(mAdapter = new ServerListAdapter(getContext(), new ArrayList<ServerListItem>()));
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ServerListItem item = (ServerListItem)adapterView.getItemAtPosition(i);

                mListener.onServerSelected(item.server);
            }
        });

        return view;
    }

    @Override
    public void onPageShow(){
        try {
            getService().startScan(new IServerScanCallback.Stub() {
                @Override
                public boolean onServerFound(final ServerInfo server) throws RemoteException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ServerListAdapter adapter = (ServerListAdapter)mList.getAdapter();
                            String displayName = server.getName() + "(" + server.getVersion() + ")\n" + server.getUid();

                            adapter.insert(new ServerListItem(displayName, server), adapter.getCount());
                        }
                    });


                    return false;
                }
            });
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
        }
    }

    @Override
    public void onPageHide(){
        try {
            getService().stopScan();
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
        }

        mAdapter.clear();
    }

    @Override
    public String getName() {
        return "Server Locator";
    }


    private IListener mListener;
    private ListView mList;
    private ServerListAdapter mAdapter;
}
