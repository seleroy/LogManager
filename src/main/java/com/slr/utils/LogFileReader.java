package com.slr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.slr.LogManager;
import com.slr.model.EventLogLine;

public final class LogFileReader {
	
	public Map<String, List<EventLogLine>> readFromFile (String inputPath) {
		File file = new File(inputPath);
		return readFromFile(file);
	}
	 
	/**
	 * Reads a log event files and return a map of events grouped by id
	 * @param file
	 * @return map of events grouped by ID
	 */
	private Map<String, List<EventLogLine>> readFromFile(File file) {
		Map<String, List<EventLogLine>> eventMap = new HashMap<>();
		JSONParser parser = new JSONParser();
		try (Reader is = new FileReader(file)) {

	        BufferedReader bufferedReader = new BufferedReader(is);
	        
	        String currentLine;
	        while((currentLine=bufferedReader.readLine()) != null) {
	            JSONObject logLine = (JSONObject) parser.parse(currentLine);
	            EventLogLine event = new EventLogLine(logLine);
	            String id = event.getId();
	            if (eventMap.containsKey(id)) {
	            	eventMap.get(id).add(event);
	            } else {
	            	List<EventLogLine> eventList = new ArrayList<>();
	            	eventList.add(event);
	            	eventMap.put(id, eventList);
	            }
	        }
	        
		} catch (IOException | ParseException e) {
			LogManager.LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException("Error with input file:"+e);
		} 

		return eventMap;
	}
}
