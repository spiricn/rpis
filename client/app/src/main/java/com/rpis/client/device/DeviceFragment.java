package com.rpis.client.device;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rpis.client.AControl;
import com.rpis.client.R;
import com.rpis.service.comm.IRpisService;


public class DeviceFragment extends AControl {
    public DeviceFragment(IRpisService service) {
        super(service);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        return view;
    }

    @Override
    public void onPageShow() {
        ((Button)getView().findViewById(R.id.btnDeviceReboot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAction("Reboot device?", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getServer().getDeviceControl().reboot(ACTION_DELAY_MS);
                        } catch (RemoteException e) {
                            // TODO
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ((Button)getView().findViewById(R.id.btnDeviceShutdown)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAction("Shutdown device?", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getServer().getDeviceControl().shutdown(ACTION_DELAY_MS);
                        } catch (RemoteException e) {
                            // TODO
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ((Button)getView().findViewById(R.id.btnDeviceStop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAction("Stop server ?", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getServer().getDeviceControl().stopServer(ACTION_DELAY_MS);
                        } catch (RemoteException e) {
                            // TODO
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    private void confirmAction(String message, final Runnable action){
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmation")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        action.run();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public String getName() {
        return "Device Control";
    }

    private static final int ACTION_DELAY_MS = 2000;
}
