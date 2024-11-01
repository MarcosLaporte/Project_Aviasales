import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AivenDatabaseConnection {
    private static final String URL = "jdbc:mysql://aviasales-aviasales-00bd.k.aivencloud.com:10458/aviasales?sslmode=require";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = ""; //put the password here :)

    // hice este metodo para testear
    public void testConnection() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
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
