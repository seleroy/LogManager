package com.slr.model;

import java.util.Optional;
import org.json.simple.JSONObject;

/**
 * This class represents a line from the input log file
 * @author Sebastien
 *
 */
public class EventLogLine {
	
	public static final String JSON_ID = "id";
	public static final String JSON_STATE = "state";
	public static final String JSON_TYPE = "type";
	public static final String JSON_HOST = "host";
	public static final String JSON_TIMESTAMP = "timestamp";
		
	private String id;
	private EventState state;
	private long timestamp;
	private Optional<String> type;
	private Optional<String> host;
	
	
	public EventLogLine(JSONObject jsonObject) {
		this.id = (String) jsonObject.get(JSON_ID);
		this.state = EventState.getFromString((String) jsonObject.get(JSON_STATE));
		this.timestamp = (long) jsonObject.get(JSON_TIMESTAMP);
		this.type = Optional.ofNullable((String) jsonObject.get(JSON_TYPE));
		this.host = Optional.ofNullable((String) jsonObject.get(JSON_HOST));		
	}


	public String getId() {
		return id;
	}


	public EventState getState() {
		return state;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public Optional<String> getType() {
		return type;
	}


	public Optional<String> getHost() {
		return host;
	}
}
