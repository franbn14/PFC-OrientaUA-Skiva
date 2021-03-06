package com.orientatua.direction;

import java.util.Locale;

public enum Orientation {
	NORTH("norte"),
	NORTH_EAST("nordeste"),
	EAST("este"),
	SOUTH_EAST("sureste"),
	SOUTH("sur"),
	SOUTH_WEST("suroeste"),
	WEST("oeste"),	
	NORTH_WEST("noroeste"),
	
	LEFT("Gira la izquierda"),
	LIGHT_LEFT("Gira ligeramente a la izquierda"),
	TURN_AND_LEFT("Date la vuelta y gira ligeramente a la izquierda"),
	RIGHT("Gira a la derecha"),	
	LIGHT_RIGHT("Gira ligeramente a la derecha"),
	TURN_AND_RIGHT("Date la vuelta y gira ligeramente a la derecha"),
	TURN("Date la vuelta"),
	STAY(""),
	ERROR("Error");
	
	private String value;
	
	private Orientation(String _value) {
		value=_value;
	}
	
	public String getValue() {
		return value;
	}
	 
	@Override
	public String toString() {
		return value;
	}
	
	public static boolean contains(String direction) {
		for(Orientation orientation: Orientation.values()) {
			if(orientation.getValue().equalsIgnoreCase(direction))
				return true;
		}
		return false;
	}
	
	public static Orientation getValueOf(String direction) {
		for(Orientation value: values()) {
			if(value.getValue().equalsIgnoreCase(direction))
				return value;
		}
		return Orientation.ERROR;
	}
}
