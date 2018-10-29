package com.slr;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.slr.utils.*;
class LogManagerTest {
	
	private static final String INPUT_PATH_EMPTY_FILE = "src/test/resources/empty.txt";
	private static final String INPUT_PATH_SAMPLE_WORKING = "src/test/resources/sample-input.json";

	@Test
	void createFlaggedEvents_Empty() {
		LogManager logManager = new LogManager(INPUT_PATH_EMPTY_FILE);
		LogFileReader reader = new LogFileReader();
		logManager.setLinesMap(reader.readFromFile(INPUT_PATH_EMPTY_FILE));
		logManager.createFlaggedEvents();
		assertTrue(logManager.getFlaggedEventList().isEmpty());
	}
	
	@Test
	void createFlaggedEvents_Working() {
		LogManager logManager = new LogManager(INPUT_PATH_SAMPLE_WORKING);
		LogFileReader reader = new LogFileReader();
		logManager.setLinesMap(reader.readFromFile(INPUT_PATH_SAMPLE_WORKING));
		logManager.createFlaggedEvents();
		assertEquals(logManager.getFlaggedEventList().size(), 2);
		Assertions.assertEquals(logManager.getFlaggedEventList().get(0).getId(), "scsmbstgra");
		Assertions.assertEquals(logManager.getFlaggedEventList().get(0).getDuration(), 5L);
		Assertions.assertEquals(logManager.getFlaggedEventList().get(0).getType(), Optional.of("APPLICATION_LOG"));
		Assertions.assertEquals(logManager.getFlaggedEventList().get(0).getHost(), Optional.of("12345"));
	}

}
