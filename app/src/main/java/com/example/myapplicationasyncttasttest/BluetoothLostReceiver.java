package com.example.myapplicationasyncttasttest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

public class BluetoothLostReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action){
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.i("ConexionDispositivoBT", "-------correcta-----------------");
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Log.i("ConexionDispositivoBT", "-------erronea-----------------");
                break;
        }
    }
}
