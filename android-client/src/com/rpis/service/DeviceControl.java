
package com.rpis.service;

import com.rpis.service.comm.IDeviceControl;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.RpisResult;

import android.os.RemoteException;

public class DeviceControl extends IDeviceControl.Stub {
    public DeviceControl(IRpisServer server) {
        mServer = server;
    }

    @Override
    public RpisResult shutdown() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RpisResult reboot() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    private IRpisServer mServer;
}
