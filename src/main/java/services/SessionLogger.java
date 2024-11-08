package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.logging.log4j.Level;
import utils.LoggerService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionLogger {

    private static final String LOG_FILE_PATH = "session_log.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper mapper;
    private final Map<String, Object> currentSession;

    public SessionLogger() {
        this.mapper = new ObjectMapper();
        this.currentSession = new HashMap<>();
        logProgramStart();
    }

    private void logProgramStart() {
        currentSession.put("start_time", LocalDateTime.now().format(DATE_FORMAT));
    }

    public void logRouteDetails(String shortestRoute, double shortestDistance, String cheapestRoute, double cheapestPrice) {
        Map<String, Object> routeDetails = new HashMap<>();
        routeDetails.put("shortest_route", shortestRoute);
        routeDetails.put("shortest_distance_km", shortestDistance);
        routeDetails.put("cheapest_route", cheapestRoute);
        routeDetails.put("cheapest_price", cheapestPrice);

        currentSession.put("route_details", routeDetails);
        appendSessionToLogFile();
    }

    private void appendSessionToLogFile() {
        List<Map<String, Object>> sessions = new ArrayList<>();
        File file = new File(LOG_FILE_PATH);


        if (file.exists()) {
            try {
                sessions = mapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
            } catch (IOException e) {
                LoggerService.consoleLog(Level.ERROR, "Error reading log file: " + e.getMessage());
                sessions = new ArrayList<>();
            }
        }

        sessions.add(currentSession);

        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(file, sessions);
        } catch (IOException e) {
            LoggerService.consoleLog(Level.ERROR, "Error writing to log file: " + e.getMessage());
        }
    }
}
