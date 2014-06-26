package com.orientatua.direction;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class Compass implements SensorEventListener{
	private SensorManager sensorManager;	 
	private Sensor orientation;
	private Context context;
	private Float azimut;	
	
	public Compass(Context _context, SensorManager manager) {
		context=_context;
		sensorManager=manager;			   
	    orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION ), SensorManager.SENSOR_DELAY_NORMAL);	    
	}
	
	public Float getAzimut() {
		return azimut;
	}
	
	public Orientation getCardinalPoint()
    {				
		if(azimut!=null) {
			if((azimut>=0 && azimut<22.5) || (azimut<=360 && azimut>=337.5))
	    		return Orientation.NORTH;
	    	
	    	if((azimut>=22.5 && azimut<67.5))
	    		return Orientation.NORTH_EAST;
	    	
	    	if((azimut>=67.5 && azimut<112.5))
	    		return Orientation.EAST;
	    	
	    	if((azimut>=112.5 && azimut<157.5))
	    		return Orientation.SOUTH_EAST;
	    	
	    	if((azimut>=157.5 && azimut<202.5))
	    		return Orientation.SOUTH;
	    	
	    	if((azimut>=202.5 && azimut<247.5))
	    		return Orientation.SOUTH_WEST;
	    	
	    	if((azimut>=247.5 && azimut<292.5))
	    		return Orientation.WEST;
	    	
	    	if((azimut>=292.5 && azimut<337.5))
	    		return Orientation.NORTH_WEST;
		}    	    	    	
		else 
			Toast.makeText(context, "Azimut nulo", Toast.LENGTH_SHORT).show();
    	return Orientation.ERROR;
    }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
	       switch (event.sensor.getType()) {
	          case Sensor.TYPE_ORIENTATION: 
	        	  azimut=event.values[0];
	              break;	               	          	           
	          }
	      }		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
