package com.example.kanjuice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
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
    private Parcelable[] juices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_input);

        juices = getIntent().getParcelableArrayExtra("juices");
        setupViews(juices);
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
            cardNumberView.setText("Sorry, Some technical error in reading your RFID card");
        }

        H.sendEmptyMessageDelayed(MSG_FINISH, FINISH_DELAY_MILLIS);
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }

    private void onCardNumberReceived(int cardNumber) {
        getJuiceServer().getUserByCardNumber(cardNumber, new Callback<User>() {

            @Override
            public void success(User user, Response response) {
                placeUserOrder(user);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void placeUserOrder(User user) {
        Order order = new Order();
        order.employeeId = user.empId;
        /* Add more properties here */
        getJuiceServer().placeOrder(order, new Callback<Response>() {

            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "Successfully placed your order");
                makeText(UserInputActivity.this, "Your order is placed", LENGTH_SHORT).show();
                UserInputActivity.this.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to place your order");
                makeText(UserInputActivity.this, "Your order is placement failed", LENGTH_SHORT).show();
                UserInputActivity.this.finish();
            }
        });
    }

    public void setupViews(Parcelable[] juices) {
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(format("You have selected %s", getJuiceCount(juices)));

        cardNumberView = (TextView) findViewById(R.id.card_number);

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInputActivity.this.finish();
            }
        });

        final EditText euidView = (EditText) findViewById(R.id.edit_text_euid);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                placeOrderForEuid(euidView.getText().toString().trim());
            }
        });
    }

    private void placeOrderForEuid(final String euid) {
        getJuiceServer().getUserByEuid(euid, new Callback<User>() {

            @Override
            public void success(final User user, Response response) {
                UserInputActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        placeUserOrder(user);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch user by euid for : " + euid);
            }
        });
    }

    private Object getJuiceCount(Parcelable[] juices) {
        int count = 0;
        for(Parcelable item : juices) {
            count += ((JuiceItem)item).selectedQuantity;
        }
        return count == 1 ? ((JuiceItem)juices[0]).juiceName + " juice" : count + " juices";
    }

    private void updateReceivedData(byte[] data) {
        cardNumber += new String(data);
        if (cardNumber.contains("*")) {
            H.removeMessages(MSG_FINISH);
            cardNumberView.setText("card# " + extractCardNumber(cardNumber));
            this.cardNumber = "";
            onCardNumberReceived(extractCardNumber(cardNumber));
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
