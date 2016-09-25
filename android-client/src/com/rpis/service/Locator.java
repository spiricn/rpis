
package com.rpis.service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import org.json.JSONException;
import org.json.JSONObject;

import com.rpis.service.comm.ServerInfo;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Locator {
    private static final String TAG = "RPISLocator";
    private static final int BROADCAST_PORT = 13099;
    private static final int RESPONSE_PORT = BROADCAST_PORT - 1;
    private static final String RPIS_LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";
    private static final int CYCLE_TIMEOUT_MS = 1000;

    public Locator(Context context) {
        mContext = context;
        setState(State.Idle);
    }

    public ServerInfo findServer() {
        if (!broadcastRequest()) {
            return null;
        }

        return receiveResponse();
    }

    private boolean broadcastRequest() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);

        } catch (SocketException e2) {
            e2.printStackTrace();
            return false;
        }

        byte[] sendData = RPIS_LOCATOR_REQUEST.getBytes();
        DatagramPacket sendPacket;
        try {
            sendPacket = new DatagramPacket(sendData, sendData.length,
                    getBroadcastAddress(), BROADCAST_PORT);
            socket.send(sendPacket);

            Log.d(TAG, "Broadcast sent to " + getBroadcastAddress() + ":" + BROADCAST_PORT);

        } catch (IOException e) {
            e.printStackTrace();
            socket.close();
            return false;
        }

        socket.close();

        return true;
    }

    private ServerInfo receiveResponse() {
        String msg = receiveMessage();
        if (msg == null) {
            Log.e(TAG, "Error receiving message");
            return null;
        }

        try {
            JSONObject obj = new JSONObject(msg);

            return new ServerInfo(obj.getString("name"),
                    obj.getString("version"),
                    obj.getString("address"),
                    obj.getString("rest"));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String receiveMessage() {
        byte[] message = new byte[1500];
        DatagramPacket p = new DatagramPacket(message, message.length);

        DatagramSocket socket;
        try {
            socket = new DatagramSocket(RESPONSE_PORT);
            socket.setReuseAddress(true);
            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }

        try {
            socket.receive(p);
        } catch (SocketTimeoutException timeoutException) {
            socket.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            socket.close();
            return null;
        }

        socket.close();

        return new String(message, 0, p.getLength());
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

    private State mState = State.Invalid;
    private Context mContext;
}
