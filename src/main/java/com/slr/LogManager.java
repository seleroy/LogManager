package com.slr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.slr.model.EventLogLine;
import com.slr.model.EventState;
import com.slr.model.LogEvent;
import com.slr.utils.DBManager;
import com.slr.utils.LogFileReader;


public class LogManager {
	
	private static final int FLAG_EVENT_THRESHOLD_MS = 4;
	public static final Logger LOGGER = Logger.getLogger("LogManager");
	private static final String DEFAULT_INPUT_FILE = "src/main/resources/sample-input.json";
		
	private String inputPath;
	//Stores for each id, the list of events that happened. Ex: {"IdA" : [started, finished]}
	private Map<String, List<EventLogLine>> linesMap;
	//Stores the list of flagged events to write in DB:
	private List<LogEvent> flaggedEventList = new ArrayList<>();
	private boolean dropExistingTable = false;

	public LogManager(String inputPath) {
		this.inputPath = inputPath;		
	}
	
	public void setLinesMap(Map<String, List<EventLogLine>> linesMap) {
		this.linesMap = linesMap;
	}
	
	public List<LogEvent> getFlaggedEventList() {
		return flaggedEventList;
	}
	
	public void setDropExistingTable(boolean dropExistingTable) {
		this.dropExistingTable = dropExistingTable;
	}
	
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	
	private void initLogger() {
		//Configured to log in the console:
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.INFO);
	}

	/**
	 * Main business logic of the manager:
	 *  - Reads the log file from its path
	 *  - Check for flagged events
	 *  - If there are flagged events, write them in the db
	 */
	public void processLogEvents() {
		//initLogger();
		LogFileReader reader = new LogFileReader();
		this.linesMap = reader.readFromFile(inputPath);
		createFlaggedEvents();
		if (!this.flaggedEventList.isEmpty()) {
			DBManager dbmanager = new DBManager();
			dbmanager.startHSQLDB();
			if(dropExistingTable) {
				dbmanager.dropHSQLDBLTable();
			}
			dbmanager.createHSQLDBTable();
			LOGGER.info("Content of db at startup:\n"+dbmanager.readEvents());
			dbmanager.insertEvents(this.flaggedEventList);
			LOGGER.info("Content of db before stopping:\n"+dbmanager.readEvents());
			dbmanager.stopHSQLDB();		

		}
	}
	
	/**
	 * Browse the existing list of events to find any long event that take longer than 4ms
	 * @return void, stores them in the private attribute flaggedEventLists
	 */
	public void createFlaggedEvents() {
				
		//Browse the map and compute the duration between finished and started if possible:
		for (List<EventLogLine> eventList : linesMap.values()) {
			if (eventList.size() >= 2) {
				
				EventLogLine startEvent = null;
				EventLogLine finishEvent = null;		
				
				for (EventLogLine event : eventList) {
					if (event.getState() == EventState.STARTED) {
						startEvent = event;
					} else if (event.getState() == EventState.FINISHED) {
						finishEvent = event;
					}
				}
				
				//If there are both 'started' and 'finished' line, add a log Event to the list if applicable
				if (startEvent != null && finishEvent != null) {
					long duration = finishEvent.getTimestamp() - startEvent.getTimestamp();
					if (duration >= FLAG_EVENT_THRESHOLD_MS) {
						LogEvent e = new LogEvent(finishEvent.getId(), duration, finishEvent.getType(), finishEvent.getHost());
						flaggedEventList.add(e);
					}
				}		
			}
		}
	
	}
	
	
	
	public static void main(String[] args) {
		
		//Uses the following default input file if not provided in arguments:
		String inputFilePath=DEFAULT_INPUT_FILE;
		
		LogManager logManager = new LogManager(inputFilePath);

		// Gives the ability to set the file paths in a command line argument to be able to test different files
		// Command <inputFilePath> <outputFilePath>
		if (args.length > 2) {
			LOGGER.info("Too many arguments provided.");
		} else if (args.length == 2) {
			inputFilePath=args[0];
			if (args[1].equals("-drop")) {
				logManager.setDropExistingTable(true);
			}
		} else if (args.length == 1) {
			inputFilePath=args[0];
		}
		
		logManager.setInputPath(inputFilePath);


		//Business logic:
		logManager.processLogEvents();
		
	}

}
