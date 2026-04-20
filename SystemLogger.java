import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SystemLogger {
    private List<String> logs;
    private static final int MAX_LOGS = 100;
    private DateTimeFormatter timeFormatter;
    
    public SystemLogger() {
        this.logs = new ArrayList<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
    
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
    
    public List<String> getAllLogs() {
        return new ArrayList<>(logs);
    }
    
    public String getLatestLog() {
        return logs.isEmpty() ? "" : logs.get(logs.size() - 1);
    }
    
    public void clear() {
        logs.clear();
    }
}