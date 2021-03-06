
package com.rpis.service.comm;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.rpis.service.RpisService;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
        
        Log.d(TAG, "" + mState + " " + mService);

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
                Log.d(TAG, "Connected to RPIS service");

                mService = IRpisService.Stub.asInterface(service);

                if (mListener != null) {
                    mListener.onServiceConnected(mService);
                }
                mState = State.CONNECTED;

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

        Log.d(TAG, "Connecting to service ..");
        mContext.bindService(new Intent(mContext, RpisService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private enum State {
        DISCONNECTED, CONNECTING, CONNECTED
    }

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
