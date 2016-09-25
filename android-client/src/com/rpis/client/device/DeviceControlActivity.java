
package com.rpis.client.device;

import com.rpis.client.R;
import com.rpis.client.led.LedControlActivity;
import com.rpis.client.locator.LocatorActivity;
import com.rpis.client.nav.NavigationActivity;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.RpisResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeviceControlActivity extends Activity {
    private static final String TAG = LedControlActivity.class.getSimpleName();

    private static final int DELAY_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mServer = IRpisServer.Stub.asInterface(
                getIntent().getExtras().getBinder(NavigationActivity.EXTRA_KEY_SERVER));
        mService = IRpisService.Stub
                .asInterface(
                        getIntent().getExtras().getBinder(NavigationActivity.EXTRA_KEY_SERVICE));

        ((Button) findViewById(R.id.btnPowerOff)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAction("Shutdown device?", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mServer.getDeviceControl().shutdown(DELAY_MS) == RpisResult.OK) {
                                exit("", DELAY_MS);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ((Button) findViewById(R.id.btnReboot)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAction("Reboot device?", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mServer.getDeviceControl().reboot(DELAY_MS) == RpisResult.OK) {
                                exit("", DELAY_MS);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void exit(String message, int delayMs) {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LocatorActivity.start(DeviceControlActivity.this);
            }
        }, delayMs);
    }

    private void confirmAction(String message, final Runnable action) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        action.run();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private IRpisService mService;
    private IRpisServer mServer;
}
