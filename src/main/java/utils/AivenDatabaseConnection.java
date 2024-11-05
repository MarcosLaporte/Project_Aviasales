package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class AivenDatabaseConnection {

    public static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("Aiven worked.");
            String query = "SELECT id, name FROM airports";
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);

                System.out.println("Airport list:");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    System.out.println("ID: " + id + ", Name: " + name);
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
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
