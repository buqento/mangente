package com.wikitude.example;

/**
 * Created by buqento on 10/5/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends Activity {
	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	Boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		cekInternet();
	}

	@Override
	protected void onResume(){
		super.onResume();
		this.doubleBackToExitPressedOnce = false;
	}
//	@Override
//	public void onBackPressed(){
//		if (back_pressed + 2000 > System.currentTimeMillis())
//			super.onBackPressed();
//		else
//			Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
//		back_pressed = System.currentTimeMillis();
//	}

	public void cariLokasi(final View view){
		Intent cari = new Intent(MenuActivity.this, ARBrowserActivity.class);
		startActivity(cari);
	}

	public void tampilPeta(final View view){
		Intent cari = new Intent(MenuActivity.this, ListTour.class);
		startActivity(cari);
	}

	public void cekInternet(){
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			Toast.makeText(getBaseContext(), "Tidak terhubung jaringan internet", Toast.LENGTH_LONG).show();
		}
	}
}


