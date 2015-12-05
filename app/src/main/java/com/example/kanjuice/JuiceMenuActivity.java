package com.example.kanjuice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class JuiceMenuActivity extends Activity {

    private static final String TAG = "JuiceMenuActivity";
    private JuiceAdapter adapter;
    private boolean isInMultiSelectMode = false;
    private View goButton;
    private View cancelButton;
    private View actionButtonLayout;
    private View noNetworkView;
    private GridView juicesView;
    private View menuLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE);
        String selectedRegion = sharedPreferences.getString("selectedRegion", null);

        setContentView(R.layout.activity_juice_menu);

        setupViews();

        startBluetoothDataReaderService();
    }

    private void startBluetoothDataReaderService() {
        startService(new Intent(this, BluetoothReaderService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableRecentAppsClick();
    }

    @Override
    protected void onResume() {
        super.onResume();

        exitMultiSelectMode();

        fetchMenu();
    }

    @Override
    public void onBackPressed() {
        if (isInMultiSelectMode) {
            exitMultiSelectMode();
        }

        // else block back button
    }

    private void disableRecentAppsClick() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    private void fetchMenu() {
        getJuiceServer().getJuices(new Callback<List<Juice>>() {
            @Override
            public void success(final List<Juice> juices, Response response) {
                JuiceMenuActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        menuLoadingView.setVisibility(View.GONE);
                        juicesView.setVisibility(View.VISIBLE);
                        onJuicesListReceived(juices);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch menu list : " + error);
                JuiceMenuActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNoNetworkView();
                    }
                });

            }
        });
    }

    private void showNoNetworkView() {
        noNetworkView.setVisibility(View.VISIBLE);
        juicesView.setVisibility(View.GONE);
        menuLoadingView.setVisibility(View.GONE);
    }


    private void onJuicesListReceived(List<Juice> juices) {
        decorate(juices);
        adapter.addAll(juices);
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

    private void setupViews() {
        juicesView = (GridView) findViewById(R.id.grid);
        setupAdapter(juicesView);

        juicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onJuiceItemClick(position);
            }
        });

        juicesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onJuiceItemLongClick(position);
            }
        });

        setupActionLayout();
        setupNoNetworkLayout();

        menuLoadingView = findViewById(R.id.loading);
    }

    private void setupNoNetworkLayout() {
        noNetworkView = findViewById(R.id.no_network_layout);
        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLoadingView.setVisibility(View.VISIBLE);
                noNetworkView.setVisibility(View.INVISIBLE);
                fetchMenu();
            }
        });
    }

    private boolean onJuiceItemLongClick(int position) {
        if(isRegisterActivity(position)) {
            return false;
        }
        adapter.toggleSelectionChoice(position);
        if (!isInMultiSelectMode) {
            enterMultiSelectionMode();
        }
        return true;
    }

    private void onJuiceItemClick(int position) {
        if(isRegisterActivity(position)) {
            Intent intent = new Intent(JuiceMenuActivity.this, CardSwipeActivity.class);
            startActivity(intent);
        }
        else {
            if (isInMultiSelectMode) {
                adapter.toggleSelectionChoice(position);
            } else {
                gotoSwipingScreen(position);
            }
        }
    }

    private boolean isRegisterActivity(int position) {
        return ((JuiceItem) adapter.getItem(position)).juiceName.equals("Register User");
    }

    private void setupActionLayout() {
        actionButtonLayout = findViewById(R.id.action_button_layout);

        goButton = actionButtonLayout.findViewById(R.id.order);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSwipingScreen();
            }
        });

        cancelButton = actionButtonLayout.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitMultiSelectMode();
            }
        });
    }

    private void setupAdapter(GridView juicesView) {
        adapter = new JuiceAdapter(this);
        juicesView.setAdapter(adapter);
    }

    private void exitMultiSelectMode() {
        adapter.reset();

        isInMultiSelectMode = false;

        ObjectAnimator anim = ObjectAnimator.ofFloat(actionButtonLayout, "translationY", -20f, 200f);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                actionButtonLayout.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    private void enterMultiSelectionMode() {
        isInMultiSelectMode = true;

        actionButtonLayout.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(actionButtonLayout, "translationY", 100f, -15f);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                actionButtonLayout.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    private void gotoSwipingScreen() {
        gotoSwipingScreen(adapter.getSelectedJuices());
    }

    private void gotoSwipingScreen(int position) {
        gotoSwipingScreen(new JuiceItem[]{ (JuiceItem) adapter.getItem(position)});
    }

    private void gotoSwipingScreen(JuiceItem[] juiceItems) {
        if (isFruitsSection(juiceItems)) {
            showFruitsSection();
        } else {
            Intent intent = new Intent(JuiceMenuActivity.this, UserInputActivity.class);
            intent.putExtra("juices", juiceItems);
            startActivity(intent);
        }
    }

    private boolean isFruitsSection(JuiceItem[] juiceItems) {
        return juiceItems[0].juiceName.equals("Fruits");
    }

    private void showFruitsSection() {
        Intent intent = new Intent(JuiceMenuActivity.this, FruitsMenuActivity.class);
        startActivity(intent);
    }
}
