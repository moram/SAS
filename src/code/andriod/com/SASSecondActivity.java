package code.andriod.com;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import Firstdroid.Tutorial.Gps.Locator;


public class SASSecondActivity extends Activity //implements Runnable 
{
	Location location = null;
	Locator locator = null;
	
	SMSSender sender;
	int counter = 0; //the starting counter for the maxWaitingTime
	int maxWaitingTime = 60; //max waiting time for obtain the location
	
	TextView tv, gettingLocationTextView, sendingSmsTextView; 
	long startTime;
	private Handler handler = new Handler();
	
	SASClientData temp;
    ImageView rotateGlobus;
    RotateAnimation mRotateAnimation;
    boolean foundLocation;
    boolean newLocation;
    ImageView imageLocationStatus, imageSmsStatus;
    
    private ProgressBar progressBar;
    private int progressStatus = 0;
        
    
	public void onCreate(Bundle savedInstanceState) 
	{
		Log.v("Emergency", "in ON CREATE");
		super.onCreate(savedInstanceState);
		
		sender = new SMSSender();
        
        foundLocation = false;
        
        
        
        setContentView(R.layout.sending_values);
        
        rotateGlobus = (ImageView) findViewById(R.id.globus);
        mRotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setDuration(700);
        rotateGlobus.startAnimation(mRotateAnimation);

        
    	temp = new SASClientData(this);
    	foundLocation = false;
    	newLocation = false;
    	
         
        startLocator();
        
        requestGPSDialog();

               
        /*
         * this thread is responsible on parsing the location and when find stop to waiting and send the SMS.
         */
        new Thread(new Runnable() 
        {
        	 	
    		public void run() 
            {
    			tv = (TextView) findViewById(R.id.location_info);
    			gettingLocationTextView = (TextView) findViewById(R.id.getting_location_txt);
    			sendingSmsTextView = (TextView) findViewById(R.id.sending_sms_txt);
    			imageLocationStatus = (ImageView)findViewById(R.id.imgLocation);
    			imageSmsStatus = (ImageView)findViewById(R.id.img_sms);
    			
    			startTime = System.currentTimeMillis();
    			progressBar = (ProgressBar) findViewById(R.id.progressbar);
    			
    			    			
            	while (counter < maxWaitingTime && !foundLocation) 
            	{
            		counter = waitTime(); //increase the counter by 1 second based on the waitTime() function.
            		                                 	   
	                handler.post(new Runnable() 
	                {
	                    public void run() 
	                    {
	                    	progressBar.setProgress(progressStatus);
	                    	
	                    	if (location != null) //if found the location
	                    	{
	                    		Thread.interrupted(); //stop all the threads
	                    		foundLocation = true; //
	                    		gettingLocationTextView.setText("");
	                    		mRotateAnimation.cancel(); //stop the globus animation
	                    		mRotateAnimation.reset();
	                    		progressBar.setVisibility(8); //hide the progress bar 
	                    		
	                    		Long timeFromLastFix = (location.getTime() - startTime) / 1000;
	                        	
	                    		if (timeFromLastFix < 0) //if there is no newer location
	                        		locationIconFalse(); //update the icon to red X
	                        		                        	
	                        	else //we got newer location
	                        	{
	                        		newLocation = true;
	                        		locationIconTrue(); //update the icon to green V
	                           	} //else
       		            	
	                    	} //if
	                    	
	                    	if (counter >= maxWaitingTime && !foundLocation) //if the max time is over and no location found
	                    	{
	                    		gettingLocationTextView.setText("");
	                    		progressBar.setVisibility(8);
	                    		locationIconFalse();
	                    		mRotateAnimation.cancel();
	                    		mRotateAnimation.reset();
	                    	}
	                    
	                    } //run
	                
	                }); //handler post Runnable
            	
            	} //while
            	
            	//after the while loop is finished I want to perform the following action
            	handler.post(new Runnable() 
                {
                    public void run() 
                    {
                    	if (location == null) //if we don't have any location at all also in memory
                		{
                			tv.setText("phone: " + temp.getPhone() + 
                					"\n" + "Latitude: None" + 
                					"\n" + "Longitude: None" +
                					"\n" + "Provider: None" +
                					"\n" + "Msg: " + temp.getMessage());
                			
                			if (temp.getSendSms()) //if checked the send sms checkBox
                			{
//                				Toast.makeText(SASSecondActivity.this, "checkBox = " + temp.getSendSms() , Toast.LENGTH_SHORT).show();
                				sender.sendSMS(temp.getPhone(), "None", "None", "None", temp.getMessage());
                			
                				smsIconTrue();
                			} //if
                			
                			else
                			{
                				smsIconFalse();
                			}
                			
                		} //if
                    	
                    	
                    	
                    	else //if got new location during the while loop
                        {
                    		if (newLocation) //prepare the text and message and send them
	                		{
                    			tv.setText("phone: " + temp.getPhone() + 
	                					"\n" + "Latitude: " + location.getLatitude() +
	                					"\n" + "Longitude: " + location.getLongitude() + 
	                					"\n" + "Provider: " + location.getProvider() +
	                					"\n" + "Msg: " + temp.getMessage());
	                    	
	                        	if (temp.getSendSms()) //if checked the send sms checkBox
	                        	{
//	                        		Toast.makeText(SASSecondActivity.this, "checkBox = " + temp.getSendSms() , Toast.LENGTH_SHORT).show();
	                        		sender.sendSMS(temp.getPhone(),Double.toString(location.getLatitude()), 
	                        				Double.toString(location.getLongitude()),location.getProvider(),temp.getMessage());
	                        		smsIconTrue();
	                        	} //if
	                        	
	                        	else
	                        	{
	                        		smsIconFalse();
	                        	} //else
	                		}
                        
                    	   	else //if didn't get new location during the while loop but has location in memory
	                    	{
	                    			//calculate the latest location time
		                    		//prepare the text and message and send them
		                    		Long serverUptimeSeconds =(startTime - location.getTime()); 
		                			String serverUptimeText = 
		                			String.format("%d days %d hours %d min %d sec",
		                					((serverUptimeSeconds/(1000*60*60))%24)/24,
		                					(serverUptimeSeconds/(1000*60*60))%24,
		                					(serverUptimeSeconds/(1000*60))%60,
		                					(serverUptimeSeconds/1000)%60);
		                    		String msg = ("There is no new location" + 
		                    						"\n" + "The latest was before:" + "\n" + serverUptimeText +
		                    						"\n" + "Latitude: " + location.getLatitude() +
		                    						"\n" + "Longitude: " + location.getLongitude() +
		                    						"\n" + "Provider: " + location.getProvider()); 
		                    		
		                    		tv.setText("phone: " + temp.getPhone() + 
		                    					"\n" + "Latitude: None" + 
		                    					"\n" + "Longitude: None" +
		                    					"\n" + "Provider: None" +
		                    					"\n" + "Msg: " + temp.getMessage() + 
		                    					"\n\n" + msg);
		                    		
		                    		
		                    		if (temp.getSendSms()) //if checked the send sms checkBox
		                    		{
//		                    			Toast.makeText(SASSecondActivity.this, "checkBox = " + temp.getSendSms() , Toast.LENGTH_SHORT).show();
		                    			sender.sendSMS(temp.getPhone(), "None", "None", "None", temp.getMessage() + msg);
		                    		
		                    			smsIconTrue();
		                    		} //if
		                    		
		                    		else
		                    		{
		                    			smsIconFalse();
		                    		} //else
	                    	} //else

                        } //else
                        
                    } //run
                    
                }); //handler runnable
            		
            } //run 
    		

 
	        // this is the waiting time function
	        private int waitTime() 
	        {
	            try 
	            {
	                Thread.sleep(1000);
	            } 
	            
	            catch (InterruptedException e) 
	            {
	                e.printStackTrace();
	            }
	            
	            ++progressStatus; //update the progress bar by 2 because progress bar need to get to 100 andmy locator  
	            ++progressStatus; //is until 50 seconds
	            return ++counter; 
	        }
	        
	        
	        // this function responsible to replace the Location icon to V
	        private void locationIconTrue()
	    	{
	    		imageLocationStatus.setBackgroundColor(android.R.color.transparent);
	    		imageLocationStatus.setImageResource(R.drawable.green_v_70_70);
	    	}
	    	
	        // this function responsible to replace the Location icon to X
	    	private void locationIconFalse()
	    	{
	    		imageLocationStatus.setBackgroundColor(android.R.color.transparent);
	    		imageLocationStatus.setImageResource(R.drawable.red_x_75_75);
	    	}
	    	
	    	// this function responsible to replace the SMS icon to V
	        private void smsIconTrue()
	    	{
	        	sendingSmsTextView.setText("");
	        	imageSmsStatus.setBackgroundColor(android.R.color.transparent);
	    		imageSmsStatus.setImageResource(R.drawable.green_v_70_70);
	    	}
	    	
	        // this function responsible to replace the SMS icon to X
	    	private void smsIconFalse()
	    	{
	        	sendingSmsTextView.setText("");
	    		imageSmsStatus.setBackgroundColor(android.R.color.transparent);
	    		imageSmsStatus.setImageResource(R.drawable.red_x_75_75);
	    	}

        }).start();  //Thread Runnable
        
	}
    
//        requestGPSDialog();
	
	private void startLocator() {
		final Context context = this;
		if (this.locator != null)
		{
			Log.e("EmergencyActivity", "locator exists while lock is open");
		}
		this.locator = new Locator(context,	new EmergencyLocator());
	}
	
	private class EmergencyLocator implements Locator.BetterLocationListener 
	{
		public void onGoodLocation(Location location) 
		{
			SASSecondActivity.this.location = location;
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
	

}

/*		
	
	seconds=(serverUptimeSeconds/1000)%60
	minutes=(serverUptimeSeconds/(1000*60))%60
	hours=(serverUptimeSeconds/(1000*60*60))%24
	days=((serverUptimeSeconds/(1000*60*60))%24)/24
*/





