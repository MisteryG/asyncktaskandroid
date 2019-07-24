package com.example.myapplicationasyncttasttest;

import android.app.Activity;
import android.os.AsyncTask;
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;
import static android.view.Gravity.CENTER;

public class MainActivity extends Activity {
    String dominio = "http://192.168.1.99:8080"; // Beto ip
    String ruta;
    ProcessTask PT;
    PrinterBluetooh printerBluetooh = new PrinterBluetooh();
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
        mSocket.emit("connected");
        mSocket.connect();
        PT = new ProcessTask();
        PT.execute();
    }

    public void sendRest (String eventoN, String handheldN, String printerN, String etiquetaN) {
        ruta="/api/informeMonitoreo";
        JSONObject jsonRest = new JSONObject();
        String result = null;
        URL url = null;
        try {
            url = new URL(dominio+ruta);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            jsonRest.put("identificador", 1);
            JSONObject jsonObjTwo = new JSONObject();
            jsonObjTwo.put("ID_accion", eventoN);
            jsonObjTwo.put("ID_HH", handheldN);
            jsonObjTwo.put("ID_Impresora", printerN);
            jsonObjTwo.put("matricula", etiquetaN);
            jsonRest.put("datos", jsonObjTwo);
            Log.i("json", "value" + jsonRest);
            Log.i("_____", "_______________");
            Log.i("----", "--->>>" + eventoN + handheldN + printerN + etiquetaN);

            //DEFINIR PARAMETROS DE CONEXION
            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000/* milliseconds */);
            urlConnection.setRequestMethod("POST");// se puede cambiar por delete ,put ,etc
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //OBTENER EL RESULTADO DEL REQUEST
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(jsonRest));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();// conexion OK?
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String linea = "";
                while ((linea = in.readLine()) != null) {
                    sb.append(linea);
                    break;
                }
                in.close();
                result = sb.toString();
            } else {
                //  Toast.makeText(httpContext, "no disponible", Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request}
                result = new String("Error: " + responseCode);
                Log.i("resulr", result);
                // urlConnection.disconnect();

            }
        } catch (MalformedURLException e) {
            // Toast.makeText(httpContext, "no disponible", Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request}
            e.printStackTrace();
            //  e.getMessage();
        } catch (IOException e) {
            // Toast.makeText(httpContext, "no disponible", Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request}
            e.printStackTrace();
            //  e.getMessage();
        } catch (JSONException e) {
            //  Toast.makeText(httpContext, "no disponible", Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request}
            e.printStackTrace();
            //  e.getMessage();
        } catch (Exception e) {
            // Toast.makeText(httpContext, "no disponible", Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request}
            e.printStackTrace();
            // e.getMessage();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            String a = params.getString("ID_accion");
            String b = params.getString(" ID_HH");
            String c = params.getString("ID_Impresora");
            String d = params.getString("matricula");
            Log.i("Parse", a);
            Log.i("Parse", b);
            Log.i("Parse", c);
            Log.i("Parse", d);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
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
                        Log.i("===>","data  "+data);
                    }catch(Exception e){
                        Log.i("===>","error  "+e);
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

}
