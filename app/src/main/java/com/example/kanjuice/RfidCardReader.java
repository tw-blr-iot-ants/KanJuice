package com.example.kanjuice;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RfidCardReader {
    private static final String TAG = "RfidCardReader";

    private final UsbManager usbManager;
    private UsbSerialPort port;
    private SerialInputOutputManager mSerialIoManager;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager.Listener listener;

    private boolean isConnected  = true;

    public RfidCardReader(Context context, SerialInputOutputManager.Listener listener) {
        this.listener = listener;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        try {
            final List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
            port = drivers.get(0).getPorts().get(0);
        } catch (Exception e) {
            Log.d(TAG, "Failed to get serial port with exception : " + e.getMessage());
            isConnected = false;
        }
    }

    public boolean connectToSerialPort() {
        if (port == null) return false;

        UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());
        if (connection == null) {
            Log.d(TAG, "Opening device failed");
            return false;
        }

        try {
            port.open(connection);
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            try {
                port.close();
            } catch (IOException e2) {
                // Ignore.
            }
            port = null;
            return false;
        }
        return true;
    }

    public void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    public void startIoManager() {
        if (port != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(port, listener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    public void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    public void closePort() {
        if (port != null) {
            try {
                port.close();
            } catch (IOException e) {
                // Ignore.
            }
            port = null;
        }
    }

    public Boolean isConnected() {
        return isConnected;
    }
}
