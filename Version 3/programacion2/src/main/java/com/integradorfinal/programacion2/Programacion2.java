package com.integradorfinal.programacion2;

import com.integradorfinal.programacion2.config.DatabaseConnection;
import java.sql.Connection;

/**
 
 * Prueba la conexión con la base de datos tpi_prog_2.
 */
public class Programacion2 {

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando prueba de conexión...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("🎯 Conexión exitosa al esquema tpi_prog_2!");
            } else {
                System.err.println("⚠️ No se pudo establecer la conexión.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al conectar con la base de datos:");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
