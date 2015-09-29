package com.example.kanjuice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.util.SerialInputOutputManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_input);

        juices = getIntent().getParcelableArrayExtra("juices");
        setupViews(juices);
        rfidCardReader = new RfidCardReader(this, cardDataListener);
        progressDialog = new ProgressDialog();
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

    private void onGo(EditText euidView) {
        String cardNumber = euidView.getText().toString().trim();
        if (cardNumber.length() == 5) {
            showProgressDialog();
            placeOrderForEuid(cardNumber);
        } else {
            makeText(UserInputActivity.this, "Employee id entered is not valid", LENGTH_SHORT);
        }
    }

    private void showProgressDialog() {
        progressDialog.show(getFragmentManager(), "orderProgress");
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    private void placeOrderForEuid(final String euid) {
        getJuiceServer().getUserByEuid(euid, new Callback<User>() {

            @Override
            public void success(final User user, Response response) {
                placeUserOrder(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch user for given euid: " + error.getMessage());
                handleUserNotFound();
            }
        });
    }

    private void onCardNumberReceived(int cardNumber) {
        getJuiceServer().getUserByCardNumber(cardNumber, new Callback<User>() {

            @Override
            public void success(User user, Response response) {
                placeUserOrder(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch user for given euid : " + error.getMessage());
                handleUserNotFound();
            }
        });
    }

    private void placeUserOrder(User user) {
        if (user == null) {
            handleUserNotFound();
            return;
        }

        getJuiceServer().placeOrder(new TypedJsonString(constructOrder(user).asJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "Successfully placed your order");
                finishActivityWithToast("Your order is successfully placed");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to place your order: " + error.getMessage());
                finishActivityWithToast("Failed to place your order");
            }
        });
    }

    private Order constructOrder(User user) {
        Order order = new Order();
        order.employeeId = user.empId;
        for(Parcelable juice : juices) {
            JuiceItem item = (JuiceItem) juice;
            order.addDrink(item.juiceName, item.selectedQuantity);
        }
        Log.d(TAG, "order is being placed : " + order.toString() + " for user: " + user.toString());
        return order;
    }

    private void handleUserNotFound() {
        finishActivityWithToast("Sorry, We are not able to find you in our database");
    }

    private void finishActivityWithToast(final String message) {
        UserInputActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeText(UserInputActivity.this, message, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
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
        euidView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onGo(euidView);
                    handled = true;
                }
                return handled;
            }
        });

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onGo(euidView);
            }
        });
    }

    private Object getJuiceCount(Parcelable[] juices) {
        if (juices == null) {
            return "";
        }

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
            onCardNumberReceived(extractCardNumber(cardNumber));
            this.cardNumber = "";
        }
    }

    private Integer extractCardNumber(String readString) {
        Log.d(TAG, "Card# " + readString);
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


    public static class ProgressDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_order_progress, null));
            return builder.create();
        }
    }
}
