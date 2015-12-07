package com.example.kanjuice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.lang.String.format;


public class UserInputActivity extends BluetoothServiceConnectionActivity {
    private static final String TAG = "UserInputActivity";
    public static final int NO_USER_ACTIVITY_FINISH_DELAY = 10000;
    public static final int ANIMATION_DURATION = 500;
    public static final int DELAY_BEFORE_FINISHING_ACTIVITY = 2500;

    private static final int REQUEST_CODE_ADMIN = 1001;
    private static final int REQUEST_CODE_REGISTER = 1002;

    private TextView cardNumberView;
    private String cardNumber = "";

    private static final int MSG_FINISH = 101;
    public static final int MSG_DATA_RECEIVED = 102;
    public static final int MSG_FAILED_BLUETOOTH_CONNECTION = 103;
    public static final int MSG_SHOW_REGISTRATION_SCREEN = 104;
    public static final int MSG_DATA_RECEIVE_FAILED = 105;
    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    UserInputActivity.this.finish();
                    break;
                case MSG_DATA_RECEIVED:
                     UserInputActivity.this.updateReceivedData((byte[]) msg.obj);
                    break;

                case MSG_DATA_RECEIVE_FAILED:
                    orderFinished(false, "Failed read card details, Please try again");
                    break;

                case MSG_FAILED_BLUETOOTH_CONNECTION:
                    Toast.makeText(UserInputActivity.this,
                            "Failed to connect to bluetooth device",
                            Toast.LENGTH_LONG).show();
                    break;

                case MSG_SHOW_REGISTRATION_SCREEN:
                    UserInputActivity.this.showRegisterScreen();
                    break;
            }
        }
    };

    private Parcelable[] juices;
    private View cardLayout;
    private View euidLayout;
    private View orLayout;
    private View orderingProgressView;
    private TextView messageView;
    private TextView swipeCardView;
    private ImageView statusView;
    private View messageLayout;
    private int internalCardNumber;
    private View registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        juices = getIntent().getParcelableArrayExtra("juices");
        if(isRegisterActivity()) {
            setContentView(R.layout.swipe_card);
            swipeCardView = (TextView) findViewById(R.id.swipe_card);
            swipeCardView.setVisibility(View.VISIBLE);
        }
        else {
            setContentView(R.layout.activity_user_input);
            setupViews(juices);
        }
    }

    private boolean isRegisterActivity() {
        return ((JuiceItem)juices[0]).juiceName.equals("Register User");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AndroidUtils.disableRecentAppsClick(this);
        H.removeMessages(MSG_FINISH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        H.sendEmptyMessageDelayed(MSG_FINISH, NO_USER_ACTIVITY_FINISH_DELAY);
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
        titleView.setText(Html.fromHtml(format("You have selected <b>%s</b>", getJuiceCount(juices))));

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
        H.removeMessages(MSG_FINISH);
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
        } else if (requestCode == REQUEST_CODE_REGISTER) {
            if(resultCode == RESULT_OK) {
                registerUser(getUserFromIntent(data));
            } else {
                H.sendEmptyMessage(MSG_FINISH);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void registerUser(final User user) {
        getJuiceServer().register(new TypedJsonString(user.toJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                H.sendEmptyMessage(MSG_FINISH);
                if (isRegisterActivity()) {
                    UserInputActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Thank you " + user.employeeName + ". Your card has been registered",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    placeUserOrder(user, false, true);
                }
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

                H.removeMessages(MSG_FINISH);
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
                placeUserOrder(user, false, false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch user for given euid: " + error.getMessage());
                orderFinished(false, "Failed to fetch your information for employee Id : " + euid);
                setRegisterButtonVisibility(false);
            }
        });
    }

    private void onCardNumberReceived(final int cardNumber) {
        Log.d(TAG, "onCardNumberReceived: " + cardNumber);
        internalCardNumber = cardNumber;
        if (isRegisterActivity()) {
            H.sendEmptyMessage(MSG_SHOW_REGISTRATION_SCREEN);
        } else {
            showOrdering();
            getJuiceServer().getUserByCardNumber(cardNumber, new Callback<User>() {

                @Override
                public void success(User user, Response response) {
                    placeUserOrder(user, true, true);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed to fetch user for given cardNumber : " + cardNumber + " e: " + error.getMessage());
                    orderFinished(false, "Your card is not registered", 6500);
                }
            });
        }
    }

    private void placeUserOrder(final User user, final boolean allowRegistration, final  boolean isSwipe) {
        if (user == null) {
            orderFinished(false, "Failed to fetch your information");
            return;
        }

        getJuiceServer().placeOrder(new TypedJsonString(constructOrder(user, isSwipe).asJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "Successfully placed your order");
                setRegisterButtonVisibility(false);
                orderFinished(true, "Thank you " + user.employeeName + "! Your order is successfully placed");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to place your order: " + error.getMessage());
                setRegisterButtonVisibility(allowRegistration ? true : false);
                orderFinished(false, "Sorry! Problem placing your order");
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

    private Order constructOrder(User user, Boolean isSwipe) {
        Order order = new Order();
        order.employeeId = user.empId;
        order.employeeName = user.employeeName;
        order.isSwipe = isSwipe;
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
        Log.d(TAG, "updateDataReceived " + new String(data));
        try {
            cardNumber += new String(data);
            if (cardNumber.contains("*")) {
                onCardNumberReceived(extractCardNumber(cardNumber));
                this.cardNumber = "";
            }
        } catch(Exception e) {
            Log.d(TAG, "Exception while reading card "  + this.cardNumber + " with "  + e.getMessage());
            e.printStackTrace();
            this.cardNumber = "";
            orderFinished(false, "Problem reading your card number");
        }
    }

    private Integer extractCardNumber(String readString) {
        Log.d(TAG, "Card# " + readString);
        String cardDecNumber = readString.substring(readString.indexOf("$") + 1 , readString.length() - 1).trim();
        String binaryNumber = Integer.toBinaryString(Integer.valueOf(cardDecNumber));
        int startIndex = binaryNumber.length() - 17;
        if (startIndex < 0) {
            startIndex = 0;
        }
        return Integer.valueOf(binaryNumber.substring(startIndex, binaryNumber.length() - 1), 2);
    }

    @Override
    protected Handler getHandler() {
        return H;
    }
}
