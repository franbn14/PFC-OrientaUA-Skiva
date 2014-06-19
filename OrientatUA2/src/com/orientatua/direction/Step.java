package com.orientatua.direction;

public class Step {
	private Unit distance;
	private Unit duration;
	private String html_instructions;

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
	public String getInstruction() {
		return html_instructions;
	}
	public void setInsruction(String html_insructions) {
		this.html_instructions = html_insructions;
	}
}
