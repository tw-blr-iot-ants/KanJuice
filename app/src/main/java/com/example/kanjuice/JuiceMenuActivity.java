package com.example.kanjuice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


public class JuiceMenuActivity extends Activity  {

    private JuiceAdapter adapter;
    private boolean isInMultiSelectMode = false;
    private View goButton;
    private View cancelButton;

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
                    adapter.multiSelect(view, position);
                } else {
                    gotoSwipingScreen(position);
                }
            }
        });

        juicesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.multiSelect(view, position);
                enterMultiSelectionMode();
                return true;
            }
        });

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
                exitMultiSelectMode();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        exitMultiSelectMode();
    }

    @Override
    public void onBackPressed() {
        if (isInMultiSelectMode) {
            exitMultiSelectMode();
        } else {
            super.onBackPressed();
        }
    }

    private void exitMultiSelectMode() {
        adapter.reset();

        isInMultiSelectMode = false;
        goButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
    }

    private void enterMultiSelectionMode() {
        isInMultiSelectMode = true;
        goButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
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
