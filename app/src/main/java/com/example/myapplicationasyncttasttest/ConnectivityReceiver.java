package com.example.myapplicationasyncttasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                String stateName = "";
                switch (info.getState()) {
//                    case CONNECTED:
//                        stateName = "connected";
//                        Log.i("ConnectivityReceiver", "--ConexionWifi---------"+stateName+"---------");
//                        envioNotificacion(context,stateName);
//                        break;

//                    case CONNECTING:
//                        stateName = "connecting";
//                        Log.i("ConnectivityReceiver", "--ConexionWifi---------"+stateName+"---------");
//                        break;

                    case DISCONNECTED:
                        stateName = "disconnected";
                        Log.i("ConnectivityReceiver", "--ConexionWifi---------"+stateName+"---------");
                        envioNotificacion(context,stateName);
                        break;

//                    case DISCONNECTING:
//                        stateName = "disconnecting";
//                        Log.i("ConnectivityReceiver", "--ConexionWifi---------"+stateName+"---------");
//                        break;

                    case SUSPENDED:
                        stateName = "suspended";
                        Log.i("ConnectivityReceiver", "--ConexionWifi---------"+stateName+"---------");
                        envioNotificacion(context,stateName);
                        break;

                    case UNKNOWN:
                        stateName = "unknown";
                        Log.i("ConnectivityReceiver", "--ConexionWifi---------"+stateName+"---------");
                        envioNotificacion(context,stateName);
                        break;
                }
        }
    }

    public void envioNotificacion (Context context, String stateName) {
        try {
            MainActivity.onSendNotificationsButtonClick("Calidad Recepcion WIFI", "Se detecto lo siguiente en WIFI:--"+stateName);
            Intent intentHome = new Intent(context, MainActivity.class);
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentHome);
        } catch (Exception e) {
            Log.i("Exception", "---------error excepcion---------"+e.toString());
        }
    }

}

