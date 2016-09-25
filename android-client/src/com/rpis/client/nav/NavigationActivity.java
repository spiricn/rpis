
package com.rpis.client.nav;

import com.rpis.client.R;
import com.rpis.client.device.DeviceControlActivity;
import com.rpis.client.led.LedControlActivity;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NavigationActivity extends Activity {
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        NavigationActivity.super.onBackPressed();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit the application?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static final String EXTRA_KEY_SERVER = "server";
    public static final String EXTRA_KEY_SERVICE = "service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        mServer = IRpisServer.Stub.asInterface(getIntent().getExtras().getBinder(EXTRA_KEY_SERVER));
        mService = IRpisService.Stub
                .asInterface(getIntent().getExtras().getBinder(EXTRA_KEY_SERVICE));

        mListView = (ListView) findViewById(R.id.listview);
        final NavigationMenuItem[] values = new NavigationMenuItem[] {
                new NavigationMenuItem("Led Control", LedControlActivity.class),
                new NavigationMenuItem("Device Control", DeviceControlActivity.class),
        };

        mListView.setAdapter(new NavigationListAdapter(this, values));
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                NavigationMenuItem activity = values[position];

                Intent intent = new Intent(NavigationActivity.this, activity.clazz);
                Bundle bundle = new Bundle();

                bundle.putBinder(NavigationActivity.EXTRA_KEY_SERVICE, mService.asBinder());
                bundle.putBinder(NavigationActivity.EXTRA_KEY_SERVER, mServer.asBinder());

                intent.putExtras(bundle);
                NavigationActivity.this.startActivity(intent);
            }
        });
    }

    private ListView mListView;
    private IRpisService mService;
    private IRpisServer mServer;

}
