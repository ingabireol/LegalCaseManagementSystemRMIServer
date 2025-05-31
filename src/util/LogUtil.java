package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging system activities, errors, and user actions
 */
public class LogUtil {
    
    // Log levels
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_WARNING = "WARNING";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    public static final String LOG_LEVEL_DEBUG = "DEBUG";
    public static final String LOG_LEVEL_AUDIT = "AUDIT";
    
    // Log file paths
    private static final String LOG_DIRECTORY = "logs/";
    private static final String SYSTEM_LOG_FILE = LOG_DIRECTORY + "system.log";
    private static final String ERROR_LOG_FILE = LOG_DIRECTORY + "error.log";
    private static final String AUDIT_LOG_FILE = LOG_DIRECTORY + "audit.log";
    private static final String USER_ACTIVITY_LOG_FILE = LOG_DIRECTORY + "user_activity.log";
    
    // Date formatter for log entries
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    static {
        // Create logs directory if it doesn't exist
        createLogDirectory();
    }
    
    /**
     * Creates the logs directory if it doesn't exist
     */
    private static void createLogDirectory() {
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }
    
    /**
     * Logs a general system message
     * 
     * @param level Log level (INFO, WARNING, ERROR, DEBUG)
     * @param message The message to log
     */
    public static void logSystem(String level, String message) {
        writeToFile(SYSTEM_LOG_FILE, formatLogEntry(level, "SYSTEM", message));
        
        // Also log errors to error file
        if (LOG_LEVEL_ERROR.equals(level)) {
            writeToFile(ERROR_LOG_FILE, formatLogEntry(level, "SYSTEM", message));
        }
        
        // Print to console for immediate visibility
        System.out.println(formatLogEntry(level, "SYSTEM", message));
    }
    
    /**
     * Logs an error with exception details
     * 
     * @param message Error message
     * @param exception The exception that occurred
     */
    public static void logError(String message, Exception exception) {
        String errorMsg = message + " - " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
        writeToFile(ERROR_LOG_FILE, formatLogEntry(LOG_LEVEL_ERROR, "ERROR", errorMsg));
        writeToFile(SYSTEM_LOG_FILE, formatLogEntry(LOG_LEVEL_ERROR, "ERROR", errorMsg));
        
        // Print to console
        System.err.println(formatLogEntry(LOG_LEVEL_ERROR, "ERROR", errorMsg));
        exception.printStackTrace();
    }
    
