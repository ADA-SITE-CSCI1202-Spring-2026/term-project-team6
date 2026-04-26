import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SystemLogger {
    private List<String> logs;
    private static final int MAX_LOGS = 100;
    private DateTimeFormatter timeFormatter;
    
    // Initializes the logger with an empty log list and a time formatter.
    public SystemLogger() {
        this.logs = new ArrayList<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
    
    // Logs a new message with a timestamp.
    public void log(String message) {
        String timestamp = LocalTime.now().format(timeFormatter);
        String logEntry = "[" + timestamp + "] " + message;
        logs.add(logEntry);
        
        // Keep only recent logs
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }
        
        System.out.println(logEntry); // Also print to console for debugging
    }
    
    // Retrieves all logs as a list of strings.
    public List<String> getAllLogs() {
        return new ArrayList<>(logs);
    }
    
    // Retrieves the most recent log entry, or an empty string if no logs exist.
    public String getLatestLog() {
        return logs.isEmpty() ? "" : logs.get(logs.size() - 1);
    }
    
    // Clears all logs.
    public void clear() {
        logs.clear();
    }
}