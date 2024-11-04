import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class AivenDatabaseConnection {

    private String url;
    private String user;
    private String password;

    public AivenDatabaseConnection() {
        loadProperties();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/java/config.properties")) {
            properties.load(input);
            url = properties.getProperty("database.url");
            user = properties.getProperty("database.user");
            password = properties.getProperty("database.password");
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para testear la conexión
    public void testConnection() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
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
}
