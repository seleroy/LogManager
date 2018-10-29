package com.slr.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.slr.model.LogEvent;

public class DBManagerTest {
		
	@Test
	void insertAndReadEvents_OK() {
		
		String expectedOutput="ID:0,  LOGID:1,  DURATION:2,  HOST:4,  TYPE:3\n";
		
		DBManager dbmanager = new DBManager();
		dbmanager.startHSQLDB();
		dbmanager.dropHSQLDBLTable();
		dbmanager.createHSQLDBTable();
		LogEvent e = new LogEvent("1", 2L, Optional.of("3"), Optional.of("4"));
		List<LogEvent> list = new ArrayList<>();
		list.add(e);
		dbmanager.insertEvents(list);
		String result = dbmanager.readEvents();
		assertEquals(result, expectedOutput);
		dbmanager.stopHSQLDB();
	}
	
	@Test
	void insertAndReadEvents_insertNullList() {
		DBManager dbmanager = new DBManager();
		dbmanager.startHSQLDB();
		dbmanager.dropHSQLDBLTable();
		dbmanager.createHSQLDBTable();
		List<LogEvent> list = new ArrayList<>();
		dbmanager.insertEvents(list);
		String result = dbmanager.readEvents();
		assertEquals(result, "");
		dbmanager.stopHSQLDB();
	}

}
