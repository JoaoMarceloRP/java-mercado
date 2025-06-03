import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conec {
    public static Connection conectar() {
        try {
            String url = "jdbc:sqlite:mercado.db";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco: " + e.getMessage());
            return null;
        }
    }
}