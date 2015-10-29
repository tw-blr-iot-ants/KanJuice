package com.example.kanjuice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.util.SerialInputOutputManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.lang.String.format;


public class UserInputActivity extends Activity {
    private static final String TAG = "UserInputActivity";
    public static final int NO_USER_ACTIVITY_FINISH_DELAY = 15000;
    public static final int ANIMATION_DURATION = 500;
    public static final int DELAY_BEFORE_FINISHING_ACTIVITY = 2000;

    private static final int REQUEST_CODE_ADMIN = 1001;
    private static final int REQUEST_CODE_REGISTER = 1002;

    private TextView cardNumberView;
    private RfidCardReader rfidCardReader;
    private String cardNumber = "";

    private static final int MSG_FINISH = 101;
    public static final int MSG_DATA_RECEIVED = 102;
    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    UserInputActivity.this.finish();
                    break;
                case MSG_DATA_RECEIVED:
                    onBluetoothDataReceived(msg.arg1, msg.obj);

            }
        }
    };

    private Parcelable[] juices;
    private View cardLayout;
    private View euidLayout;
    private View orLayout;
    private View orderingProgressView;
    private TextView messageView;
    private ImageView statusView;
    private View messageLayout;
    private int internalCardNumber;
    private View registerButton;

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
        AndroidUtils.disableRecentAppsClick(this);
        rfidCardReader.stopIoManager();
        rfidCardReader.closePort();

        H.removeMessages(MSG_FINISH);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rfidCardReader.isConnected()) {
            rfidCardReader.connectToSerialPort();
            rfidCardReader.onDeviceStateChange();
        } else {
            cardNumberView.setText("Card reader is not connected");
        }

        H.sendEmptyMessageDelayed(MSG_FINISH, NO_USER_ACTIVITY_FINISH_DELAY);

        // REMOVE ME PLEASE
        setRegisterButtonVisibility(true);
        orderFinished(false, "this sucks");
    }

    public void setupViews(Parcelable[] juices) {
        cardLayout = findViewById(R.id.card_swipe_layout);
        euidLayout = findViewById(R.id.euid_layout);
        orLayout = findViewById(R.id.or_layout);
        orderingProgressView = findViewById(R.id.ordering);

        messageLayout = findViewById(R.id.message_layout);
        statusView = (ImageView) findViewById(R.id.status_icon);
        messageView = (TextView) findViewById(R.id.message);

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

        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdering();
                showRegisterScreen();
            }
        });
    }

    private void showRegisterScreen() {
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_CODE_REGISTER);
    }

    private void animateOut() {
        ObjectAnimator cardAnimation = ObjectAnimator.ofFloat(cardLayout, "translationX", 0f, -400f);
        cardAnimation.setDuration(ANIMATION_DURATION);
        cardAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                cardLayout.setVisibility(View.INVISIBLE);
            }
        });
        cardAnimation.start();

        ObjectAnimator euidAnimation = ObjectAnimator.ofFloat(euidLayout, "translationX", 0f, 400f);
        euidAnimation.setDuration(ANIMATION_DURATION);
        euidAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                euidLayout.setVisibility(View.INVISIBLE);
            }
        });
        euidAnimation.start();

        ObjectAnimator orAnimation = ObjectAnimator.ofFloat(orLayout, "translationY", 0f, 400f);
        orAnimation.setDuration(ANIMATION_DURATION);
        orAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                orLayout.setVisibility(View.INVISIBLE);
            }
        });
        orAnimation.start();
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }

    private void onGo(EditText euidView) {
        String cardNumber = euidView.getText().toString().trim();

        if (cardNumber.length() == 3) {
            handleEasterEggs(cardNumber);
        } else if (cardNumber.length() == 5) {
            showOrdering();
            placeOrderForEuid(cardNumber);
        } else {
            makeText(UserInputActivity.this, "Employee id entered is not valid", LENGTH_SHORT);
        }
    }

    private void handleEasterEggs(String whichEgg) {
        if (whichEgg.equals("999")) {
            showAdminPage(whichEgg);
        } else if (whichEgg.equals("888")) {
            H.removeMessages(MSG_FINISH);
            showRegisterScreen();
        } else if (whichEgg.equals("777")) {
            AndroidUtils.clearKanJuiceAsDefaultApp(this);
        }
    }

    private void showAdminPage(String cardNumber) {
        H.removeMessages(MSG_FINISH);
        startActivityForResult(new Intent(this, AdminActivity.class), REQUEST_CODE_ADMIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADMIN) {
            finish();
        } else if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            registerUser(getUserFromIntent(data));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void registerUser(final User user) {
        getJuiceServer().register(new TypedJsonString(user.toJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                placeUserOrder(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to register the user");
            }
        });
    }

    private User getUserFromIntent(Intent data) {
        User newUser = new User();
        newUser.employeeName = data.getStringExtra("employeeName");
        newUser.empId = data.getStringExtra("empId");
        newUser.internalNumber = String.valueOf(internalCardNumber);

        Log.d(TAG, " new user registered : " + newUser.toString());
        return newUser;
    }

    private void showOrdering() {
        hideIme();
        H.removeMessages(MSG_FINISH);
        animateOut();
        orderingProgressView.setVisibility(View.VISIBLE);
    }

    private void orderFinished(final boolean isSuccess, final String message) {
        orderFinished(isSuccess, message, DELAY_BEFORE_FINISHING_ACTIVITY);
    }

    private void orderFinished(final boolean isSuccess, final String message, final int timeForFinish) {
        UserInputActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderingProgressView.setVisibility(View.INVISIBLE);
                messageView.setText(message);
                statusView.setImageResource(isSuccess ? R.drawable.success : R.drawable.failure);
                messageLayout.setVisibility(View.VISIBLE);
                H.sendEmptyMessageDelayed(MSG_FINISH, timeForFinish);
            }
        });
    }

    private void hideIme() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                orderFinished(false, "Failed to fetch your information for employee Id : " + euid);
            }
        });
    }

    private void onCardNumberReceived(final int cardNumber) {
        internalCardNumber = cardNumber;
        getJuiceServer().getUserByCardNumber(cardNumber, new Callback<User>() {

            @Override
            public void success(User user, Response response) {
                placeUserOrder(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch user for given cardNumber : " + error.getMessage());
                orderFinished(false, "Failed to fetch your information for card number : " + cardNumber);
            }
        });
    }

    private void placeUserOrder(final User user) {
        if (user == null) {
            orderFinished(false, "Failed to fetch your information");
            return;
        }

        getJuiceServer().placeOrder(new TypedJsonString(constructOrder(user).asJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "Successfully placed your order");
                setRegisterButtonVisibility(false);
                orderFinished(true, "Thank you " + user.employeeName + "! Your order is successfully placed");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to place your order: " + error.getMessage());
                if (!user.internalNumber.isEmpty()) {
                    setRegisterButtonVisibility(true);
                }
                orderFinished(false, "Sorry!, Failed to place your order");
            }
        });
    }

    private void setRegisterButtonVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
            showOrdering();
            onCardNumberReceived(extractCardNumber(cardNumber));
            this.cardNumber = "";
        }
    }

    private Integer extractCardNumber(String readString) {
        Log.d(TAG, "Card# " + readString);
        String cardDecNumber = readString.substring(readString.indexOf("$") + 1 , readString.length() - 1).trim();
        String binaryNumber = Integer.toBinaryString(Integer.valueOf(cardDecNumber));
        return Integer.valueOf(binaryNumber.substring(binaryNumber.length() - 17, binaryNumber.length() - 1), 2);
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


    @Override
    protected void onStart() {
        super.onStart();

        BlueToothProvider blueToothProvider = getApp().getBlueToothProvider();
        blueToothProvider.startReading(H);
    }

    @Override
    protected void onStop() {
        super.onStop();

        getApp().getBlueToothProvider().stopReading();
    }


    private void onBluetoothDataReceived(int arg1, Object obj) {
        Toast.makeText(this, "Bytes received : " + arg1, Toast.LENGTH_LONG).show();
    }

}
