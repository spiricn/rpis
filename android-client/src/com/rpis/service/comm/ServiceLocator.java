
package com.rpis.service.comm;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

public class ServiceLocator {
    public interface IListener {
        void onServiceConnected(IRpisService service);

        void onServiceDisconnected();
    }

    public IRpisService connectBlocking(Context context, IListener listener, long timeoutMs) {
        if (!connect(context, listener)) {
            return null;
        }

        try {
            if (timeoutMs > 0) {
                if (!mSemaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                    return null;
                }
            } else {
                mSemaphore.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        if (mState != State.CONNECTED || mService == null) {
            return null;
        }

        return mService;
    }

    public boolean connect(Context context, IListener listener) {
        synchronized (mState) {
            if (mState != State.DISCONNECTED) {
                Log.e(TAG, "Invalid state " + mState + " (expected " + State.DISCONNECTED + ")");
                return false;
            }

            mState = State.CONNECTING;
            mListener = listener;
            mContext = context;

            bind();
        }
        return true;
    }

    public IRpisService connectBlocking(Context context) {
        return connectBlocking(context, null, TIMEOUT_INFINITE);
    }

    public IRpisService connectBlocking(Context context, long timeoutMs) {
        return connectBlocking(context, null, timeoutMs);
    }

    public IRpisService connectBlocking(Context context, IListener listener) {
        return connectBlocking(context, listener, TIMEOUT_INFINITE);
    }

    public void disconnect() {
        synchronized (mState) {
            if (mState != State.CONNECTED) {
                Log.e(TAG, "Invalid state " + mState + " (expected " + State.CONNECTED + ")");
                return;
            }

            if (mServiceConnection != null) {
                mContext.unbindService(mServiceConnection);
            }

            mState = State.DISCONNECTED;
        }
    }

    public IRpisService getService() {
        return mService;
    }

    private void bind() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName arg0, IBinder service) {
                mService = IRpisService.Stub.asInterface(service);

                if (mListener != null) {
                    mListener.onServiceConnected(mService);
                }
                mState = State.CONNECTED;

                Log.d(TAG, "Connected to RPIS service");

                mSemaphore.release();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                Log.d(TAG, "Disconnected from RPIS service");
                mService = null;
                if (mListener != null) {
                    mListener.onServiceDisconnected();
                }
                mState = State.DISCONNECTED;
            }
        };

        Intent explicitIntent = createExplicitFromImplicitIntent(mContext,
                new Intent(SERVICE_INTENT));
        mContext.bindService(explicitIntent, mServiceConnection, 0);
    }

    private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    private enum State {
        DISCONNECTED, CONNECTING, CONNECTED
    }

    private static final String SERVICE_INTENT = "com.rpis.RpisService.START_SERVICE";
    private static final String TAG = ServiceLocator.class.getSimpleName();
    private static final int WAIT_TIME_MS = 500;
    private static final long TIMEOUT_INFINITE = -1;

    private State mState = State.DISCONNECTED;
    private ServiceConnection mServiceConnection = null;
    private IListener mListener = null;
    private Context mContext = null;
    private Semaphore mSemaphore = new Semaphore(0);
    private IRpisService mService = null;
}
