import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDatabase {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://192.168.103.106:5432/postgres";
        String user = "postgres";
        String password = "123cp!@#";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to postgres database...");
            
            // Check if database exists
            try {
                stmt.execute("CREATE DATABASE \"aml-springboot\"");
                System.out.println("Database 'aml-springboot' created successfully.");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                     System.out.println("Database 'aml-springboot' already exists.");
                } else {
                    throw e;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