    /**
     * Logs user activities for audit purposes
     * 
     * @param username Username performing the action
     * @param action Action performed
     * @param details Additional details about the action
     */
    public static void logUserActivity(String username, String action, String details) {
        String message = "User: " + username + " | Action: " + action + " | Details: " + details;
        writeToFile(USER_ACTIVITY_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "USER_ACTIVITY", message));
        writeToFile(AUDIT_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "USER_ACTIVITY", message));
    }
    
    /**
     * Logs authentication events
     * 
     * @param username Username attempting authentication
     * @param success Whether authentication was successful
     * @param ipAddress IP address of the user (if available)
     */
    public static void logAuthentication(String username, boolean success, String ipAddress) {
        String status = success ? "SUCCESS" : "FAILED";
        String message = "Authentication " + status + " for user: " + username + " from IP: " + ipAddress;
        writeToFile(AUDIT_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "AUTH", message));
        writeToFile(USER_ACTIVITY_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "AUTH", message));
    }
    
    /**
     * Logs database operations
     * 
     * @param operation Operation type (CREATE, UPDATE, DELETE, SELECT)
     * @param entity Entity name (Client, Case, etc.)
     * @param entityId ID of the entity
     * @param username User performing the operation
     */
    public static void logDatabaseOperation(String operation, String entity, String entityId, String username) {
        String message = operation + " operation on " + entity + " (ID: " + entityId + ") by user: " + username;
        writeToFile(AUDIT_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "DATABASE", message));
    }
    
    /**
     * Logs RMI service calls
     * 
     * @param serviceName Name of the service called
     * @param methodName Method name called
     * @param username User making the call
     * @param success Whether the call was successful
     */
    public static void logServiceCall(String serviceName, String methodName, String username, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        String message = "RMI call " + status + " - Service: " + serviceName + "." + methodName + " by user: " + username;
        writeToFile(SYSTEM_LOG_FILE, formatLogEntry(LOG_LEVEL_INFO, "RMI", message));
    }
    
    /**
     * Logs server startup and shutdown events
     * 
     * @param event Event type (STARTUP, SHUTDOWN)
     * @param port Server port
     */
    public static void logServerEvent(String event, int port) {
        String message = "Server " + event + " on port: " + port;
        writeToFile(SYSTEM_LOG_FILE, formatLogEntry(LOG_LEVEL_INFO, "SERVER", message));
        System.out.println(formatLogEntry(LOG_LEVEL_INFO, "SERVER", message));
    }
    
    /**
     * Logs financial transactions (payments, invoices)
     * 
     * @param transactionType Type of transaction (PAYMENT, INVOICE, etc.)
     * @param amount Transaction amount
     * @param clientName Client involved
     * @param username User processing the transaction
     */
    public static void logFinancialTransaction(String transactionType, String amount, String clientName, String username) {
        String message = transactionType + " of " + amount + " for client: " + clientName + " processed by: " + username;
        writeToFile(AUDIT_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "FINANCIAL", message));
    }
    
    /**
     * Logs case status changes
     * 
     * @param caseNumber Case number
     * @param oldStatus Previous status
     * @param newStatus New status
     * @param username User making the change
     */
    public static void logCaseStatusChange(String caseNumber, String oldStatus, String newStatus, String username) {
        String message = "Case " + caseNumber + " status changed from '" + oldStatus + "' to '" + newStatus + "' by: " + username;
        writeToFile(AUDIT_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "CASE_STATUS", message));
    }
    
    /**
     * Logs document operations
     * 
     * @param operation Operation type (UPLOAD, DOWNLOAD, DELETE, VIEW)
     * @param documentName Document name
     * @param caseNumber Associated case number
     * @param username User performing the operation
     */
    public static void logDocumentOperation(String operation, String documentName, String caseNumber, String username) {
        String message = "Document " + operation + " - '" + documentName + "' for case: " + caseNumber + " by: " + username;
        writeToFile(AUDIT_LOG_FILE, formatLogEntry(LOG_LEVEL_AUDIT, "DOCUMENT", message));
    }
    
    /**
     * Logs performance metrics
     * 
     * @param operation Operation name
     * @param executionTime Execution time in milliseconds
     */
    public static void logPerformance(String operation, long executionTime) {
        String message = "Performance - " + operation + " completed in " + executionTime + "ms";
        writeToFile(SYSTEM_LOG_FILE, formatLogEntry(LOG_LEVEL_DEBUG, "PERFORMANCE", message));
    }
    
    /**
     * Formats a log entry with timestamp, level, category, and message
     * 
     * @param level Log level
     * @param category Log category
     * @param message Log message
     * @return Formatted log entry
     */
    private static String formatLogEntry(String level, String category, String message) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("[%s] [%s] [%s] %s", 
                           now.format(DATE_FORMATTER), 
                           level, 
                           category, 
                           message);
    }
    
    /**
     * Writes a log entry to the specified file
     * 
     * @param filename Log file name
     * @param logEntry Formatted log entry
     */
    private static void writeToFile(String filename, String logEntry) {
        try (FileWriter fileWriter = new FileWriter(filename, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            
            printWriter.println(logEntry);
            
        } catch (IOException e) {
            // If we can't write to log file, at least print to console
            System.err.println("Failed to write to log file " + filename + ": " + e.getMessage());
            System.err.println("Log entry was: " + logEntry);
        }
    }
    
    /**
     * Clears old log files (utility method for log rotation)
     * 
     * @param daysToKeep Number of days of logs to keep
     */
    public static void rotateLogFiles(int daysToKeep) {
        // Implementation for log rotation
        // This could be enhanced to move old logs to archive folder
        logSystem(LOG_LEVEL_INFO, "Log rotation requested - keeping " + daysToKeep + " days of logs");
    }
    
    /**
     * Gets the current log directory path
     * 
     * @return Log directory path
     */
    public static String getLogDirectory() {
        return LOG_DIRECTORY;
    }
    
    /**
     * Test method to verify logging functionality
     */
    public static void testLogging() {
        logSystem(LOG_LEVEL_INFO, "LogUtil test - System logging working");
        logUserActivity("testUser", "TEST_ACTION", "Testing user activity logging");
        logAuthentication("testUser", true, "127.0.0.1");
        logDatabaseOperation("CREATE", "TestEntity", "123", "testUser");
        logServiceCall("TestService", "testMethod", "testUser", true);
        logServerEvent("TEST", 5555);
        logFinancialTransaction("TEST_PAYMENT", "$100.00", "Test Client", "testUser");
        logCaseStatusChange("CASE001", "Open", "In Progress", "testUser");
        logDocumentOperation("UPLOAD", "test-document.pdf", "CASE001", "testUser");
        logPerformance("testOperation", 150);
        
        System.out.println("LogUtil test completed. Check log files in: " + LOG_DIRECTORY);
    }
}