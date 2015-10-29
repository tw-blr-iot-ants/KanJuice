package com.example.kanjuice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class BlueToothProvider {

    private final String TAG = "ArduinoBluetooth";
    private final UUID MY_UUID = UUID.randomUUID();
    private BluetoothSocket arduinoSocket;
    private BluetoothDevice arduinoBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private BlueToothReaderThread blueToothReaderThread;
    private Context context;

    public BlueToothProvider(Context context) {
        this.context = context;
    }

    private BluetoothDevice initialize() throws NoBlueToothDeviceConnectedException {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() != 1) {
            throw new NoBlueToothDeviceConnectedException();
        }
        arduinoBluetooth = (BluetoothDevice) bondedDevices.toArray()[0];
        return arduinoBluetooth;
    }

    public void connect() {
        try {
            initialize();
        } catch (NoBlueToothDeviceConnectedException e) {
            Toast.makeText(context, " Failed to conenct to bluetooth device : ", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(context, " connecting to bluetooth device : " + arduinoBluetooth.getName(), Toast.LENGTH_LONG).show();
        connectThread = new ConnectThread(arduinoBluetooth);
        connectThread.start();
    }

    public void disconnect() {
        stopReading();

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
    }

    public void startReading(Handler handler) {
        if (arduinoSocket == null) {
            Log.d(TAG, "ERROR: failed arduinoSocket is null");
            return;
        }

        blueToothReaderThread = new BlueToothReaderThread(arduinoSocket, handler);
        blueToothReaderThread.start();
    }

    public void stopReading() {
        if (blueToothReaderThread != null) {
            blueToothReaderThread.cancel();
            blueToothReaderThread = null;
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "createRfcommSocketToServiceRecord Failed e: " + e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                Log.d(TAG, "Connect Failed e: " + e.getMessage());
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            manageConnectedSocket(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void manageConnectedSocket(BluetoothSocket arduinoSocket) {
        this.arduinoSocket = arduinoSocket;
    }

    private class BlueToothReaderThread extends Thread {
        private final BluetoothSocket mmSocket;
        private Handler handler;
        private final InputStream mmInStream;

        public BlueToothReaderThread(BluetoothSocket socket, Handler handler) {
            mmSocket = socket;
            this.handler = handler;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.d(TAG, "Failed to get input stream Failed e: " + e.getMessage());
            }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    handler.obtainMessage(UserInputActivity.MSG_DATA_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Failed to read input stream Failed e: " + e.getMessage());
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
