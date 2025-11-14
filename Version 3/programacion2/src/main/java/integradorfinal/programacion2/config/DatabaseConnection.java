package integradorfinal.programacion2.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Esta clase la uso como un punto central para manejar la conexión JDBC a mi base de datos.
 * La idea es simple: tener un único lugar donde se abre, se reutiliza y se cierra la conexión.
 * De esta forma evito código repetido en todos los DAO.
 */
public class DatabaseConnection {

    // Mantengo una única conexión (patrón Singleton a nivel conexión).
    private static Connection connection = null;

    // Constructor privado: no quiero que esta clase se pueda instanciar.
    private DatabaseConnection() {}

    /**
     * Acá obtengo la conexión a la base usando los valores de la clase Config.
     *
     * Si ya existe una conexión abierta, la reutilizo para no crear conexiones innecesarias.
     * Si no existe (o está cerrada), creo una nueva usando DriverManager.
     *
     * @return una conexión activa, lista para ejecutar SQL
     * @throws SQLException si algo falla al conectar
     */
    public static Connection getConnection() throws SQLException {
        // Si nunca abrí la conexión, o si se cerró por algún motivo, la creo de nuevo.
        if (connection == null || connection.isClosed()) {
            try {
                // Cargo el driver JDBC antes de usarlo.
                Class.forName(Config.DB_DRIVER);

                // Creo la conexión usando la URL, usuario y contraseña definidos en Config.
                connection = DriverManager.getConnection(
                    Config.JDBC_URL,
                    Config.DB_USER,
                    Config.DB_PASS
                );

                System.out.println("✅ Conexión establecida correctamente con la base de datos.");

            } catch (ClassNotFoundException e) {
                // Error claro si el driver no está en el classpath.
                System.err.println("❌ No se encontró el driver JDBC. Verifique la configuración.");
                System.err.println("Detalles técnicos: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Cierro la conexión si existe y está abierta.
     *
     * Siempre envuelvo el cierre en un try/catch porque es común que falle si
     * la conexión ya está cerrada o si hubo un timeout. En esos casos muestro
     * un mensaje amable pero también registro el detalle técnico por consola.
     */
    public static void closeConnection() {
        try {
            // Solo cierro si existe y realmente está abierta.
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            // Mensaje amable para el usuario.
            System.err.println("⚠️ Hubo un problema al cerrar la conexión, pero no afecta sus datos.");

            // Detalle técnico para debugging.
            System.err.println("Detalles técnicos: " + e.getMessage());
        }
    }
}
