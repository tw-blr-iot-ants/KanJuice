package com.example.kanjuice.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kanjuice.R;

import static android.view.WindowManager.LayoutParams.*;


public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(FLAG_NOT_TOUCH_MODAL, FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(FLAG_WATCH_OUTSIDE_TOUCH, FLAG_WATCH_OUTSIDE_TOUCH);

        setContentView(R.layout.activity_register);

        setupViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableRecentAppsClick();
    }

    private void disableRecentAppsClick() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    private void setupViews() {
        final EditText empIdView  = (EditText) findViewById(R.id.empId);
        final EditText nameView  = (EditText) findViewById(R.id.name);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(empIdView.getText().toString().trim(), nameView.getText().toString().trim());
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void save(String euid, String name) {
        if ("".equals(euid)) {
            Toast.makeText(RegisterActivity.this, "Please enter your EmpID!", Toast.LENGTH_LONG).show();
            return;
        }

        if (euid.trim().length() != 5) {
            Toast.makeText(RegisterActivity.this, "EmpID should be 5 digits!", Toast.LENGTH_LONG).show();
            return;
        }

        if("".equals(name)) {
            Toast.makeText(RegisterActivity.this, "Please enter your Full Name!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra("empId", euid.trim());
        data.putExtra("employeeName", name.trim());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            return true;
        }
        return super.onTouchEvent(event);
    }

}
