package utils;

import entities.Route;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class AivenDatabaseConnection {

    public static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("Aiven worked.");
            String query = "SELECT * FROM routes";
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);

                System.out.println("Airport list:");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int id_from = resultSet.getInt("id_from");
                    int id_to = resultSet.getInt("id_to");
                    int airline_id = resultSet.getInt("airline_id");
                    int km = resultSet.getInt("km");
                    float price = resultSet.getFloat("price");
                    LoggerService.println(new Route(id, id_from, id_to, airline_id, km, price).toString());
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            LoggerService.println(e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try(InputStream input = AivenDatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")){
            if (input == null)
                throw new IOException("Unable to find config.properties");
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return DriverManager.getConnection(
                props.getProperty("URL"),
                props.getProperty("USER"),
                props.getProperty("PASSWORD")
        );
    }
}
