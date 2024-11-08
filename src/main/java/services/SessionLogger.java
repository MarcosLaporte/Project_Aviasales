package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import entities.Airport;
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
        List<Map<String, Object>> sessions = loadExistingSessions();
        sessions.add(currentSession);
        writeSessionsToFile(sessions);
    }

    private List<Map<String, Object>> loadExistingSessions() {
        File file = new File(LOG_FILE_PATH);
        if (file.exists() && file.length() > 0) {
            try {
                return mapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
            } catch (IOException e) {
                System.err.println("Error reading log file: " + e.getMessage());
            }
        }
        // Return a new list if file is empty or cannot be read
        return new ArrayList<>();
    }

    private void writeSessionsToFile(List<Map<String, Object>> sessions) {
        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(new File(LOG_FILE_PATH), sessions);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}