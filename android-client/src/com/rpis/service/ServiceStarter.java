
package com.rpis.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceStarter extends BroadcastReceiver {
    public static final String START_SERVICE_INTENT = "com.rpis.RpisService.START_SERVICE";
    public static final String STOP_SERVICE_INTENT = "com.rpis.RpisService.STOP_SERVICE";
    public static final String RESTART_SERVICE_INTENT = "com.rpis.RpisService.RESTART_SERVICE";
    public static final String BOOT_INTENT = "android.intent.action.BOOT_COMPLETED";

    private static final String TAG = ServiceStarter.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent: " + intent.getAction());

        mContext = context;

        if (intent.getAction() != null) {
            if (intent.getAction().equals(START_SERVICE_INTENT)
                    || intent.getAction().equals(BOOT_INTENT)) {
                startService();
            } else if (intent.getAction().equals(STOP_SERVICE_INTENT)) {
                stopService();
            } else if (intent.getAction().equals(RESTART_SERVICE_INTENT)) {
                restartService();
            }

        }
    }

    private void restartService() {
        Log.d(TAG, "Restarting servce");
        stopService();
        startService();
    }

    private void startService() {
        Log.d(TAG, "Starting service");
        Intent i = new Intent();
        i.setClass(mContext, RpisService.class);
        mContext.startService(i);
    }

    private void stopService() {
        Log.d(TAG, "Stopping service");
        Intent i = new Intent();
        i.setClass(mContext, RpisService.class);
        mContext.stopService(i);
    }

    private Context mContext = null;
}
