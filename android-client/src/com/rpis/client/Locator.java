
package com.rpis.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class Locator {
    public interface ILocatorListener {
        void onAddressFound(String address);
    }

    private static final String TAG = "RPISLocator";
    private static final int BROADCAST_PORT = 13099;
    private static final int RESPONSE_PORT = BROADCAST_PORT - 1;
    private static final String RPIS_LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";
    private static final String RPIS_LOCATOR_RESPONSE = "RPIS.PROVIDE.ADDRESS";
    private static final int CYCLE_TIMEOUT_MS = 1000;

    public Locator(Context context, ILocatorListener listener) {
        mContext = context;
        mListener = listener;
        mHandler = new Handler(context.getMainLooper());
        setState(State.Idle);
    }

    public void stop() {
        Log.d(TAG, "Stopping location ..");
        synchronized (mState) {
            if (mState != State.Locating) {
                Log.e(TAG, "Invalid state " + mState);
                return;
            }

            mReceiverThreadRunning = false;
            try {
                mListenerSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                mReceiverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mSenderSocket.close();
            mSenderThreadRunning = false;
            try {
                mSenderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setState(State.Idle);
            Log.d(TAG, "Location stopped");
        }
    }

    public void start() {
        Log.d(TAG, "Staring location ..");

        synchronized (mState) {
            if (mState != State.Idle) {
                Log.e(TAG, "Invalid state " + mState);
                return;
            }

            setState(State.Locating);

            mSenderThreadRunning = true;
            mSenderThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    senderThread();
                }
            });

            mReceiverThreadRunning = true;
            mReceiverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        receiverThread();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSenderThread.start();
            mReceiverThread.start();

            Log.d(TAG, "Location started");
        }
    }

    private void senderThread() {
        Log.d(TAG, "Starting sender thread");

        try {
            mSenderSocket = new DatagramSocket();
            mSenderSocket.setBroadcast(true);

        } catch (SocketException e2) {
            e2.printStackTrace();
            return;
        }

        boolean firstIter = true;
        while (mSenderThreadRunning) {
            if (!firstIter) {
                try {
                    Thread.sleep(CYCLE_TIMEOUT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
            } else {
                firstIter = false;
            }

            try {
                sendBroadcast();
            } catch (IOException e1) {
                continue;
            }
        }

        mSenderSocket.close();
        Log.d(TAG, "Sender thread finished");
    }

    private void receiverThread() throws UnknownHostException {
        try {
            mListenerSocket = new ServerSocket(RESPONSE_PORT, 0, getLocalAddress());
            mListenerSocket.setReuseAddress(true);

            Log.d(TAG, "Started TCP server on " + mListenerSocket.getInetAddress().getHostAddress()
                    + ":" + mListenerSocket.getLocalPort());

        } catch (IOException e) {
            Log.e(TAG, "Error creating listener socket: " + e);
            e.printStackTrace();
            return;
        }

        boolean firstIter = true;
        while (mReceiverThreadRunning) {
            if (!firstIter) {
                try {
                    Thread.sleep(CYCLE_TIMEOUT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
            } else {
                firstIter = false;
            }

            final String msg = receive();
            if (msg == null) {
                Log.e(TAG, "Error receiving message");
                continue;
            }

            if (msg.startsWith(RPIS_LOCATOR_RESPONSE)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onAddressLocated(msg.substring(RPIS_LOCATOR_RESPONSE.length()));
                    }
                });

                break;
            } else {
                Log.e(TAG, "Malformed message: " + msg);
                continue;
            }
        }

        try {
            mListenerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Receiver thread finished");

    }

    private void onAddressLocated(String address) {
        Log.d(TAG, "Address located");
        stop();
        mListener.onAddressFound(address);
    }

    private String receive() {
        Socket socket = null;
        try {
            Log.d(TAG, "Waiting for connection ..");
            socket = mListenerSocket.accept();
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "Client connected, waiting for response ..");

        byte[] message = new byte[1024];

        try {
            socket.getInputStream().read(message);
        } catch (IOException e) {
            Log.e(TAG, "Exception ocurred while receiving message: " + e);
            e.printStackTrace();
            return null;
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(message, 0, message.length);
    }

    private void sendBroadcast() throws IOException {
        byte[] sendData = RPIS_LOCATOR_REQUEST.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                getBroadcastAddress(), BROADCAST_PORT);
        mSenderSocket.send(sendPacket);
    }

    private InetAddress getLocalAddress() throws UnknownHostException {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        int ipAddress = wifiInfo.getIpAddress();

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        return InetAddress.getByAddress(ipByteArray);
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }

        return InetAddress.getByAddress(quads);
    }

    private void setState(State state) {
        Log.d(TAG, "Changing state: " + mState + " -> " + state);

        mState = state;
    }

    private enum State {
        Invalid, Idle, Locating
    }

    private DatagramSocket mSenderSocket;
    private ServerSocket mListenerSocket;
    private boolean mSenderThreadRunning;
    private boolean mReceiverThreadRunning;
    private Thread mReceiverThread;
    private Thread mSenderThread;
    private Handler mHandler;
    private State mState = State.Invalid;
    private Context mContext;
    private ILocatorListener mListener;
}
