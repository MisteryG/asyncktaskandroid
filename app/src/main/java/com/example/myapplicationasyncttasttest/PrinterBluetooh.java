package com.example.myapplicationasyncttasttest;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class PrinterBluetooh extends AppCompatActivity {

    MainActivity main = null;

    public void setMainActivity(MainActivity main)
    {
        this.main = main;
    }

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    TextView txtmesage;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    boolean activado = false;
    boolean encontrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void findBT(String nameDevice) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Log.i("resul conection-->", "no se pudo conectar");
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(nameDevice)) {
                        mmDevice = device;
                        encontrado=true;
                        break;
                    }  // fin del if
                }// fin del for
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        Log.i("donde esta-->", "entro openbt "+encontrado);
        if (encontrado==true) {
            try {
                // Standard SerialPortService ID
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();
                activado = true;
            } catch (Exception e) {
                mmSocket.close();
                e.printStackTrace();
                Log.i("status", "" + e);
            }
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "ES-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                //  myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean sendData(String codigo) throws IOException {
        String codigoProducto = codigo;
        boolean impreso = false;
        try {
            String codigoZebra = "! 0 200 200 1000 1\r\n";
            codigoZebra += "LABEL\r\n";
            codigoZebra += "CONTRAST 0\r\n";
            codigoZebra += "TONE 0\r\n";
            codigoZebra += "SPEED 3\r\n";
            codigoZebra += "PAGE-WIDTH 410\r\n";
            codigoZebra += "BAR-SENSE\r\n";
            codigoZebra += "BARCODE-TEXT 20 50 10 \r\n";
            codigoZebra += "VBARCODE 128 3 1 190 80 800 " + codigoProducto + "\r\n";
            codigoZebra += "BARCODE-TEXT OFF\r\n";
            codigoZebra += "FORM\r\n";
            codigoZebra += "PRINT\r\n";


            Log.i("valor actual -->>>", codigoProducto.toString());
            String msg = codigoZebra.toString();
            mmOutputStream.write(msg.getBytes());
            impreso = true;

        } catch (IOException |NullPointerException e) {
            e.printStackTrace();
            Log.i("se atrapo una excepcion", "error al imprimir" + e);
            impreso = false;
        }
        return  impreso;
    }
    //cerra conexion bluetooth
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast t = Toast.makeText(this, "Bluetooth cerrado", Toast.LENGTH_SHORT);
            t.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
