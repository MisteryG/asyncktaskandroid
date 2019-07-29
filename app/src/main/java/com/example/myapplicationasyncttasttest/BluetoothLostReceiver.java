package com.example.myapplicationasyncttasttest;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothLostReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action){
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.i("ConexionDispositivoBT", "-------ACTION_ACL_CONNECTED-----------------");
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Log.i("ConexionDispositivoBT", "-------ACTION_ACL_DISCONNECTED-----------------");
                try {
                    MainActivity.conexionBluetooth();
                } catch (Exception e){
                    Log.i("ConexionDispositivoBT", "-------Valio-----------------");
                }
                break;
            case BluetoothDevice.ACTION_CLASS_CHANGED:
                Log.i("ConexionDispositivoBT", "-------ACTION_CLASS_CHANGED-----------------");
                break;
            case BluetoothDevice.ACTION_FOUND:
                Log.i("ConexionDispositivoBT", "-------ACTION_CLASS_CHANGED-----------------");
                break;
        }
    }
}
