package com.example.kanjuice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kanjuice.*;
import com.example.kanjuice.models.User;
import com.example.kanjuice.utils.TypedJsonString;

import org.acra.ACRA;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CardSwipeActivity extends BluetoothServiceConnectionActivity {
    private static final String TAG = "CardSwipeActivity";
    private static final int MSG_FINISH = 101;
    public static final int MSG_DATA_RECEIVED = 102;
    public static final int MSG_FAILED_BLUETOOTH_CONNECTION = 103;
    private static final int MSG_REGISTER_USER = 104;
    private static final int MSG_DATA_RECEIVE_FAILED = 105;

    private static final int REQUEST_CODE_REGISTER = 2001;
    public static final int NO_REGISTER_ACTIVITY_FINISH_DELAY = 10000;

    private Integer internalnumber = 0;

    Handler H = new Handler() {
      @Override
      public void handleMessage(Message msg) {
          switch (msg.what) {
              case MSG_FINISH:
                  CardSwipeActivity.this.finish();
                  break;

              case MSG_FAILED_BLUETOOTH_CONNECTION:
                  Toast.makeText(CardSwipeActivity.this,
                          "Failed to connect to bluetooth device",
                          Toast.LENGTH_LONG).show();
                  ACRA.getErrorReporter().handleException(new Throwable("Failed to connect to bluetooth device"));
                  break;
              case MSG_DATA_RECEIVED:
                  stopListeningForData();
                  CardSwipeActivity.this.registerNewUser((Integer) msg.obj);
                  break;

              case MSG_REGISTER_USER:
                  registerUser((User) msg.obj);
                  break;

              case MSG_DATA_RECEIVE_FAILED:
                  Toast.makeText(CardSwipeActivity.this,
                          "Error Reading your card !! Bangalore facilities team has been informed about the same",
                          Toast.LENGTH_LONG).show();
                  CardSwipeActivity.this.finish();
                  break;
          }
      }
    };
    private TextView swipeCardTextView;

    @Override
    protected void onResume() {
        super.onResume();
        H.sendEmptyMessageDelayed(MSG_FINISH, NO_REGISTER_ACTIVITY_FINISH_DELAY);
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
        if(requestCode == REQUEST_CODE_REGISTER) {
            if(resultCode == RESULT_OK) {
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
        H.sendMessage(H.obtainMessage(MSG_REGISTER_USER, user));
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

    @Override
    protected Handler getHandler() {
        return H;
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }
}
