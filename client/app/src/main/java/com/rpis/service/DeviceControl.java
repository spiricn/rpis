
package com.rpis.service;

import com.rpis.service.comm.IDeviceControl;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.rest.RestAPI;

import android.os.RemoteException;

public class DeviceControl extends IDeviceControl.Stub {
    public DeviceControl(IRpisServer server) throws RemoteException {
        mServer = server;
        mPowerApi = new RestAPI(mServer.getInfo().getRest() + "/power");
        mStatusApi = new RestAPI(mServer.getInfo().getRest() + "/status");
    }

    @Override
    public RpisResult shutdown(int delayMs) throws RemoteException {
        return mPowerApi.parsedGet("shutdown?delayMs=" + delayMs) == null ? RpisResult.ERROR
                : RpisResult.OK;
    }

    @Override
    public RpisResult reboot(int delayMs) throws RemoteException {
        return mPowerApi.parsedGet("reboot?delayMs=" + delayMs) == null ? RpisResult.ERROR
                : RpisResult.OK;
    }

    @Override
    public RpisResult stopServer(int delayMs){
        return mPowerApi.parsedGet("stop?delayMs=" + delayMs) == null ? RpisResult.ERROR
                : RpisResult.OK;
    }

    @Override
    public RpisResult ping() throws RemoteException {
        return mStatusApi.parsedGet("ping") == null ? RpisResult.ERROR : RpisResult.OK;
    }

    private RestAPI mPowerApi;
    private RestAPI mStatusApi;
    private IRpisServer mServer;
}
