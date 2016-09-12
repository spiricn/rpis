
package com.rpis.service;

import com.rpis.service.comm.Color;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.ILedControl;
import com.rpis.service.comm.IRpisServer;

import android.os.RemoteException;

public class LedControl extends ILedControl.Stub {
    public LedControl(IRpisServer server) {
        mServer = server;
    }

    @Override
    public RpisResult init() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RpisResult term() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getColor() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RpisResult setColor(Color color) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
    private IRpisServer mServer;
}
