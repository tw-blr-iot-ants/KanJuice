package com.example.kanjuice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public abstract class BluetoothServiceConnectionActivity extends Activity {

    private static final String TAG = "BluetoothActivity";
    private BluetoothReaderService mService;

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: remove this
        startService(new Intent(this, BluetoothReaderService.class));

        bindToBluetoothService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            stopListeningForData();
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void bindToBluetoothService() {
        Intent intent = new Intent(this, BluetoothReaderService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothReaderService.LocalBinder binder = (BluetoothReaderService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            startListeningForData();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void startListeningForData() {
        mService.startListening(getHandler());
    }

    public void stopListeningForData() {
        mService.stopListening();
    }

    protected abstract Handler getHandler();
}
