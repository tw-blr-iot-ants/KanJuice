package com.example.kanjuice;

import org.apache.cordova.DroidGap;

import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends DroidGap {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//super.loadUrl("http://10.4.23.75:8000/menu.html");

		setContentView(R.layout.activity_main);

		super.setIntegerProperty("loadUrlTimeoutValue", 60000);
		super.loadUrl("file:///android_asset/www/menu.html", 1000);
	}

}
