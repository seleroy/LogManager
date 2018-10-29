# LogManager

## Problem

Our custom-build server logs different events to a file. Every event has 2 entries in a log - one entry when the event was started and another when
the event was finished. The entries in a log file have no specific order (it can occur that a specific event is logged before the event starts)
Every line in the file is a JSON object containing event data:  
- id - the unique event identifier  
- state - whether the event was started or finished (can have values "STARTED" or "FINISHED"  
- timestamp - the timestamp of the event in milliseconds  
- Application Server logs also have the additional attributes:  
- type - type of log
- host - hostname

Example:  

```
{"id":"scsmbstgra", "state":"STARTED", "type":"APPLICATION_LOG",  
"host":"12345", "timestamp":1491377495212}  
{"id":"scsmbstgrb", "state":"STARTED", "timestamp":1491377495213}  
{"id":"scsmbstgrc", "state":"FINISHED", "timestamp":1491377495218}  
{"id":"scsmbstgra", "state":"FINISHED", "type":"APPLICATION_LOG",  
"host":"12345", "timestamp":1491377495217}  
{"id":"scsmbstgrc", "state":"STARTED", "timestamp":1491377495210}  
{"id":"scsmbstgrb", "state":"FINISHED", "timestamp":1491377495216}  
...
```

In the example above, the event scsmbstgrb duration is 1401377495216 - 1491377495213 = 3ms  
The longest event is scsmbstgrc (1491377495218 - 1491377495210 = 8ms)  
The program should:  
Take the input file path as input argument  
Flag any long events that take longer than 4ms with a column in the database called "alert"  
Write the found event details to file-based HSQLDB (http://hsqldb.org/) in the working folder  
The application should a new table if necessary and enter the following values:  
- Event id  
- Event duration  
- Type and Host if applicable  
- "alert" true is applicable

### Project Hierarchy

The following files and folder are part of the project:

- README.md : current file
- build.gradle : gradle build configuration
- src/main/java :  Source files of the code.
- src/main/resources :  Sample input json file
- src/test/java :  Source files for the JUNIT5 unit tests
- src/test/resources :  input json files used by the unit tests

### Prerequisites

You will need gradle version 4.9 or higher to be able to compile and run this application

### Installing

Build the application and run the JUnit5 unit tests, from the root folder:

```
gradle build
```

## Executing the application

The execution of the application requires an input file that can be passed as argument of the execution.
If no argument is passed, the default behaviour of the application is to read the input file from src/main/resources/sample-input.json.

If you want to drop the table before running a second execution to start with no data, you can pass the "-drop" argument as a second argument

```
gradle run --args "[<absolute_path_to_input_file>] [-drop]"
```

Examples of valid commands:

```
gradle run
gradle run --args "E:\dev\data\input.json"
gradle run --args "E:\dev\data\input.json -drop"
```

Note:
	The input file must exist

If the input path does not exist or in an invalid JSON format, an IllegalArgumentException will be raised.

## Expected output

While running, the application will show different logs showing the successful execution of the table in the database, the content of the table after the run, and successful stop of the db server. Below is an example of the expected output:

```
Oct 29, 2018 12:12:04 PM com.slr.utils.DBManager startHSQLDB
INFO: HSQL Server started
Oct 29, 2018 12:12:04 PM com.slr.LogManager processLogEvents
INFO: Content of db at startup:

Oct 29, 2018 12:12:04 PM com.slr.LogManager processLogEvents
INFO: Content of db before stopping:
ID:0,  LOGID:scsmbstgra,  DURATION:5,  HOST:12345,  TYPE:APPLICATION_LOG
ID:1,  LOGID:scsmbstgrc,  DURATION:8,  HOST:,  TYPE:

Oct 29, 2018 12:12:04 PM com.slr.utils.DBManager stopHSQLDB
INFO: Connection to HSQL Server closed
Oct 29, 2018 12:12:04 PM com.slr.utils.DBManager stopHSQLDB
INFO: HSQL Server stopped
```

The application creates an HSQLDB table and the following files are expected to be generated at the root folder:
- events.lck : lock file
- events.log 
- events.properties
- events.script : script running at startup to restore the table. You can see the content of the table by checking the "INSERT" rows in this script.

