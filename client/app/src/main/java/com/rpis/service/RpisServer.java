
package com.rpis.service;

import com.rpis.service.comm.IDeviceControl;
import com.rpis.service.comm.ILedControl;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.ServerInfo;

import android.os.RemoteException;

public class RpisServer extends IRpisServer.Stub {
    public RpisServer(ServerInfo info) throws RemoteException {
        mInfo = info;

        mLedControl = new LedControl(this);
        mDeviceControl = new DeviceControl(this);
    }

    @Override
    public ILedControl getLedControl() throws RemoteException {
        return mLedControl;
    }

    @Override
    public IDeviceControl getDeviceControl() throws RemoteException {
        return mDeviceControl;
    }

    @Override
    public String getAddress() throws RemoteException {
        return mAddress;
    }

    @Override
    public int getPort() throws RemoteException {
        return mPort;
    }

    @Override
    public ServerInfo getInfo() throws RemoteException {
        return mInfo;
    }

    private LedControl mLedControl;
    private DeviceControl mDeviceControl;
    private String mAddress;
    private int mPort;
    private ServerInfo mInfo;
}
