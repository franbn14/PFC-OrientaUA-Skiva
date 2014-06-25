package com.orientatua.direction;

import android.location.Location;

public class StepLocation extends Location {
	private double lat;
	private double lng;
	
	public StepLocation(Location l) {
		super(l); 
	}
		
	@Override
	public double getLatitude() {
		return lat;
	}
	
	@Override
	public double getLongitude() {
		return lng;
	}
}
