package com.example.kanjuice.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
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

import com.example.kanjuice.BuildConfig;
import com.example.kanjuice.JuiceServer;
import com.example.kanjuice.KanJuiceApp;
import com.example.kanjuice.R;
import com.example.kanjuice.gcm.GCMReceiverService;
import com.example.kanjuice.models.JuiceItem;
import com.example.kanjuice.models.Order;
import com.example.kanjuice.models.User;
import com.example.kanjuice.utils.AndroidUtils;
import com.example.kanjuice.utils.TypedJsonString;

import org.acra.ACRA;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.format;


public class UserInputActivity extends Activity {
    private static final String TAG = "UserInputActivity";
    public static final int TIME_FOR_FINISHING_ACTIVITY = 2000;
    private static final int REQUEST_CODE_ADMIN = 1001;
    private static final int REQUEST_CODE_REGISTER = 1002;
    private static final int MSG_FINISH = 101;
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
    private BroadcastReceiver receiver;

    public static final String NOTIFICATION_PAYLOAD = BuildConfig.APPLICATION_ID + ".EMP_ID";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    UserInputActivity.this.finish();
                    break;
                default:
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_input);

        juices = getIntent().getParcelableArrayExtra("juices");
        setupViews(juices);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AndroidUtils.disableRecentAppsClick(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String notificationPayload = intent.getStringExtra(NOTIFICATION_PAYLOAD);
                if (notificationPayload != null) {
                    String employeeId = extractEmployeeId(notificationPayload);
                    Log.d(TAG, "EmployeeId is : " + employeeId);
                    placeOrder(employeeId);
                }
            }
        };
        manager.registerReceiver(receiver, new IntentFilter(GCMReceiverService.ACTION_RECEIVE_EMP_ID));
    }

    private String extractEmployeeId(String notificationPayload) {
        if (!notificationPayload.endsWith(",")) {
            return notificationPayload.substring(notificationPayload.lastIndexOf(",") + 1, notificationPayload.length());
        }
        return null;
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
        handler.removeMessages(MSG_FINISH);
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_CODE_REGISTER);
    }

    private void animateOut() {
        cardLayout.setVisibility(View.INVISIBLE);
        euidLayout.setVisibility(View.INVISIBLE);
        orLayout.setVisibility(View.INVISIBLE);
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }

    private void onGo(EditText euidView) {
        String cardNumber = euidView.getText().toString().trim();

        placeOrder(cardNumber);
    }

    private void placeOrder(String cardNumber) {
        if (cardNumber == null || cardNumber.length() == 5) {
            showOrdering();
            placeOrderForEuid(cardNumber);
        } else if (cardNumber.length() == 3) {
            handleEasterEggs(cardNumber);
        } else {
            Toast.makeText(UserInputActivity.this, "Employee id entered is not valid", LENGTH_SHORT).show();
        }
    }

    private void handleEasterEggs(String whichEgg) {
        if (whichEgg.equals("999")) {
            showAdminPage();
        } else if (whichEgg.equals("888")) {
            showRegisterScreen();
        } else if (whichEgg.equals("777")) {
            AndroidUtils.clearKanJuiceAsDefaultApp(this);
        }
    }

    private void showAdminPage() {
        handler.removeMessages(MSG_FINISH);
        startActivityForResult(new Intent(this, AdminActivity.class), REQUEST_CODE_ADMIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADMIN) {
            finish();
        } else if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
                registerUser(getUserFromIntent(data));
            } else {
                handler.sendEmptyMessage(MSG_FINISH);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void registerUser(final User user) {
        getJuiceServer().register(new TypedJsonString(user.toJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                placeUserOrder(user, false, true);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to register the user");
                orderFinished(false, "Failed to register. Try again!");
                setRegisterButtonVisibility(false);
            }
        });
    }

    private void placeOrderForEuid(final String euid) {
        getJuiceServer().getUserByEuid(euid, new Callback<User>() {

            @Override
            public void success(final User user, Response response) {
                placeUserOrder(user, false, false);
            }

            @Override
            public void failure(RetrofitError error) {
                ACRA.getErrorReporter()
                        .handleException(error);

                Log.d(TAG, "Failed to fetch user for given euid: " + error.getMessage());
                orderFinished(false, "Failed to fetch your info. Contact Admin Team/Siddhu");
                setRegisterButtonVisibility(false);
            }
        });
    }

    private void placeUserOrder(final User user, final boolean allowRegistration,
                                final boolean isSwipe) {
        if (user == null) {
            ACRA.getErrorReporter().handleException(new Throwable("placeUserOrder user is null"));
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
                ACRA.getErrorReporter().handleException(error);
                setRegisterButtonVisibility(false);
                orderFinished(false, "Sorry! Failed to place your order, Try again!");
            }
        });
    }


    private User getUserFromIntent(Intent data) {
        User newUser = new User();
        newUser.employeeName = data.getStringExtra("employeeName");
        newUser.empId = data.getStringExtra("empId");
        newUser.internalNumber = String.valueOf(internalCardNumber);
        return newUser;
    }

    private void showOrdering() {
        hideIme();
        handler.removeMessages(MSG_FINISH);
        animateOut();
        orderingProgressView.setVisibility(View.VISIBLE);
    }

    private void orderFinished(final boolean isSuccess, final String message) {
        orderFinished(isSuccess, message, TIME_FOR_FINISHING_ACTIVITY);
    }

    private void orderFinished(final boolean isSuccess, final String message, final int timeForFinish) {
        UserInputActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderingProgressView.setVisibility(View.INVISIBLE);
                messageView.setText(message);
                statusView.setImageResource(isSuccess ? R.drawable.success : R.drawable.failure);
                messageLayout.setVisibility(View.VISIBLE);

                handler.removeMessages(MSG_FINISH);
                handler.sendEmptyMessageDelayed(MSG_FINISH, timeForFinish);
            }
        });
    }

    private void hideIme() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        for (Parcelable juice : juices) {
            JuiceItem item = (JuiceItem) juice;
            order.addDrink(item.juiceName, item.isSugarless, item.selectedQuantity, item.isFruit, item.type);
        }
        Log.d(TAG, "order is being placed : " + order.toString() + " for user: " + user.toString());
        return order;
    }

    private Object getJuiceCount(Parcelable[] juices) {
        int count = 0;
        if (juices == null) {
            return "";
        }


        for (Parcelable item : juices) {
            count += ((JuiceItem) item).selectedQuantity;
        }

        if (count == 1) {
            return (((JuiceItem) juices[0]).juiceName + getSuffixForOrder(juices[0]) + isSugarless(juices[0]));
        } else {
            return (count + " juices");
        }
    }

    private String getSuffixForOrder(Parcelable juice) {
        return ((JuiceItem) juice).isFruit ? " fruit " : " juice ";
    }

    private String isSugarless(Parcelable juice) {
        if (((JuiceItem) juice).isFruit) return "";
        if (((JuiceItem) juice).isSugarless) {
            return "Sugarless";
        } else {
            return "with Sugar";
        }
    }
}
