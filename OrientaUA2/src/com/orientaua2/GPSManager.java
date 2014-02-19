package com.orientaua2;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSManager extends Service implements LocationListener {
	
	private final Context mContext;
    private boolean GPSEnabled;
    private boolean networkEnabled;
    private boolean canGetLocation;
    private Location location; 
    private double latitude; 
    private double longitude;
    private LocationManager manager;
    
    private static final long MIN_DISTANCE = 10;    
    private static final long MIN_TIME = 1000 * 60 * 1; // 1 minute
    
    public GPSManager(Context mContext) {
		super();
		this.mContext = mContext;
		GPSEnabled = false;
		networkEnabled = false;
		canGetLocation = false;
		setLocation();
	}
    
    public void setLocation() {
    	manager =  (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    	
    	GPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    	networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    	
    	if(GPSEnabled || networkEnabled) {
    		canGetLocation=true;
    		
    		if(networkEnabled) {
    			setLocationParameters(LocationManager.NETWORK_PROVIDER);
    		}
    		
    		if(GPSEnabled && location==null) {
    			setLocationParameters(LocationManager.GPS_PROVIDER);
    		}
    	}    	
    }
    
    public void setLocationParameters(String provider) {
    	manager.requestLocationUpdates(provider,MIN_TIME,MIN_DISTANCE,this);
		Log.d(provider,provider);
		
		if (manager != null) {
            location = manager.getLastKnownLocation(provider);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }
    
    public void stopGPS() {
    	if(manager != null){
            manager.removeUpdates(this);
        }   
    }
    
    public void setSettings() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
    	alert.setTitle("Ajustes de GPS");
    	alert.setMessage("El GPS no está activado. ¿Quiere al menú de ajustes?");
    	
    	alert.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
    	
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
    	alert.show();    	
    }
    
    //Getters and setters
	public boolean canGetLocation() {
		return canGetLocation;
	}

	public void setCanGetLocation(boolean canGetLocation) {
		this.canGetLocation = canGetLocation;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) { 	
		this.location = location;
	}	

	public double getLatitude() {
		if(location != null)
			latitude = location.getLatitude();
		
		return latitude;
	}

	public double getLongitude() {
		if(location != null)
			longitude = location.getLongitude();
			
		return longitude;
	}
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	} 
	
}
