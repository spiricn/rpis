
package com.rpis.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LocatorActivity extends Activity implements Locator.ILocatorListener {
    private static final String TAG = LocatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        mLocator = new Locator(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAddress == null) {
            mLocator.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAddress == null) {
            mLocator.start();
        }
    }

    @Override
    public void onAddressFound(String address) {
        mAddress = address;

        finish();

        Intent intent = new Intent(this, WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(WebActivity.EXTRA_KEY_URL, address);
        this.startActivity(intent);
    }

    private Locator mLocator;
    private String mAddress = null;
}
