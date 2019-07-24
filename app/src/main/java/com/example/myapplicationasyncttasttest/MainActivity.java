package com.example.myapplicationasyncttasttest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;
import static android.view.Gravity.CENTER;

public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    String dominio = "http://192.168.1.99:8080"; // Beto ip
    ProcessTask PT;
    // variables necesarias
    String action;
    String iduser;
    String idhh;// hombre de la hh
    String ticket;
    String valorCedis = "";
    String valorDevice = "";// nombre de la impresora
    String valoruser = "";
    String usuario = "";
    String devicetext = "";
    JSONObject settings;

    public Socket mSocket;
    {
        IO.Options opts = new IO.Options();
        opts.forceNew = false;
        opts.reconnection = true;
        opts.timeout = 5000;

        try {
            mSocket = IO.socket(dominio);
        } catch (URISyntaxException e) {
            Log.i("error_____________ ", e.toString());
        }
    }

    public JSONObject leerSettings() {
        JSONObject obj = new JSONObject();
        try {
            String filename = "config";
            FileOperations fop = new FileOperations();
            String text = fop.read(filename);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pedir autorización para leer memoria interna
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        settings = leerSettings();

        mSocket.emit("connected");
        mSocket.connect();
        PT = new ProcessTask();
        PT.execute();
    }

    protected void onStart () {
        Log.i("_____________>", "on start");
        super.onStart();
    }

    protected void onRestart (){
        Log.i("_____________>", "on restart");
        super.onRestart();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        JSONObject data = (JSONObject) args[0];
                        Log.i("===> 1","data  "+data);
                    }catch(Exception e){
                        Log.i("===> 2","error  "+e);
                    }
                }
            });
        }
    };
//    private Listener onConnect = new Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    socketisConnect = true;
//                    Log.i("--->", "*************** CONECTADO ************ " + socketisConnect);
//                    Toast tm = Toast.makeText(getApplicationContext(), "Se ha conectado con el servicio...", Toast.LENGTH_LONG);
//                    tm.setGravity(CENTER, 0, 0);
//                    tm.show();
//
//                    mSocket.emit("messages", "Yo merengues");
//                    Log.i("_____________>","algo"+mSocket.connect());
//                }
//            });
//        }
//    };


    public class ProcessTask extends android.os.AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            mSocket.on("messages", onNewMessage);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void disconnect() {
            try {
                Log.d(TAG, "Closing the socket connection.");

            } catch (Exception ex) {
                Log.e(TAG, "disconnect(): " + ex.toString());
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

}
