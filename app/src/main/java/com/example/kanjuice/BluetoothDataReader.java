package com.example.kanjuice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothDataReader {

    public static final String TAG = "BluetoothDataReader";
    public static final java.util.UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice arduinoDevice;

    private InputStream mmInputStream;
    private Thread workerThread;

    private volatile boolean stopWorker;

    private SerialDataReceiver receiver;

    public interface SerialDataReceiver {
        void onDataReceived(byte[] data);
        void onDataReceiveFail(String message);
    }

    public BluetoothDataReader(SerialDataReceiver receiver) {
        this.receiver = receiver;
    }

    void closeConnection() {
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to close BT with exception : " + e.getMessage());
        }
    }

    boolean openConnection() {
        try {
            return openBT(findBT());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to open BT with exception : " + e.getMessage());
            return false;
        }
    }

    private BluetoothDevice findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "No bluetooth adapter available");
            return null;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                arduinoDevice = device;
                Log.v(TAG, "findBT found device named " + arduinoDevice.getName());
                Log.v(TAG, "device address is " + arduinoDevice.getAddress());
                break;
            }
        }
        return arduinoDevice;
    }

    private boolean openBT(BluetoothDevice arduinoDevice) throws IOException {
        if (arduinoDevice == null) {
            return false;
        }

        socket = arduinoDevice.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
        socket.connect();
        mmInputStream = socket.getInputStream();
        beginListenForData();
        return true;
    }

    private void beginListenForData() {
        setStopWorker(false);
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !isStopWorker()) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        byte[] packetBytes = new byte[bytesAvailable];
                        int read = mmInputStream.read(packetBytes);
                        if (read > 0) {
                            receiver.onDataReceived(packetBytes);
                        }
                    } catch (IOException ex) {
                        setStopWorker(true);
                        receiver.onDataReceiveFail(ex.getMessage());
                    }
                }
            }
        });

        workerThread.start();
    }

    private void closeBT() throws IOException {
        setStopWorker(true);

        if (mmInputStream != null) {
            mmInputStream.close();
            mmInputStream = null;
        }

        if (socket != null) {
            socket.close();
            socket = null;
        }
        Log.d(TAG, "Bluetooth Closed");
    }

    public void setStopWorker(boolean stopWorker) {
        this.stopWorker = stopWorker;
    }

    public boolean isStopWorker() {
        return this.stopWorker;
    }
}