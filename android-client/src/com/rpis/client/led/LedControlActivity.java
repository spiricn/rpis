
package com.rpis.client.led;

import com.rpis.client.R;
import com.rpis.client.nav.NavigationActivity;
import com.rpis.service.comm.Color;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.RpisResult;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class LedControlActivity extends Activity {
    private static final String TAG = LedControlActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        mServer = IRpisServer.Stub.asInterface(
                getIntent().getExtras().getBinder(NavigationActivity.EXTRA_KEY_SERVER));
        mService = IRpisService.Stub
                .asInterface(
                        getIntent().getExtras().getBinder(NavigationActivity.EXTRA_KEY_SERVICE));

        mPowerToggle = (ToggleButton) findViewById(R.id.toggleButton1);
        mPowerToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                setPower();
            }
        });

        OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                setColor();
            }
        };

        mHue = (SeekBar) findViewById(R.id.hue);
        mHue.setOnSeekBarChangeListener(seekListener);

        mSaturation = (SeekBar) findViewById(R.id.saturation);
        mSaturation.setOnSeekBarChangeListener(seekListener);

        mValue = (SeekBar) findViewById(R.id.value);
        mValue.setOnSeekBarChangeListener(seekListener);

        mSurface = (SurfaceView) findViewById(R.id.surfaceView);

        updateUi();
    }

    private void setPower() {
        if (mUiUpdating) {
            return;
        }
        RpisResult res = RpisResult.ERROR;

        try {
            if (mPowerToggle.isChecked()) {
                res = mServer.getLedControl().powerOn();
            } else {
                res = mServer.getLedControl().powerOff();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (res == RpisResult.ERROR) {
            Log.e(TAG, "Power command failed");
            return;
        }

        updateUi();
    }

    private void setColor() {
        if (mUiUpdating) {
            return;
        }

        RpisResult res = RpisResult.ERROR;

        float h = (float) mHue.getProgress() / mHue.getMax();
        float s = (float) mSaturation.getProgress() / mHue.getMax();
        float v = (float) mValue.getProgress() / mHue.getMax();
        Color color = new Color(h, s, v);

        try {
            res = mServer.getLedControl().setColor(color);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (res == RpisResult.ERROR) {
            Log.e(TAG, "setColor failed");
            return;
        }
        
        updateUi(null, color);
    }

    private void updateUi() {
        Color currColor = null;
        boolean poweredOn = false;

        try {
            currColor = mServer.getLedControl().getColor();
            poweredOn = mServer.getLedControl().isPoweredOn();
            Log.d(TAG, "Current power state: " + poweredOn);

            if (currColor == null) {
                Log.e(TAG, "Error getting current color");
                return;
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

        updateUi(poweredOn, currColor);
    }

    private void updateUi(Boolean poweredOn, Color currColor) {
        mUiUpdating = true;

        if (currColor != null) {
            int hue = (int) (currColor.getHue() * 100);
            int saturation = (int) (currColor.getSaturation() * 100);
            int value = (int) (currColor.getValue() * 100);

            if (currColor != null) {
                mHue.setProgress(hue);
                mSaturation.setProgress(saturation);
                mValue.setProgress(value);

                mSurface.setBackgroundColor(currColor.getRGB());
            }
        }

        if (poweredOn != null) {
            mHue.setEnabled(poweredOn);
            mSaturation.setEnabled(poweredOn);
            mValue.setEnabled(poweredOn);

            mPowerToggle.setChecked(poweredOn);
        }

        mUiUpdating = false;
    }

    private SeekBar mHue;
    private SeekBar mSaturation;
    private SeekBar mValue;
    private SurfaceView mSurface;
    private ToggleButton mPowerToggle;

    private IRpisService mService;
    private IRpisServer mServer;
    private boolean mUiUpdating = false;

}
