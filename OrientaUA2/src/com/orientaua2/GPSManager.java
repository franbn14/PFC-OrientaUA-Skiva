package com.orientaua2;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.location.Geocoder; 
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GPSManager extends Service implements LocationListener {
	
	private final Context context;
    private boolean GPSEnabled;
    private boolean networkEnabled;
    private boolean canGetLocation;
    private Location location; 
    private double latitude; 
    private double longitude;
    
    private LocationManager manager;
    private Geocoder geocoder;
    
    private static final long MIN_DISTANCE = 10;    
    private static final long MIN_TIME = 1000 * 60 * 1; // 1 minute
    
    public GPSManager(Context mContext) {		    	    
		context = mContext;
		GPSEnabled = false;
		networkEnabled = false;
		canGetLocation = false;
		setLocation();		
	}
    
    
    
    public void setLocation() {
    	manager =  (LocationManager) context.getSystemService(LOCATION_SERVICE);
    	
    	GPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    	networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    	
    	if(GPSEnabled || networkEnabled) {
    		canGetLocation=true;
    		
    		if(networkEnabled) {
    			setLocationParameters(LocationManager.NETWORK_PROVIDER);
    			//Toast.makeText(getApplicationContext(), "Por Red", Toast.LENGTH_LONG).show();
    		}
    		
    		if(GPSEnabled && location==null) {
    			setLocationParameters(LocationManager.GPS_PROVIDER);
    			//Toast.makeText(getApplicationContext(), "Por GPS", Toast.LENGTH_LONG).show();
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
    	AlertDialog.Builder alert = new AlertDialog.Builder(context);
    	alert.setTitle("Ajustes de GPS");
    	alert.setMessage("El GPS no est� activado. �Quiere al men� de ajustes?");
    	
    	alert.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
    	
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
    	alert.show();    	
    }
    
    public String getAddress(double latitude, double longitude) {
    	geocoder=new Geocoder(context,Locale.getDefault());
    	String result="Localizaci�n no encontrada";
    	
    	if(Geocoder.isPresent()) {    	
        	try {
    			List<Address> addresses=geocoder.getFromLocation(latitude, longitude, 1);
    			
    			if(addresses.size()>0) {
    				Address address=addresses.get(0);
    				StringBuilder str=new StringBuilder();
    				
    				for(int i=0; i<address.getMaxAddressLineIndex(); i++)
    					str.append(address.getAddressLine(i)).append("\n");
    				
    				str.append(address.getLocality()).append("\n");
    				str.append(address.getPostalCode()).append("\n");
    				str.append(address.getCountryName()); 
    				
    				result=str.toString();
    			}			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			System.err.println("EXCEPCION");
    		}
    	}
    	else
    		result="Servicio no disponible";
    	
    	return result;    	
    }
    
    public String getCoordinates(String name) {
    	geocoder=new Geocoder(context,Locale.getDefault());
    	String result="Localizaci�n no encontrada";
    	    	   
    	if(Geocoder.isPresent()) {    	
        	try {
    			List<Address> addresses=geocoder.getFromLocationName(name, 1);
    			
    			if(addresses.size()>0) {
    				Address address=addresses.get(0);
    				   				
    				result=address.getLatitude()+","+address.getLongitude();
    			}			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			System.err.println("EXCEPCION");
    		}
    	}
    	else
    		result="Servicio no disponible";
    	
    	return result;    	
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
		setLocation();
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