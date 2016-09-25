
package com.rpis.service;

import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.ServerInfo;

import android.content.Context;
import android.os.RemoteException;

public class RpisServiceImpl extends IRpisService.Stub {
    public RpisServiceImpl(Context context) {
        mContext = context;
    }

    @Override
    public RpisResult scan(final IServerScanCallback callback) throws RemoteException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                scanThread(callback);
            }
        }).start();

        return RpisResult.OK;
    }

    private void scanThread(IServerScanCallback callback) {
        Locator locator = new Locator(mContext);

        ServerInfo server = null;

        while (server == null) {
            server = locator.findServer();

            if (server == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            callback.onServerFound(server);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IRpisServer connect(ServerInfo info) throws RemoteException {
        return new RpisServer(info);
    }

    private Context mContext;
}
