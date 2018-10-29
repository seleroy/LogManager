package com.slr.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.slr.model.EventLogLine;
import com.slr.utils.LogFileReader;

class LogFileReaderTest {
	
	private static final String INPUT_PATH_INVALID = "invalid_path";
	private static final String INPUT_PATH_EMPTY_FILE = "src/test/resources/empty.txt";
	private static final String INPUT_PATH_SAMPLE_WORKING = "src/test/resources/sample-input.json";
	private static final String INPUT_PATH_MALFORMED = "src/test/resources/sample-input-malformed.json";

	@Test
	void readFromFile_noFile() {
		LogFileReader reader = new LogFileReader();
		assertThrows(IllegalArgumentException.class, () -> {reader.readFromFile(INPUT_PATH_INVALID);});		
	}
	
	@Test
	void readFromFile_emptyFile() {
		LogFileReader reader = new LogFileReader();
		Map<String, List<EventLogLine>> resultMap = reader.readFromFile(INPUT_PATH_EMPTY_FILE);
		Assertions.assertTrue(resultMap.isEmpty());
	}
	
	@Test
	void readFromFile_sampleWorking() {
		LogFileReader reader = new LogFileReader();
		Map<String, List<EventLogLine>> resultMap = reader.readFromFile(INPUT_PATH_SAMPLE_WORKING);
		Assertions.assertEquals(resultMap.size(), 3);
		Assertions.assertEquals(resultMap.get("scsmbstgra").size(), 2);
		Assertions.assertEquals(resultMap.get("scsmbstgra").get(0).getTimestamp(), 1491377495212L);
		Assertions.assertEquals(resultMap.get("scsmbstgra").get(0).getType(), Optional.of("APPLICATION_LOG"));
		Assertions.assertEquals(resultMap.get("scsmbstgra").get(0).getHost(), Optional.of("12345"));
	}
	
	@Test
	void readFromFile_sampleMalformed() {
		LogFileReader reader = new LogFileReader();
		assertThrows(IllegalArgumentException.class, () -> {reader.readFromFile(INPUT_PATH_MALFORMED);});		
	}

}
