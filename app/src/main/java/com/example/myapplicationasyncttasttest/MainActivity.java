package com.example.myapplicationasyncttasttest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.Gravity.CENTER;

public class MainActivity extends Activity {
//    String dominio = "http://192.168.1.99:8080"; // Beto ip
    String dominio = "http://pandora.databv4.com:9743"; // Beto ip
//    Boolean socketisConnect = false;
    JSONObject prueba = new JSONObject();
    PrinterBluetooh printerBluetooh = new PrinterBluetooh();
    String nameDevice="PR2-886B0FAE4351";
    LostReceiver lostReceiver = new LostReceiver();
    BluetoothLostReceiver bluetoothLostReceiver = new BluetoothLostReceiver();
    IntentFilter intentFilterReceiver = new IntentFilter();
    IntentFilter intentFilterBluetooth = new IntentFilter();

    public Socket mSocket;
    {
        IO.Options opts = new IO.Options();
        opts.forceNew = false;
        opts.reconnection = true;
        opts.timeout = 5000;

        try {
            mSocket = IO.socket(dominio,opts);
        } catch (URISyntaxException e) {
            Log.i("error_____________ ", e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentFilterReceiver.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilterReceiver.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(lostReceiver, intentFilterReceiver);
        intentFilterBluetooth.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilterBluetooth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothLostReceiver,intentFilterBluetooth);
        try {
            prueba.put("action", "login");
            prueba.put("UserId", "prueba");
            prueba.put("DeviceId", "HH13");
            prueba.put("idCEDIS", "art");
        } catch (JSONException ex) {
            Log.i("error_____________ ", ex.toString());
        }
        mSocket.on("connect", onConnect);
        mSocket.connect();
        Log.i("_____________>", "algo  ----------------");
        try {
            printerBluetooh.findBT(nameDevice);
            printerBluetooh.openBT();
            if (printerBluetooh.activado) {
                Toast.makeText(getApplicationContext(), "Conexion establecida !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error al conectar con el dispositivo", Toast.LENGTH_LONG).show();;
            }
        } catch (Exception e) {
            Log.i("error_____________ ", e.toString());
        }
        new ProcessTask().execute();
    }

    @Override
    protected void onRestart () {
        super.onRestart();
        try {
            printerBluetooh.closeBT();
            printerBluetooh.findBT(nameDevice);
            printerBluetooh.openBT();
            if (printerBluetooh.activado) {
                Toast.makeText(getApplicationContext(), "Conexion establecida !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error al conectar con el dispositivo", Toast.LENGTH_LONG).show();;
            }
        } catch (Exception e) {
            Log.i("error_____________ ", e.toString());
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            unregisterReceiver(lostReceiver);
        } catch (Exception e) {
            Log.i("error_____________ ", e.toString());
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("--->", "******* CONECTADO ******** ");
                    Toast tm = Toast.makeText(getApplicationContext(), "Se ha conectado con el servicio...", Toast.LENGTH_LONG);
                    tm.setGravity(CENTER, 0, 0);
                    tm.show();
                    mSocket.emit("connected", prueba);
                }
            });
        }
    };

    private Emitter.Listener onNewConnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                mensaje(data);
                if (data.getString("action").equals("imprimir")) {
                    if (printerBluetooh.sendData(data.getString("LP"))) {
                        Toast.makeText(getApplicationContext(), "Impresion Correcta", Toast.LENGTH_LONG).show();;
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo imprimir", Toast.LENGTH_LONG).show();;
                    }
                }
            } catch (Exception e) {
                Log.i("===>", "error  " + e);
            }
        }
    };

    public void mensaje( final JSONObject data) {
        runOnUiThread(new Runnable() {
            public void run () {
            Toast tm = Toast.makeText(getApplicationContext(), "llego un dato --> "+data, Toast.LENGTH_LONG);
                tm.setGravity(CENTER,0,0);
                tm.show();}
        });
    }

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("------------------>", "desconectado ");
                    Toast toast = Toast.makeText(getApplicationContext(), "Se perdió la conexion", Toast.LENGTH_LONG);
                    toast.setGravity(CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    };


    public class ProcessTask extends android.os.AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
//            Log.i("===>", "Dentro de doInBackground");
            mSocket.on("messages", onNewConnection);
            mSocket.on("disconnect", onDisconnect);
            return null;
        }
    }
}
