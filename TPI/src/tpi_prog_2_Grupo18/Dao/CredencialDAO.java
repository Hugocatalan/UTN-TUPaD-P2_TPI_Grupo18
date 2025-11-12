/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tpi_prog_2_Grupo18.Dao;

/**
 *
 * @author Matias
 */


import tpi_prog_2_Grupo18.Models.Credencial;
import tpi_prog_2_Grupo18.Models.EstadoCredencial;
import tpi_prog_2_Grupo18.Models.Usuario;
import tpi_prog_2_Grupo18.Util.PasswordUtil;
import tpi_prog_2_Grupo18.Config.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;

public class CredencialDAO {

    private static final String INSERT_SQL =
        "INSERT INTO credencial_acceso (usuario_id, estado, hash_password, salt, ultimo_cambio, requiere_reset) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE credencial_acceso SET estado = ?, hash_password = ?, salt = ?, ultimo_cambio = ?, requiere_reset = ? " +
        "WHERE usuario_id = ? AND eliminado = FALSE";

    private static final String DELETE_SQL =
        "UPDATE credencial_acceso SET eliminado = TRUE WHERE usuario_id = ?";

    private static final String SELECT_BY_USER_SQL =
        "SELECT * FROM credencial_acceso WHERE usuario_id = ? AND eliminado = FALSE";

    private static final String UPDATE_SESION_SQL =
        "UPDATE credencial_acceso SET ultima_sesion = CURRENT_TIMESTAMP WHERE usuario_id = ?";

    /**
     * Inserta una nueva credencial para un usuario con hash + salt.
     */
    public void insertarCredencial(Usuario usuario, String password, EstadoCredencial estado) throws Exception {
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setInt(1, usuario.getId());
            stmt.setString(2, estado.name());
            stmt.setString(3, hash);
            stmt.setString(4, salt);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(6, false); // requiere_reset por defecto

            stmt.executeUpdate();
        }
    }

    /**
     * Actualiza la credencial (ej: cambio de contraseña).
     */
    public void actualizarCredencial(Usuario usuario, String newPassword, boolean requiereReset) throws Exception {
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashPassword(newPassword, salt);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, EstadoCredencial.ACTIVO.name()); // o el estado que corresponda
            stmt.setString(2, hash);
            stmt.setString(3, salt);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(5, requiereReset);
            stmt.setInt(6, usuario.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No se encontró credencial para usuario ID: " + usuario.getId());
            }
        }
    }

    /**
     * Valida si la contraseña ingresada coincide con la credencial guardada.
     */
    public boolean validarCredencial(Usuario usuario, String password) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_SQL)) {

            stmt.setInt(1, usuario.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("hash_password");
                    String salt = rs.getString("salt");

                    String hashIngresado = PasswordUtil.hashPassword(password, salt);

                    if (hashGuardado.equals(hashIngresado)) {
                        actualizarUltimaSesion(usuario.getId());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Marca la última sesión del usuario.
     */
    private void actualizarUltimaSesion(int usuarioId) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SESION_SQL)) {
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina lógicamente la credencial.
     */
    public void eliminarCredencial(int usuarioId) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene la credencial completa de un usuario.
     */
    public Credencial getCredencialByUsuarioId(Usuario usuario) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_SQL)) {

            stmt.setInt(1, usuario.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Credencial c = new Credencial();
                    c.setId(rs.getInt("id_credencial"));
                    c.setEstado(EstadoCredencial.valueOf(rs.getString("estado")));
                    c.setUltimaSesion(rs.getTimestamp("ultima_sesion") != null ?
                            rs.getTimestamp("ultima_sesion").toLocalDateTime() : null);
                    c.setHashPassword(rs.getString("hash_password"));
                    c.setSalt(rs.getString("salt"));
                    c.setUltimoCambio(rs.getTimestamp("ultimo_cambio") != null ?
                            rs.getTimestamp("ultimo_cambio").toLocalDateTime() : null);
                    c.setRequiereReset(rs.getBoolean("requiere_reset"));
                    c.setUsuario(usuario);
                    c.setEliminado(rs.getBoolean("eliminado"));
                    return c;
                }
            }
        }
        return null;
    }
}