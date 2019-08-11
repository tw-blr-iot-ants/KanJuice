package com.example.kanjuice.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kanjuice.R;

public class RegionSelectionActivity extends Activity{

    public static final String SELECTED_REGION = "selectedRegion";
    private RadioGroup regionsChoice;
    private String selectedRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.region_selection);
        setUpViews();
    }

    private void setUpViews() {
        regionsChoice = (RadioGroup) findViewById(R.id.regions);
        regionsChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                selectedRegion = ((RadioButton) findViewById(checkedId)).getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(SELECTED_REGION, selectedRegion).commit();
                gotoJuiceMenuActivity();
            }
        });
    }

    private void gotoJuiceMenuActivity() {
        Intent intent = new Intent(RegionSelectionActivity.this, JuiceMenuActivity.class);
        startActivity(intent);
    }

}
