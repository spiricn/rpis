
package com.rpis.service;

import org.json.JSONException;

import com.rpis.service.comm.Color;
import com.rpis.service.comm.ILedControl;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.rest.HTTPResponse;
import com.rpis.service.rest.RestAPI;

import android.os.RemoteException;

public class LedControl extends ILedControl.Stub {
    public LedControl(IRpisServer server) throws RemoteException {
        mServer = server;
        mApi = new RestAPI(mServer.getInfo().getRest() + "/strip");
    }

    @Override
    public RpisResult powerOn() throws RemoteException {
        return mApi.parsedGet("powerOn") == null ? RpisResult.ERROR : RpisResult.OK;
    }

    @Override
    public RpisResult powerOff() throws RemoteException {
        return mApi.parsedGet("powerOff") == null ? RpisResult.ERROR : RpisResult.OK;
    }

    @Override
    public Color getColor() throws RemoteException {
        HTTPResponse res = mApi.parsedGet("color");

        try {
            return res != null ? new Color(res.getResponse().getJSONObject("res")) : null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RpisResult setColor(Color color) throws RemoteException {
        return mApi.parsedGet("color/set?h=" + color.getHue() + "&s=" + color.getSaturation()
                + "&v=" + color.getValue()) == null
                        ? RpisResult.ERROR : RpisResult.OK;
    }

    @Override
    public boolean isPoweredOn() throws RemoteException {
        HTTPResponse res = mApi.parsedGet("poweredOn");

        if (res == null) {
            return false;
        }

        try {
            return res.getResponse().getBoolean("res");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private RestAPI mApi;
    private IRpisServer mServer;
}
