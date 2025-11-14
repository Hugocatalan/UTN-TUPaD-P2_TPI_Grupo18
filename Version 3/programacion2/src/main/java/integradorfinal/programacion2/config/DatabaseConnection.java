package integradorfinal.programacion2.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}
    
    /**
     * Obtiene una conexión a la base de datos usando la configuración definida en {@link Config}.
     * <p>
     * Si la conexión ya existe y está abierta, se reutiliza. En caso contrario, se crea una nueva.
     * </p>
     *
     * @return conexión activa a la base de datos
     * @throws SQLException si ocurre un error al establecer la conexión
     */

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(Config.DB_DRIVER);
                connection = DriverManager.getConnection(
                    Config.JDBC_URL,
                    Config.DB_USER,
                    Config.DB_PASS
                );
             System.out.println("✅ Conexión establecida correctamente con la base de datos.");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ No se encontró el driver JDBC. Verifique la configuración.");
                System.err.println("Detalles técnicos: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Cierra la conexión activa con la base de datos, si existe.
     * <p>
     * Muestra mensajes amigables al usuario y registra detalles técnicos en caso de error.
     * </p>
     */
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            // Mensaje amigable
            System.err.println("⚠️ Hubo un problema al cerrar la conexión, pero no afecta sus datos.");
            // Detalle técnico
            System.err.println("Detalles técnicos: " + e.getMessage());
        }
    }
}
