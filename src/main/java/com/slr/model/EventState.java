package com.slr.model;

public enum EventState {
	STARTED,
	FINISHED,
	UNDEFINED;
	
	public static EventState getFromString(String s) {
		switch(s) {
		case "STARTED":
		case "started":
			return STARTED;
		case "FINISHED":
		case "finished":
			return FINISHED;
		default:
			return UNDEFINED;	
		}
	}
}
