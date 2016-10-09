
package com.rpis.service;

import com.rpis.service.comm.IRpisService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RpisService extends Service {
    private static final String TAG = RpisService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new RpisServiceImpl(this);

        Log.d(TAG, "####################################");
        Log.d(TAG, "###### Started RPIs service #######");
        Log.d(TAG, "####################################");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "####################################");
        Log.d(TAG, "###### Stopped RPIs service #######");
        Log.d(TAG, "####################################");
    }

    private IRpisService mBinder = null;
}
