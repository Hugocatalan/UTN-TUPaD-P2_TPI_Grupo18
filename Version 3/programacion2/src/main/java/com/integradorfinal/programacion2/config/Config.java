package com.integradorfinal.programacion2.config;

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

    public static final String JDBC_URL = props.getProperty("jdbc.url");
    public static final String DB_USER  = props.getProperty("jdbc.user");
    public static final String DB_PASS  = props.getProperty("jdbc.pass");
    public static final String DB_DRIVER = props.getProperty("db.driver");

    private Config() {}

    public static void printConfig() {
        System.out.println("JDBC_URL = " + JDBC_URL);
        System.out.println("DB_USER  = " + DB_USER);
        System.out.println("DB_PASS  = " + DB_PASS);
        System.out.println("DB_DRIVER= " + DB_DRIVER);
    }
}
