package com.example.kanjuice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.hoho.android.usbserial.util.SerialInputOutputManager;

import static java.lang.String.format;


public class UserInputActivity extends Activity {
    private static final String TAG = "UserInputActivity";
    public static final int FINISH_DELAY_MILLIS = 15000;

    private TextView cardNumberView;
    private RfidCardReader rfidCardReader;
    private String cardNumber = "";

    private static final int MSG_FINISH = 101;
    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    UserInputActivity.this.finish();
                    break;

            }
        }
    };

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

        H.removeMessages(MSG_FINISH);
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

        H.sendEmptyMessageDelayed(MSG_FINISH, FINISH_DELAY_MILLIS);
    }

    public void setupViews(Intent intent) {
        TextView titleView = (TextView) findViewById(R.id.title);
        Parcelable[] juices = intent.getParcelableArrayExtra("juices");
        titleView.setText(format("You have selected %s juices", getJuiceCount(juices)));

        cardNumberView = (TextView) findViewById(R.id.card_number);
    }

    private Object getJuiceCount(Parcelable[] juices) {
        int count = 0;
        for(Parcelable item : juices) {
            count += ((Juice)item).selectedQuantity;
        }
        return count;
    }

    private void updateReceivedData(byte[] data) {
        cardNumber += new String(data);
        if (cardNumber.contains("*")) {
            H.removeMessages(MSG_FINISH);
            cardNumberView.setText("card# " + extractCardNumber(cardNumber));
            this.cardNumber = "";
        }
    }

    private Integer extractCardNumber(String readString) {
        String cardDecNumber = readString.substring(readString.indexOf("$") + 1 , readString.length() - 1).trim();
        String binaryNumber = Integer.toBinaryString(Integer.valueOf(cardDecNumber));
        return Integer.valueOf(binaryNumber.substring(7, binaryNumber.length() - 1), 2);
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
