package com.example.myapplicationasyncttasttest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.Socket;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.Gravity.CENTER;

public class MainActivity extends Activity {

    private static MainActivity myContext;
    public MainActivity(){
        myContext = this;
    }
    public static MainActivity getInstance() {
        return myContext;
    }


//    String dominio = "http://192.168.1.99:8080"; // Beto ip
    String dominio = "http://pandora.databv4.com:9743"; // Desarrollo ip
    JSONObject settings = new JSONObject();
    String filename = "config".toString();
    static PrinterBluetooh printerBluetooh = new PrinterBluetooh();
//    static String nameDevice="PR2-886B0FAE4351";
    LostReceiver lostReceiver = new LostReceiver();
    BluetoothLostReceiver bluetoothLostReceiver = new BluetoothLostReceiver();
    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    IntentFilter intentFilterReceiver = new IntentFilter();
    IntentFilter intentFilterBluetooth = new IntentFilter();
    IntentFilter intentFilterConnectivity = new IntentFilter();

    //variables generales
    String valorCedis = "";
    String valorDevice = "";// nombre de la impresora
    String valoruser = "";
    String usuario = "";
    String devicetext = "";

    public Socket mSocket;
    {
        IO.Options opts = new IO.Options();
        opts.forceNew = false;
        opts.reconnection = true;
        opts.reconnectionAttempts = 999999;
        opts.reconnectionDelay = 2000;
        opts.reconnectionDelayMax = 4000;
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

        WifiManager wmgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);

        try {
            conexionBluetooth();
        } catch (Exception e) {
            Log.i("error_____________ ", e.toString());
        }

        settings=leerSettings();

        intentFilterReceiver.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilterReceiver.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(lostReceiver, intentFilterReceiver);
        intentFilterBluetooth.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilterBluetooth.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilterBluetooth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilterBluetooth.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        registerReceiver(bluetoothLostReceiver,intentFilterBluetooth);
        intentFilterConnectivity.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilterConnectivity.addAction(WifiManager.EXTRA_NETWORK_INFO);
        registerReceiver(connectivityReceiver,intentFilterConnectivity);
        new ProcessTask().execute();
        mSocket.connect();
    }

    public static void conexionBluetooth () throws IOException {
        Integer loop = 0;
        while (loop<10){
            Log.i("conteo", "------------------"+loop);
            try {
//                printerBluetooh.findBT(nameDevice);
                printerBluetooh.findBT();
                printerBluetooh.openBT();
                if (printerBluetooh.activado) {
                    Log.i("ConexionBT", "---------lograda------------");
                    break;
                } else {
//                    printerBluetooh.closeBT();
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (Exception e) {
                Log.i("error_____________ ", e.toString());
            }
            loop++;
        }
    }

    public JSONObject leerSettings() {
        JSONObject obj = new JSONObject();
        try {
            String readfilename = filename;
            FileOperations fop = new FileOperations();
            String text = fop.read(readfilename);
            Log.i("valor de cadena txt-->", text);
            JSONObject jsonObject = new JSONObject(text);

            if (text != null) {
                valorCedis = jsonObject.getString("Cedis");
                valorDevice = jsonObject.getString("Device");
                valoruser = jsonObject.getString("usuario");
                usuario = valoruser.toLowerCase();
                devicetext = valorDevice.toLowerCase();
                Log.i("valor de json  txt-->", "" + jsonObject);
                Log.i("lectura del archivo->", text);
            } else if (text == null) {
                Toast.makeText(getApplicationContext(), "No se encontró el archivo. Vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                Log.i("--->Files content: ", "no se encontro el archivo");
            } else {
                Toast.makeText(getApplicationContext(), "Problemas con el archivo. Vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
            }

            obj.put("action", "login");
            obj.put("UserId", valoruser);
            obj.put("DeviceId", valorDevice);
            obj.put("idCEDIS", valorCedis);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public static void onSendNotificationsButtonClick(String titulo, String aviso) throws IOException {
        Log.i("===>", "titulo------------" + titulo);
        Log.i("===>", "aviso-------------" + aviso);
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.myContext);
        dialogo.setTitle(""+titulo);
        dialogo.setMessage(""+aviso);
        dialogo.setCancelable(false);
        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
        dialogo.show();
    }

    @Override
    protected void onRestart () {
        super.onRestart();
    }

    @Override
    protected void onStart () { super.onStart(); }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            unregisterReceiver(lostReceiver);
            unregisterReceiver(bluetoothLostReceiver);
            unregisterReceiver(connectivityReceiver);
            printerBluetooh.closeBT();
            mSocket.close();
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
                    mSocket.emit("connected", settings);
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

//        @Override
//        protected void onPreExecute() {
//        }

        @Override
        protected Void doInBackground(Void... params) {
//            Log.i("===>", "Dentro de doInBackground");
            mSocket.on("messages", onNewConnection);
            mSocket.on("disconnect", onDisconnect);
            mSocket.on("connect", onConnect);
            return null;
        }
    }
}
