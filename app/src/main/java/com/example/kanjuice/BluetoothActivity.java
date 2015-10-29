package com.example.kanjuice;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.example.kanjuice.BluetoothDataReader.SerialDataReceiver;

public abstract class BluetoothActivity extends Activity implements SerialDataReceiver {

    public static final String TAG = "ArduinoBT";
    final BluetoothDataReader bluetoothDataReader = new BluetoothDataReader(this);

    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            bluetoothDataReader.openConnection();
        }
    };

    @Override
    protected void onResume() {
        super.onStart();

        H.sendEmptyMessageDelayed(100, 2000);
    }

    @Override
    protected void onPause() {
        super.onStop();

        bluetoothDataReader.closeConnection();
    }
}
