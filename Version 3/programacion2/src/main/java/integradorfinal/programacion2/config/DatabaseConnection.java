package integradorfinal.programacion2.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(Config.DB_DRIVER);
                connection = DriverManager.getConnection(
                    Config.JDBC_URL,
                    Config.DB_USER,
                    Config.DB_PASS
                );
                // Solo se imprime cuando realmente se abre la conexión
                System.out.println("✅ Conectado correctamente a la base de datos.");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Driver JDBC no encontrado: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
