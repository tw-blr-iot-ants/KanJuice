package com.example.kanjuice;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BluetoothReaderService extends Service implements BluetoothDataReader.SerialDataReceiver {

    public static final String TAG = "BluetoothReaderService";

    final BluetoothDataReader bluetoothDataReader;
    private boolean isConnected;

    private static final int MSG_INITIALIZE_BLUETOOTH = 501;

    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_INITIALIZE_BLUETOOTH:
                    isConnected = bluetoothDataReader.openConnection();
                    break;
            }
        }
    };

    private final IBinder mBinder = new LocalBinder();
    private boolean clientListening;
    private Handler clientHandler;

    public class LocalBinder extends Binder {
        BluetoothReaderService getService() {
            return BluetoothReaderService.this;
        }
    }

    private BroadcastReceiver stateChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getAction();
            Log.d(TAG, "BluetoothConnectionStatechange: " + state);
            if (state.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                bluetoothDataReader.closeConnection();
                isConnected = false;
            }
        }
    };

    public BluetoothReaderService() {
        bluetoothDataReader = new BluetoothDataReader(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        H.sendEmptyMessageDelayed(MSG_INITIALIZE_BLUETOOTH, 2000);
        registerForBluetoothConnectionChange();
    }

    private void registerForBluetoothConnectionChange() {
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(stateChangeListener, filter1);
        this.registerReceiver(stateChangeListener, filter2);
        this.registerReceiver(stateChangeListener, filter3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        bluetoothDataReader.closeConnection();
        unregisterReceiver(stateChangeListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDataReceived(byte[] data) {
        Log.d(TAG, "onDataReceived: " + new String(data));
        if (clientListening && clientHandler != null) {
            clientHandler.obtainMessage(UserInputActivity.MSG_DATA_RECEIVED, data).sendToTarget();
        }
    }

    public void startListening(Handler clientHandler) {
        if (!isConnected) {
            isConnected = bluetoothDataReader.openConnection();
            if (!isConnected) {
                clientHandler.obtainMessage(UserInputActivity.MSG_FAILED_BLUETOOTH_CONNECTION).sendToTarget();
            }
        }

        this.clientHandler = clientHandler;
        clientListening = true;
    }

    public void stopListening() {
        clientListening = false;
        clientHandler = null;
    }
}