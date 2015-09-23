package com.example.kanjuice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static java.lang.String.format;


public class UserInputActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_input);

        setupViews(getIntent());
    }

    public void setupViews(Intent intent) {
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(format("You have selected %s juice", intent.getStringExtra("juice_name")));
    }
}
