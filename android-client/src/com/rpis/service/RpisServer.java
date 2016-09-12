
package com.rpis.service;

import com.rpis.service.comm.IDeviceControl;
import com.rpis.service.comm.ILedControl;
import com.rpis.service.comm.IRpisServer;

import android.os.RemoteException;

public class RpisServer extends IRpisServer.Stub {
    public RpisServer(String address, int port) {
        mAddress = address;
        mPort = port;
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

    private LedControl mLedControl = new LedControl(this);
    private DeviceControl mDeviceControl = new DeviceControl(this);
    private String mAddress;
    private int mPort;
}
