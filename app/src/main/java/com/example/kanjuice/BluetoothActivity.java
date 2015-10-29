package com.example.kanjuice;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends Activity {

    public static final String TAG = "ArduinoBT";
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            openConnection();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);

        Button openButton = (Button) findViewById(R.id.open);
        Button closeButton = (Button) findViewById(R.id.close);
        myLabel = (TextView) findViewById(R.id.label);
//
//        // Open Button
//        openButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                try {
//                    findBT();
//                    openBT();
//                } catch (IOException ex) {
//                }
//            }
//        });
//
//        // Close button
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                try {
//                    closeBT();
//                } catch (IOException ex) {
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        H.sendEmptyMessage(100);
    }

    private void openConnection() {
        try {
            findBT();
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to open BT with exception : " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        closeConnection();
    }

    private void closeConnection() {
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to close BT with exception : " + e.getMessage());
        }
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mmDevice = device;
                Log.v(TAG, "findBT found device named " + mmDevice.getName());
                Log.v(TAG, "device address is " + mmDevice.getAddress());
                break;
            }
        }
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        myLabel.setText("Bluetooth Opened");
        beginListenForData();

    }

    void beginListenForData() {
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        byte[] packetBytes = new byte[bytesAvailable];
                        int read = mmInputStream.read(packetBytes);
                        if (read > 0) {
                            Log.d(TAG, "byetes:  : " + new String(packetBytes));
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }


}
