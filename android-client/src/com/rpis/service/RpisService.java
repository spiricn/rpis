
package com.rpis.service;

import com.rpis.service.comm.IRpisService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RpisService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new RpisServiceImpl(this);
    }

    private IRpisService mBinder = null;
}
