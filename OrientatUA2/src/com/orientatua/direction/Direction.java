package com.orientatua.direction;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Direction {
	private String status;
	private ArrayList<Route> routes;	
	
	public void setStatus(String status) {
		this.status=status;
	}
	
	public String getStatus() {
		return status;
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(ArrayList<Route> routes) {
		this.routes = routes;
	}
	
	public ArrayList<Step> getSteps() {
		return routes.get(0).getLegs().get(0).getSteps();
	}
}
