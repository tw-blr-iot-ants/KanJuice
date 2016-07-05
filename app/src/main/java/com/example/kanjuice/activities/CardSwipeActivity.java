package com.example.kanjuice.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kanjuice.BuildConfig;
import com.example.kanjuice.JuiceServer;
import com.example.kanjuice.KanJuiceApp;
import com.example.kanjuice.R;
import com.example.kanjuice.gcm.GCMReceiverService;
import com.example.kanjuice.models.User;
import com.example.kanjuice.utils.TypedJsonString;

import org.acra.ACRA;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CardSwipeActivity extends Activity {
    private static final String TAG = "CardSwipeActivity";
    private static final int MSG_FINISH = 101;
    private BroadcastReceiver receiver;

    private static final int REQUEST_CODE_REGISTER = 2001;
    public static final String EXTRA_INTERNAL_NUMBER = BuildConfig.APPLICATION_ID + ".EMP_ID";
    private Integer internalnumber = 0;
    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    CardSwipeActivity.this.finish();
                    break;
            }
        }
    };
    private TextView swipeCardTextView;

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String internalNumber = intent.getStringExtra(EXTRA_INTERNAL_NUMBER);
                Log.d(CardSwipeActivity.class.getSimpleName(), "internal number is : " + internalNumber);
                if (internalNumber != null)
                    registerNewUser(Integer.valueOf(internalNumber));
            }
        };
        broadcastManager.registerReceiver(receiver, new IntentFilter(GCMReceiverService.ACTION_RECEIVE_EMP_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.swipe_card);

        swipeCardTextView = (TextView) findViewById(R.id.swipe_card);
    }

    private void registerNewUser(Integer internalnumber) {
        this.internalnumber = internalnumber;
        H.removeMessages(MSG_FINISH);
        startRegisterActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
                onRegisterActivityCallback(getUserFromIntent(data));
            } else {
                ACRA.getErrorReporter()
                        .handleException(new Throwable("CardSwipeActivity Request code not ok " + requestCode));
                H.sendEmptyMessage(MSG_FINISH);
            }
        }
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(CardSwipeActivity.this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

    private User getUserFromIntent(Intent data) {
        User newUser = new User();
        newUser.employeeName = data.getStringExtra("employeeName");
        newUser.empId = data.getStringExtra("empId");
        newUser.internalNumber = String.valueOf(this.internalnumber);

        Log.d(TAG, " new user registered : " + newUser.toString());
        return newUser;
    }

    private void onRegisterActivityCallback(final User user) {
        swipeCardTextView.setText("Registering ...");
        registerUser(user);
    }

    private void registerUser(final User user) {
        getJuiceServer().register(new TypedJsonString(user.toJson()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                CardSwipeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CardSwipeActivity.this, "Hey " + user.employeeName + "!! You have been registered successfully", Toast.LENGTH_LONG).show();
                    }
                });
                H.sendEmptyMessage(MSG_FINISH);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to register the user");
                ACRA.getErrorReporter().handleException(error);
                Toast.makeText(CardSwipeActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                H.sendEmptyMessage(MSG_FINISH);
            }
        });
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }
}
