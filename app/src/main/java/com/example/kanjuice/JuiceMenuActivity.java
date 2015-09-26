package com.example.kanjuice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


public class JuiceMenuActivity extends Activity  {

    private static final String TAG = "JuiceMenuActivity";
    private JuiceAdapter adapter;
    private boolean isInMultiSelectMode = false;
    private View goButton;
    private View cancelButton;
    private View actionButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_juice_menu);

        setupViews();
    }

    private void setupViews() {
        final GridView juicesView = (GridView) findViewById(R.id.grid);
        adapter = new JuiceAdapter(this);
        juicesView.setAdapter(adapter);

        juicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isInMultiSelectMode) {
                    adapter.toggleSelectionChoice(position);
                } else {
                    gotoSwipingScreen(position);
                }
            }
        });

        juicesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onIemLongClick: " + position);
                adapter.toggleSelectionChoice(position);

                if (!isInMultiSelectMode) {
                    enterMultiSelectionMode();
                }
                return true;
            }
        });

        actionButtonLayout = findViewById(R.id.action_button_layout);

        goButton = findViewById(R.id.order);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSwipingScreen();
            }
        });

        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitMultiSelectMode(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        exitMultiSelectMode(false);
    }

    @Override
    public void onBackPressed() {
        if (isInMultiSelectMode) {
            exitMultiSelectMode(true);
        } else {
            super.onBackPressed();
        }
    }

    private void exitMultiSelectMode(boolean animate) {
        adapter.reset(animate);

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
        gotoSwipingScreen(new Juice[]{ (Juice) adapter.getItem(position)});
    }

    private void gotoSwipingScreen(Juice[] juices) {
        Intent intent = new Intent(JuiceMenuActivity.this, UserInputActivity.class);
        intent.putExtra("juices", juices);
        startActivity(intent);
    }
}
