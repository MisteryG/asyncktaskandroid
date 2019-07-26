package com.example.myapplicationasyncttasttest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LostReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                Log.i("ConexionWifi", "-------correcta-----------------");
            } else {
                Log.i("ConexionWifi", "-------erronea-----------------");
            }
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch(state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.i("ConexionBluetooth", "-------STATE_OFF-----------------");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.i("ConexionBluetooth", "-------STATE_TURNING_OFF-----------------");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.i("ConexionBluetooth", "-------STATE_ON-----------------");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.i("ConexionBluetooth", "-------STATE_TURNING_ON-----------------");
                    break;
            }

        }
    }
}
