package com.example.kanjuice;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hoho.android.usbserial.util.SerialInputOutputManager;

import static java.lang.String.format;


public class UserInputActivity extends Activity {
    private static final String TAG = "UserInputActivity";

    private TextView cardNumberView;
    private UsbManager usbManager;
    private RfidCardReader rfidCardReader;
    private String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_input);

        setupViews(getIntent());

        rfidCardReader = new RfidCardReader(this, cardDataListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rfidCardReader.stopIoManager();
        rfidCardReader.closePort();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rfidCardReader.isConnected()) {
            rfidCardReader.connectToSerialPort();
            rfidCardReader.onDeviceStateChange();
        } else {
            cardNumberView.setText("Failed to connect to serail port");
        }
    }


    public void setupViews(Intent intent) {
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(format("You have selected %s juice", intent.getStringExtra("juice_name")));

        cardNumberView = (TextView) findViewById(R.id.card_number);
    }

    private void updateReceivedData(byte[] data) {
        cardNumber += new String(data);
        if (cardNumber.contains("*")) {
            cardNumberView.setText("card# " + cardNumber.substring(1, cardNumber.length() - 1).trim() + "");
            cardNumber = "";
        }
    }

    private final SerialInputOutputManager.Listener cardDataListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    UserInputActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserInputActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };
}
