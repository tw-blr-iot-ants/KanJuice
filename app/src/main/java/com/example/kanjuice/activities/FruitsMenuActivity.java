package com.example.kanjuice.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kanjuice.JuiceServer;
import com.example.kanjuice.KanJuiceApp;
import com.example.kanjuice.R;
import com.example.kanjuice.adapters.FruitAdapter;
import com.example.kanjuice.models.Juice;
import com.example.kanjuice.models.JuiceItem;
import com.example.kanjuice.utils.JuiceDecorator;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FruitsMenuActivity extends Activity {


    private ListView fruitsView;
    private FruitAdapter adapter;
    private View fruitMenuLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fruits_section);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchFruitsMenu();
    }


    private void setupViews() {
        fruitsView = (ListView) findViewById(R.id.grid);
        setupAdapter(fruitsView);

        fruitsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onJuiceItemClick(position);
                finish();
            }
        });

        fruitMenuLoadingView = findViewById(R.id.loading);
    }

    private void onJuiceItemClick(int position) {
        gotoSwipingScreen(position);
    }


    private void setupAdapter(ListView fruitsView) {
        adapter = new FruitAdapter(this);
        fruitsView.setAdapter(adapter);
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


    private void fetchFruitsMenu() {
        getJuiceServer().getFruits(new Callback<List<Juice>>() {
            @Override
            public void success(final List<Juice> juices, Response response) {
                FruitsMenuActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fruitMenuLoadingView.setVisibility(View.GONE);
                        fruitsView.setVisibility(View.VISIBLE);
                        onFruitsListReceived(juices);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("101", "Failed to fetch fruits list : " + error);
            }
        });
    }


    private void onFruitsListReceived(List<Juice> fruits) {
        decorate(fruits);
        adapter.addAll(fruits);
    }

    private void decorate(List<Juice> juices) {
        for (Juice juice : juices) {
            juice.imageId = JuiceDecorator.matchImage(juice.name);
            juice.kanId = JuiceDecorator.matchKannadaName(juice.name);
        }
    }

    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }


    private void gotoSwipingScreen(int position) {
        gotoSwipingScreen(new JuiceItem[]{(JuiceItem) adapter.getItem(position)});
    }

    private void gotoSwipingScreen(JuiceItem[] juiceItems) {
        Intent intent = new Intent(FruitsMenuActivity.this, UserInputActivity.class);
        intent.putExtra("juices", juiceItems);
        startActivity(intent);
        FruitsMenuActivity.this.finish();
    }

    boolean H = new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            finish();
        }
    }, 10000);
}