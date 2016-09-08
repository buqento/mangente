package com.wikitude.example;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.wikitude.architect.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * 
 * @author Wikitude
 * @date JAN 2012
 * 
 * @class SimpleARBrowserActivity
 * 
 *        sample application to show how to use the ARchitect SDK loads simple
 *        pois via javascript into the ARchitect world and displays them
 *        accordingly displays a bubble with information about the selected poi
 *        on the screen and displays a detail page when the bubble is clicked
 *        uses Android's LocationManager to get updates on the user's location
 * 
 *        important is that the methods of the activity lifecycle are forwarded
 *        to the ArchitectView Important methods: onPostCreate() onResume()
 *        onPause() onDestroy() onLowMemory()
 * 
 *        Please also have a look at the application's Manifest and layout
 *        xml-file to see the permissions and requirements an activity using the
 *        SDK has to possess. (REF: ARchitect Documentation)
 */
public class ARBrowserActivity extends Activity implements
		ArchitectUrlListener, LocationListener {

	private static final String TAG = ARBrowserActivity.class
			.getSimpleName();

	private final static float TEST_LATITUDE = -3.696541f;
	private final static float TEST_LONGITUDE = 128.178398f;
	private final static float TEST_ALTITUDE = 0;
	public double fLat, fLng;

	private String apiKey = "n+DtduXJkBa4hwW4Yhfhl6VjAbR0s8Bu+cLAvUYkENtRNfOIL96dDpAK1saHrVCG8D2IR2elw/AZda7r+Z9Gi9OhV/p+4qrNDctU0FRJipzBmGAC7A3Ro74mTk3uvPBv4RKF62H1e5bQbBpw669Jm+1ML9i1aEa9XBTtVrKtaNxTYWx0ZWRfXz3NUfI/Oou3sPI6XQQqnn8jxfaY39n7P/WT3wUj6AHLQa44pS5bVkk+YIUYiu5lrn2DFtG6wNQPk1KgOngpWihJH4IH3xstZl/CJHd6xPI279toJrakn5FWdL3LtDObTtWFI5qCuJttCRiWiZ/hd1lLx7BYyDTxhXCotN+ph5keUquN/cKNQjSJ/AnlvBcDV7NMmqBmBFzi2wJhte1WHnr80OjAw1oBPVT2+uUSCJxX5UyHygGx9qbvFgFVHclrXdalGqOwqQNauKiZF5QslSMfMYgFdWOvQgjDN1RbfTkUaaHJrW36nz2pz2JH2rVlQNN6P6EZZcOViF7H0L4MMQtm3+EqNE/4QEcW/Ir5e6hOzEeXZUx9LlRe8tIoxf50HhR8RfHKmjY0D9bDtVEDQyGD7NjPVJL+fddoEvTlrP5O5TaUSYC3BEd8uXTMxpUFVMfaEezbRQ/lcAF96gSmbkY1DHwgExsqiHs81Czbmfu+GOj6S2mnVDxnBsUF9ZXhg6+GM+0Uqfyk";

	private ArchitectView architectView;
	private LocationManager locManager;
	private Location loc;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// let the application be fullscreen
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// check if the device fulfills the SDK'S minimum requirements
		if (!ArchitectView.isDeviceSupported(this)) {
			Toast.makeText(this, "minimum requirements not fulfilled",
					Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		setContentView(R.layout.main);

		// set the devices' volume control to music to be able to change the
		// volume of possible soundfiles to play
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.architectView = (ArchitectView) this
				.findViewById(R.id.architectView);
		// onCreate method for setting the license key for the SDK
		architectView.onCreate(apiKey);

		// in order to inform the ARchitect framework about the user's location
		// Androids LocationManager is used in this case
		// NOT USED IN THIS EXAMPLE
		// locManager =
		// (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// locManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0,
		// 0, this);

		// Getting LocationManager object from System Service LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);

		locationManager.requestLocationUpdates(provider, 20000, 0, this);

		// Getting latitude of the current location
		fLat = location.getLatitude();

		// Getting longitude of the current location
		fLng = location.getLongitude();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// IMPORTANT: creates ARchitect core modules
		if (this.architectView != null)
			this.architectView.onPostCreate();

		// register this activity as handler of "architectsdk://" urls
		this.architectView.registerUrlListener(this);

		try {
			loadSampleWorld();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		this.architectView.onResume();
		this.architectView.setLocation(TEST_LATITUDE, TEST_LONGITUDE, TEST_ALTITUDE, 1f);
//		this.architectView.setLocation(fLat, fLng, TEST_ALTITUDE, 1f);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (this.architectView != null)
			this.architectView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.architectView != null)
			this.architectView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (this.architectView != null)
			this.architectView.onLowMemory();
	}

	@Override
	public boolean urlWasInvoked(String url) {
		// parsing the retrieved url string
		List<NameValuePair> queryParams = URLEncodedUtils.parse(
				URI.create(url), "UTF-8");

		String id = "";
		// getting the values of the contained GET-parameters
		for (NameValuePair pair : queryParams) {
			if (pair.getName().equals("id")) {
				id = pair.getValue();
			}
		}

		return true;
	}

	private void loadSampleWorld() throws IOException {
		this.architectView.load("Wisata/index.html");
	}

	@Override
	public void onLocationChanged(Location loc) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

}