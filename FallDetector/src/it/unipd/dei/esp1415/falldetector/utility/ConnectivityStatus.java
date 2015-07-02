/**
 * 
 */
package it.unipd.dei.esp1415.falldetector.utility;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class control if location and wi-fi or mobile connection are accessible.
 */
public class ConnectivityStatus {
	
	private Context mCtx;
	private ConnectivityManager mConMan;
	private LocationManager mLocMan;
	
	/**
	 * [c]
	 * @param context to initialize system objs 
	 */
	public ConnectivityStatus(Context context){
		mCtx = context;
		mConMan = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
		mLocMan = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);
		
	}//[c] ConnectivityUtil()
	
	/**
	 * [m]
	 * @return true if a mobile connection is available, false otherwise
	 */
	public boolean isMobileConnectionAvailable(){
		if(mConMan == null){
			return false;
		}
		
		NetworkInfo mobileInfo = mConMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobileInfo != null && mobileInfo.isAvailable();
	}//[m] isMobileConnectionAvailable()
	
	/**
	 * [m]
	 * @return true if a mobile connection is available and is connected, false otherwise
	 */
	public boolean hasMobileConnectionON(){
		if(mConMan == null){
			return false;
		}
		
		NetworkInfo mobileInfo = mConMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobileInfo != null && mobileInfo.isAvailable() && mobileInfo.isConnected();
	}//[m] hasMobileConnectionON()
	
	/**
	 * [m]
	 * @return true if a wi-fi connection is available and is connected, false otherwise
	 */
	public boolean hasWifiConnectionON(){
		if(mConMan == null){
			return false;
		}
		
		NetworkInfo wifiInfo = mConMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		return wifiInfo != null && wifiInfo.isAvailable() && wifiInfo.isConnected();
	}//[m] hasWifiConnectionON()
	
	/**
	 * [m]
	 * @return true if a location system base on GPS is active, false otherwise
	 */
	public boolean hasLocationON(){
		return mLocMan != null && mLocMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}//[m] hasLocationON()
}
