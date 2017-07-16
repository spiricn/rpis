
package com.rpis.service;

import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.ServerInfo;

import android.content.Context;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class RpisServiceImpl extends IRpisService.Stub {
    public RpisServiceImpl(Context context) {
        mContext = context;
    }

    @Override
    public RpisResult startScan(final IServerScanCallback callback) throws RemoteException {
        if(mScanRunning){
            return RpisResult.ERROR;
        }

        mScanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                scanThread(callback);
            }
        });

        mScanRunning = true;

        mScanThread.start();

        return RpisResult.OK;
    }

    @Override
    public RpisResult stopScan() throws RemoteException {
        if(!mScanRunning){
            return RpisResult.ERROR;
        }

        mScanRunning = false;

        try {
            mScanThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mScanThread = null;

        return null;
    }

    private void scanThread(IServerScanCallback callback) {
        Locator locator = new Locator(mContext);

        ServerInfo server = null;

        while (mScanRunning) {
            server = locator.findServer();
            if (server == null) {
                continue;
            }

            try {
                boolean done = callback.onServerFound(server);
                if(done){
                    break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IRpisServer connect(ServerInfo info) throws RemoteException {
        return new RpisServer(info);
    }

    private Context mContext;
    private Thread mScanThread;
    private boolean mScanRunning = false;
}
