/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tpi_prog_2_Grupo18.Dao;

import tpi_prog_2_Grupo18.Models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//import tpi_prog_2_Grupo18.Config.DatabaseConnection;
//import tpi_prog_2_Grupo18.Models.Credencial;

/**
 *
 * @author Matias
 */
public class UsuarioDAO implements GenericDAO<Usuario> {

    private static final String INSERT_SQL =
        "INSERT INTO usuario (username, nombre, apellido, email, activo, estado) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE usuario SET username = ?, nombre = ?, apellido = ?, email = ?, activo = ?, estado = ? WHERE id_usuario = ?";

    private static final String DELETE_SQL =
        "UPDATE usuario SET eliminado = TRUE WHERE id_usuario = ?";

    private static final String SELECT_BY_ID_SQL =
        "SELECT id_usuario, username, nombre, apellido, email, fecha_registro, activo, estado " +
        "FROM usuario WHERE id_usuario = ? AND eliminado = FALSE";

    private static final String SELECT_ALL_SQL =
        "SELECT id_usuario, username, nombre, apellido, email, fecha_registro, activo, estado " +
        "FROM usuario WHERE eliminado = FALSE";

    private static final String SEARCH_BY_USERNAME_SQL =
        "SELECT id_usuario, username, nombre, apellido, email, fecha_registro, activo, estado " +
        "FROM usuario WHERE eliminado = FALSE AND username LIKE ?";

    private static final String SEARCH_BY_EMAIL_SQL =
        "SELECT id_usuario, username, nombre, apellido, email, fecha_registro, activo, estado " +
        "FROM usuario WHERE eliminado = FALSE AND email = ?";

    @Override
    public void insertar(Usuario usuario) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellido());
            stmt.setString(4, usuario.getEmail());
            stmt.setBoolean(5, usuario.isActivo());
            stmt.setString(6, usuario.getEstado());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellido());
            stmt.setString(4, usuario.getEmail());
            stmt.setBoolean(5, usuario.isActivo());
            stmt.setString(6, usuario.getEstado());
            stmt.setInt(7, usuario.getIdUsuario());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No se pudo actualizar usuario con ID: " + usuario.getIdUsuario());
            }
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No se encontró usuario con ID: " + id);
            }
        }
    }

    @Override
    public Usuario getById(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Usuario> getAll() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    public Usuario buscarPorEmail(String email) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_EMAIL_SQL)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> buscarPorUsername(String filtro) throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_USERNAME_SQL)) {

            stmt.setString(1, "%" + filtro + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        }
        return usuarios;
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setEmail(rs.getString("email"));
        u.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        u.setActivo(rs.getBoolean("activo"));
        u.setEstado(rs.getString("estado"));
        return u;
    }
}
