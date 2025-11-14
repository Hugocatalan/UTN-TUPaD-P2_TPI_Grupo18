package integradorfinal.programacion2.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Esta clase Config la armé para centralizar toda la configuración de conexión
 * a la base de datos. La idea es que el resto del proyecto no tenga que saber
 * nada sobre rutas, drivers, usuarios, ni contraseñas: todo sale de acá.
 *
 * Además, cargo todo automáticamente apenas se carga la clase, usando un bloque
 * estático. Eso me evita tener que llamar a ningún método extra.
 */
public final class Config {

    // Acá guardo todas las propiedades que leo desde db.properties
    private static final Properties props = new Properties();

    /**
     * Bloque estático: se ejecuta una sola vez cuando la clase se carga en memoria.
     * En este bloque leo el archivo db.properties que está en /resources.
     * Si por alguna razón no lo encuentro o hay un error de lectura,
     * tiro una RuntimeException porque no puedo continuar sin esto.
     */
    static {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            // Si el archivo no aparece, fuerzo un error claro.
            if (input == null) {
                throw new RuntimeException("❌ No se encontró db.properties en resources");
            }

            // Si el archivo existe, cargo todas sus propiedades.
            props.load(input);

        } catch (IOException e) {
            // Si falla la carga por cualquier motivo, quiero que explote acá
            // y no durante la conexión a la base.
            throw new RuntimeException("❌ Error cargando configuración", e);
        }
    }

    // -------------------- VALORES CONFIGURADOS --------------------
    /**
     * Estos campos se llenan usando las propiedades del archivo.
     * Si alguna propiedad no está en db.properties, uso el valor por defecto
     * que le pasé como segundo parámetro.
     */
    public static final String DB_HOST   = props.getProperty("db.host", "127.0.0.1");
    public static final String DB_PORT   = props.getProperty("db.port", "3306");
    public static final String DB_NAME   = props.getProperty("db.name", "tpi_prog_2");
    public static final String DB_USER   = props.getProperty("jdbc.user", "root");
    public static final String DB_PASS   = props.getProperty("jdbc.pass", "1234");
    public static final String DB_DRIVER = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

    /**
     * Acá armo la URL completa de conexión usando los valores configurados.
     * También agrego los parámetros que necesito para UTF-8 y compatibilidad
     * con MySQL moderno (SSL, timezone, PK retrieval, etc.).
     */
    public static final String JDBC_URL = 
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?useUnicode=true&characterEncoding=utf8&useSSL=false"
        + "&allowPublicKeyRetrieval=true&serverTimezone=America/Argentina/Buenos_Aires";

    // Constructor privado: no quiero que nadie instancie esta clase.
    private Config() {}

    /**
     * Método útil para debug o para mostrar en consola qué configuración
     * estoy usando realmente en un momento dado.
     */
    public static void printConfig() {
        System.out.println("JDBC_URL = " + JDBC_URL);
        System.out.println("DB_USER  = " + DB_USER);
        System.out.println("DB_PASS  = " + DB_PASS);
        System.out.println("DB_DRIVER= " + DB_DRIVER);
    }
}
