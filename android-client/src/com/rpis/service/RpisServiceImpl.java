
package com.rpis.service;

import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.RpisResult;

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
        try {
            // TODO
            callback.onServerFound("192.168.1.42", 80);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IRpisServer connect(String address, int port) throws RemoteException {
        return new RpisServer(address, port);
    }

    private Context mContext;
}
