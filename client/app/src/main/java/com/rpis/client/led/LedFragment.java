
package com.rpis.client.led;

import com.rpis.client.AControl;
import com.rpis.client.ATab;
import com.rpis.client.R;
import com.rpis.client.locator.ServerListItem;
import com.rpis.service.comm.Color;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.Prefab;
import com.rpis.service.comm.RpisResult;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class LedFragment extends AControl {
    private static final String TAG = LedFragment.class.getSimpleName();

    public LedFragment(IRpisService service) {
        super(service);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_led, container, false);

        return view;
    }

    @Override
    public void onPageShow() {
        mPowerToggle = (ToggleButton) getView().findViewById(R.id.toggleButton1);
        mPowerToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                setPower();
            }
        });

        SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
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

        mHue = (SeekBar) getView().findViewById(R.id.hue);
        mHue.setOnSeekBarChangeListener(seekListener);

        mSaturation = (SeekBar) getView().findViewById(R.id.saturation);
        mSaturation.setOnSeekBarChangeListener(seekListener);

        mValue = (SeekBar) getView().findViewById(R.id.value);
        mValue.setOnSeekBarChangeListener(seekListener);

        mSurface = (SurfaceView) getView().findViewById(R.id.surfaceView);


        mPrefabsList = (ListView)getView().findViewById(R.id.prefabsList);
        mPrefabsList.setAdapter(mPrefabsAdapter = new PrefabsListAdapter(getContext(), new ArrayList<PrefabsListItem>()));
        mPrefabsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PrefabsListItem item = (PrefabsListItem)adapterView.getItemAtPosition(i);

                try {
                    getServer().getLedControl().runPrefab(item.prefab.getId());

                    if(mColorUpdater != null) {
                        mColorUpdater.cancel();
                        mColorUpdater = null;
                    }

                    mColorUpdater = new Timer();

                    mColorUpdater.scheduleAtFixedRate(new TimerTask(){
                        @Override
                        public void run() {
                            LedFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUi();
                                }
                            });
                        }
                    }, 0, 1000);

                } catch (RemoteException e) {
                    // TODo
                    e.printStackTrace();
                }
            }
        });

        ((Button)getView().findViewById(R.id.btnStopPrefab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mColorUpdater != null) {
                    mColorUpdater.cancel();
                    mColorUpdater = null;
                }

                try {
                    getServer().getLedControl().stopPrefab();
                } catch (RemoteException e) {
                    // TODO
                    e.printStackTrace();
                }
            }
        });

        updateUi();
    }

    private void setPower() {
        if (mUiUpdating) {
            return;
        }
        RpisResult res = RpisResult.ERROR;

        try {
            if (mPowerToggle.isChecked()) {
                res = getServer().getLedControl().powerOn();
            } else {
                res = getServer().getLedControl().powerOff();
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
            res = getServer().getLedControl().setColor(color);

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
            currColor = getServer().getLedControl().getColor();
            poweredOn = getServer().getLedControl().isPoweredOn();
            Log.d(TAG, "Current power state: " + poweredOn);

            if (currColor == null) {
                Log.e(TAG, "Error getting current color");
                return;
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

        updateUi(poweredOn, currColor);

        Prefab[] prefabs = new Prefab[0];

        try {
            prefabs = getServer().getLedControl().getPrefabs();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mPrefabsAdapter.clear();
        for(Prefab prefab : prefabs){
            mPrefabsAdapter.insert(new PrefabsListItem(prefab), mPrefabsAdapter.getCount());
        }
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
    private ListView mPrefabsList;
    private PrefabsListAdapter mPrefabsAdapter;
    private Timer mColorUpdater;

    private boolean mUiUpdating = false;

    @Override
    public String getName() {
        return "Led Control";
    }
}
