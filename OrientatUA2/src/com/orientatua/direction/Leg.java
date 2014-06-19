package com.orientatua.direction;

import java.util.ArrayList;
import java.util.List;

public class Leg {
	private Unit distance;
	private Unit duration;
	private ArrayList<Step> steps;
	
	public Unit getDistance() {
		return distance;
	}
	public void setDistance(Unit distance) {
		this.distance = distance;
	}
	public Unit getDuration() {
		return duration;
	}
	public void setDuration(Unit duration) {
		this.duration = duration;
	}
	public ArrayList<Step> getSteps() {
		return steps;
	}
	public void setSteps(ArrayList<Step> steps) {
		this.steps = steps;
	}
	
	
}
