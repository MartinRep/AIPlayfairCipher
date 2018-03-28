package ie.gmit.sw.ai;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logging service Singleton class, used to log (console and file) warnings, errors 
 * by whole application via ArrayBlockingQueue servLog.
 * Runs in it's own Thread.
 * 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class LogService {

    private static String logFile;
    private static ArrayBlockingQueue<String> servLog;
    private static LogService instance;
    private static boolean stop = false;

    private LogService(ArrayBlockingQueue<String> servLog, String logFile) {
	LogService.logFile = logFile;
	LogService.servLog = servLog;
	Thread dLogger = new Thread(() -> {
    try {
        logger();
    } catch (InterruptedException e) {
        System.out.println("[ERROR] Logging service error: " + e.getMessage());
    }
    });
	dLogger.start();
    }

    /**
     * Singleton Initialization method.
     * @param servLog ArrayBlockingQueue of Strings where messages to be logged are put.
     * @param logFile Absolute path and File name for log file.
     * @return instance of LogService
     */
    
    public static synchronized LogService init(ArrayBlockingQueue<String> servLog, String logFile) {
	if (instance == null) {
	    instance = new LogService(servLog, logFile);
	}
	return instance;
    }

    private static void logger() throws InterruptedException {
	// Creates Logger for log file management
	Logger logger = Logger.getLogger("ServerLog");
	logger.setUseParentHandlers(true);
	FileHandler fh;
	try {
	    // This block configure the logger with handler and formatter
	    fh = new FileHandler(logFile, true);
	    logger.addHandler(fh);
	    SimpleFormatter formatter = new SimpleFormatter();
	    fh.setFormatter(formatter);
	    // System.getProperty("line.separator") is used because same string is used for console and file
	    String log = new StringBuilder().append("Logging Service Started in: ").append(logFile)
		    .append(System.getProperty("line.separator")).append("==============================").toString();
	    // Allows to orderly finish thread. Waits for stop to be true to know when to stop
	    do {
		logger.info(log);
		log = servLog.take();
	    } while (!stop);
	    log = "[Warning] Logging Service Stopped.";
	    logger.info(log);
	} catch (SecurityException e) {
	    System.out.println("[ERROR] Logging service exception: " + e.getMessage());
	} catch (IOException e) {
	    System.out.println("[ERROR] Opening Log file! Please set the corrent path in web.xml\n " + e.getMessage());
	}
    }

    static void logMessage(String message){
        servLog.add(message);
    }
    /**
     * Terminate the logging Thread.
     */
    static void shutdown() {
	stop = true;
    }

    @Override
    protected void finalize() throws Throwable {
	super.finalize();
	shutdown();
    }
}
