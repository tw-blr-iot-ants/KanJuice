package com.example.kanjuice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (euid.length() != 5) {
            return;
        }

        Intent data = new Intent();
        data.putExtra("empId", euid);
        data.putExtra("employeeName", name);
        setResult(RESULT_OK, data);
        finish();
    }

}
