
package com.rpis.locator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class LocatorActivity extends Activity {
    private static final int PORT = 13099;
    private static final String TAG = "RPISLocator";
    private static final String RPIS_LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";
    private static final String RPIS_LOCATOR_RESPONSE = "RPIS.PROVIDE.ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        new Thread(new Runnable() {
            @Override
            public void run() {
                locate();
            }
        }).start();
    }

    public void locate() {
        while (true) {
            try {
                sendBroadcast();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            String msg;
            try {
                msg = receive();
            } catch (SocketException e) {
                e.printStackTrace();
                continue;
            }

            if (msg == null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            if (msg.startsWith(RPIS_LOCATOR_RESPONSE)) {
                Log.d(TAG, "Response received: " + msg);
                String address = msg.substring(RPIS_LOCATOR_RESPONSE.length());

                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_KEY_URL, address);
                startActivity(intent);
                
                break;
            } else {
                Log.e(TAG, "Malformed message: " + msg);
                continue;
            }
        }
    }

    public String receive() throws SocketException {
        DatagramSocket socket = new DatagramSocket(PORT);
        socket.setReuseAddress(true);
        socket.setSoTimeout(1000);

        byte[] message = new byte[1500];
        DatagramPacket p = new DatagramPacket(message, message.length);

        try {
            socket.receive(p);
        } catch (IOException e) {
            socket.close();
            return null;
        }
        String text = new String(message, 0, p.getLength());

        socket.close();

        return text;
    }

    public void sendBroadcast() throws IOException {
        // Open a random port to send the package
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] sendData = RPIS_LOCATOR_REQUEST.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                getBroadcastAddress(), PORT);
        socket.send(sendPacket);

        socket.close();
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }

        return InetAddress.getByAddress(quads);
    }
}
