/*
 * in this class we are going to save the phone and massage.
 */
package code.andriod.com;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.Toast;

public class SASClientData {
	
	Context context;
	SharedPreferences settings;

	private static final String PREFS_NAME = "SASPrefsFile";
	private static final String PHONE = "phoneNo";
	private static final String MESSAGE = "message";
	private static final String SENDSMS = "true";
	private static final String LATITUDE = "0";
	private static final String LONGITUDE = "0";
	
	
	public SASClientData(Context context) 
	{
		this.context = context;
		this.settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public String getPhone() 
	{
		return settings.getString(PHONE, "");
	}
	
	public String getMessage() 
	{
		return settings.getString(MESSAGE, "");
	}
	
	public boolean getSendSms()
	{
		return settings.getBoolean(SENDSMS, true);
	}
	
	public String getLatitude() 
	{
		return settings.getString(LATITUDE, "");
	}
	
	public String getLongitude() 
	{
		return settings.getString(LONGITUDE, "");
	}
		
	private void commitString(String id, String value) 
	{
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(id, value);
		editor.commit();
	}
	

	private void commitBoolean(final boolean isChecked) {
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean(SENDSMS, isChecked);
	    editor.commit();
	}
	
	public void setPhone(String phone) 
	{
		commitString(PHONE, phone);
	}
	
	public void setMessage(String message) 
	{
		commitString(MESSAGE, message);
	}
	
	public void setSendSms(Boolean confirmToSendSms)
	{
		commitBoolean(confirmToSendSms);
	}
	
		
	public void setLatitude(String latitude) 
	{
		commitString(LATITUDE, latitude);
	}
	
	public void setLongitude(String longitude) 
	{
		commitString(LONGITUDE, longitude);
	}
}
