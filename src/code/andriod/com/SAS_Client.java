package code.andriod.com;


import Firstdroid.Tutorial.Gps.Locator;
import Firstdroid.Tutorial.Gps.R;
import Firstdroid.Tutorial.Gps.SMSSender;
import Firstdroid.Tutorial.Gps.Locator.BetterLocationListener;
import Firstdroid.Tutorial.Gps.R.id;
import Firstdroid.Tutorial.Gps.R.layout;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;


public class SAS_Client extends Activity
{
	private final static int STATE_X_OR_V = 0;
	private final static int STATE_V = 1;
	private final static int STATE_X = 2;
	
	private TextView latituteField;
	private TextView longitudeField;
	static String locationString = "";
	static int locationState = STATE_X_OR_V;
	
	Location location = null;
	Locator locator = null;
	
	SMSSender sender;
	
	
/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		latituteField = (TextView) findViewById(R.id.TextView02);
		longitudeField = (TextView) findViewById(R.id.TextView04);
		
		sender = new SMSSender();
//		
//		// Get the location manager
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		// Define the criteria how to select the locatioin provider -> use
//		// default
//		Criteria criteria = new Criteria();
//		provider = locationManager.getBestProvider(criteria, false);
////		Location location = locationManager.getLastKnownLocation(provider);
//		Location location = locationManager.getLastKnownLocation(provider);

		startLocator();
		
		// Initialize the location fields
//		for (int i=0 ; i < 60 ; i++)
//		{
//			try 
//			{
//				Thread.sleep(1000);
//	        } 
//			
//			catch (InterruptedException e) 
//			{
//	            e.printStackTrace();
//	        }
//		}
			
		while (location==null);
	
				double lat = location.getLatitude();
				double lng = location.getLongitude();
			
				latituteField.setText(String.valueOf(lat));
				longitudeField.setText(String.valueOf(lng));
				
				
		sender.sendSMS(dest, altit, longit, body)

//		if (location != null) 
//		{
//		double lat = location.getLatitude();
//		double lng = location.getLongitude();
//	
//		latituteField.setText(String.valueOf(lat));
//		longitudeField.setText(String.valueOf(lng));
//		} 
//		
//		else 
//		{
//			latituteField.setText("Provider not available");
//			longitudeField.setText("Provider not available");
//		}
}

	private void startLocator() {
		final Context context = this;
		if (this.locator != null) {
			Log.e("EmergencyActivity", "locator exists while lock is open");
		}
		this.locator = new Locator(context,	new EmergencyLocator());
	}
	
	private class EmergencyLocator implements Locator.BetterLocationListener {
		public void onGoodLocation(Location location) {
			Log.v("Emergency", "got a location");
			if (location != null) {
				Log.v("Emergency", "in onGoodLocation and got a location");
				setLocationState("Location found", STATE_V);
			} else {
				Log.v("Emergency", "in onGoodLocation and didn't got a location");
				setLocationState("No location info, sending anyway.", STATE_X);
			}
			
			SAS_Client.this.location = location;
		}
	}
	
	
	private void requestGPSDialog() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		}
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Yout GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									final DialogInterface dialog,
									final int id) {
								
								showGpsOptions();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	
	private void setLocationState(String locationString, int locationState) {
		SAS_Client.locationString = locationString;
		SAS_Client.locationState = locationState;
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
		requestGPSDialog();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	
}