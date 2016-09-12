
package com.rpis.client;

import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.ServiceLocator;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class LocatorActivity extends Activity {
    private static final String TAG = LocatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        mServiceLocator = new ServiceLocator();

        mService = mServiceLocator.connectBlocking(this);
        if (mService == null) {
            Log.e(TAG, "Error connecting to service");
            finish();
        }

        try {
            mService.scan(new IServerScanCallback.Stub() {
                @Override
                public boolean onServerFound(String address, int port) throws RemoteException {
                    // TODO
                    Log.d(TAG, "Found server: " + address + ":" + port);

                    // Intent intent = new Intent(this, WebActivity.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    // Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // intent.putExtra(WebActivity.EXTRA_KEY_URL, address);
                    // this.startActivity(intent);

                    return true;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            finish();
        }
    }

    private ServiceLocator mServiceLocator;
    private IRpisService mService;
}
