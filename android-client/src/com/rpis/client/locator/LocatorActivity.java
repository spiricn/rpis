
package com.rpis.client.locator;

import com.rpis.client.R;
import com.rpis.client.nav.NavigationActivity;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.ServerInfo;
import com.rpis.service.comm.ServiceLocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class LocatorActivity extends Activity {
    private static final String TAG = LocatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mServiceLocator = new ServiceLocator();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mService = mServiceLocator.connectBlocking(LocatorActivity.this, 2000);
                if (mService == null) {
                    Toast.makeText(LocatorActivity.this, "Could not connect to RPIS service",
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                LocatorActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        start();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        mServiceLocator.disconnect();

        super.onDestroy();
    }

    public static void start(Activity previous) {
        Intent intent = new Intent(previous, LocatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        previous.startActivity(intent);
        previous.finish();
    }

    private void start() {
        setContentView(R.layout.activity_locator);

        try {
            mService.scan(new IServerScanCallback.Stub() {
                @Override
                public boolean onServerFound(ServerInfo server) throws RemoteException {
                    Log.d(TAG, "Found server: " + server);

                    Intent intent = new Intent(LocatorActivity.this, NavigationActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putBinder(NavigationActivity.EXTRA_KEY_SERVICE, mService.asBinder());
                    bundle.putBinder(NavigationActivity.EXTRA_KEY_SERVER,
                            mService.connect(server).asBinder());

                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    LocatorActivity.this.startActivity(intent);

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
