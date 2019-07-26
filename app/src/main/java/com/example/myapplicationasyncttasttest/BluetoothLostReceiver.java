package com.example.myapplicationasyncttasttest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class BluetoothLostReceiver extends BroadcastReceiver {

//    MainActivity main = null;
//
//    public void setMainActivity(MainActivity main)
//    {
//        this.main = main;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action){
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.i("ConexionDispositivoBT", "-------ACTION_ACL_CONNECTED-----------------");
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Log.i("ConexionDispositivoBT", "-------ACTION_ACL_DISCONNECTED-----------------");
//                try {
//                    main.conexionBluetooth();
//                } catch (Exception e){
//                    Log.i("ConexionDispositivoBT", "-------Valio-----------------");
//                }
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
