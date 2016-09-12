
package com.rpis.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import rest.RPISApi;

public class LedControlActivity extends Activity {
    private static final String TAG = LedControlActivity.class.getSimpleName();

    private RPISApi mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        mApi = new RPISApi("http://192.168.1.42:80/rest");

        ToggleButton btn1 = (ToggleButton) findViewById(R.id.toggleButton1);
        btn1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mApi.getStripAPI().powerOn();
                } else {
                    mApi.getStripAPI().powerOff();
                }
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
                updateColor();
            }
        };

        mHue = (SeekBar) findViewById(R.id.hue);
        mHue.setOnSeekBarChangeListener(seekListener);

        mSaturation = (SeekBar) findViewById(R.id.saturation);
        mSaturation.setOnSeekBarChangeListener(seekListener);

        mValue = (SeekBar) findViewById(R.id.value);
        mValue.setOnSeekBarChangeListener(seekListener);
    }

    private void updateColor() {
        float h = (float) mHue.getProgress() / mHue.getMax();
        float s = (float) mSaturation.getProgress() / mHue.getMax();
        float v = (float) mValue.getProgress() / mHue.getMax();

        mApi.getStripAPI().setColor(h, s, v);
    }

    private SeekBar mHue;
    private SeekBar mSaturation;
    private SeekBar mValue;

}
