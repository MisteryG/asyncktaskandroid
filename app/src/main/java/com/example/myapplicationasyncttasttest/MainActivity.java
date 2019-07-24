package com.example.myapplicationasyncttasttest;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
    String dominio = "http://192.168.1.99:8080"; // Beto ip
    //String dominio = "http://pandora.databv4.com:9743"; // Beto ip
    Boolean socketisConnect = false;
    JSONObject prueba = new JSONObject();

    TextView txtResul;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            prueba.put("action", "login");
            prueba.put("UserId", "prueba");
            prueba.put("DeviceId", "HH13");
            prueba.put("idCEDIS", "art");
        } catch (JSONException ex) {
            Log.i("error_____________ ", ex.toString());
        }
        txtResul = (TextView) findViewById(R.id.txtResultado);
        Log.i("_____________>", "algo  " + mSocket.connect());
        mSocket.emit("connected",prueba);
        mSocket.connect();
        new ProcessTask().execute();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                txtResul.setText("resul" + data.toString());
                mensaje(data);
                Log.i("===>", "data  " + data);
            } catch (Exception e) {
                Log.i("===>", "error  " + e);
            }
        }
    };

    public void mensaje( final JSONObject data) {
        runOnUiThread(new Runnable() {
            public void run () {
            Toast tm = Toast.makeText(getApplicationContext(), "llego un dato ese --> "+data, Toast.LENGTH_LONG);
                tm.setGravity(CENTER,0,0);
                tm.show();}
        });
    }

//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try{
//                        JSONObject data = (JSONObject) args[0];
//                        Log.i("===>","data  "+data);
//                    }catch(Exception e){
//                        Log.i("===>","error  "+e);
//                    }
//                }
//            });
//        }
//    };


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
//            mSocket.emit("connected");
//            mSocket.connect();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("===>", "Dentro de doInBackground");
            mSocket.on("messages", onNewMessage);
            return null;
        }
        
        public void disconnect() {
            try {
                Log.d(TAG, "Closing the socket connection.");

            } catch (Exception ex) {
                Log.e(TAG, "disconnect(): " + ex.toString());
            }
        }

    }

}
