
package com.rpis.service;

import com.rpis.service.comm.IDeviceControl;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.rest.RestAPI;

import android.os.RemoteException;

public class DeviceControl extends IDeviceControl.Stub {
    public DeviceControl(IRpisServer server) throws RemoteException {
        mServer = server;
        mApi = new RestAPI(mServer.getInfo().getRest() + "/power");
    }

    @Override
    public RpisResult shutdown(int delayMs) throws RemoteException {
        return mApi.parsedGet("shutdown?delayMs=" + delayMs) == null ? RpisResult.ERROR
                : RpisResult.OK;
    }

    @Override
    public RpisResult reboot(int delayMs) throws RemoteException {
        return mApi.parsedGet("reboot?delayMs=" + delayMs) == null ? RpisResult.ERROR
                : RpisResult.OK;
    }

    private RestAPI mApi;
    private IRpisServer mServer;
}
