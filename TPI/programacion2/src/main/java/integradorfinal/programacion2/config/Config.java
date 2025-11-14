package integradorfinal.programacion2.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("❌ No se encontró db.properties en resources");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("❌ Error cargando configuración", e);
        }
    }

    // Valores por defecto
    public static final String DB_HOST   = props.getProperty("db.host", "127.0.0.1");
    public static final String DB_PORT   = props.getProperty("db.port", "3306");
    public static final String DB_NAME   = props.getProperty("db.name", "tpi_prog_2");
    public static final String DB_USER   = props.getProperty("jdbc.user", "root");
    public static final String DB_PASS   = props.getProperty("jdbc.pass", "1234");
    public static final String DB_DRIVER = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

    // Construcción de la URL
    public static final String JDBC_URL = //usa los valores ingresador por el usuario
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?useUnicode=true&characterEncoding=utf8&useSSL=false"
        + "&allowPublicKeyRetrieval=true&serverTimezone=America/Argentina/Buenos_Aires";

    private Config() {}

    public static void printConfig() {
        System.out.println("JDBC_URL = " + JDBC_URL);
        System.out.println("DB_USER  = " + DB_USER);
        System.out.println("DB_PASS  = " + DB_PASS);
        System.out.println("DB_DRIVER= " + DB_DRIVER);
    }
}
