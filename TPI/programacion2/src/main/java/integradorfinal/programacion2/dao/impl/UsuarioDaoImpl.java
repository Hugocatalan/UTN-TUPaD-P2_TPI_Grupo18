package integradorfinal.programacion2.dao.impl;

import integradorfinal.programacion2.config.DatabaseConnection;
import integradorfinal.programacion2.dao.UsuarioDao;
import integradorfinal.programacion2.entities.Estado;
import integradorfinal.programacion2.entities.Usuario;
import integradorfinal.programacion2.exceptions.DataAccessException;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del DAO de Usuario.
 */
public class UsuarioDaoImpl implements UsuarioDao {

    // ================================
    // Métodos CRUD básicos
    // ================================

    @Override
    public Long create(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(usuario, conn);
        }
    }




// 2) Método transaccional (con Connection): PROPAGA SQLException
@Override
public Long create(Usuario usuario, Connection conn) throws SQLException {
    String sql = """
        INSERT INTO usuario (eliminado, username, nombre, apellido, email,
                             fecha_registro, activo, estado)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setBoolean(1, usuario.isEliminado());
        ps.setString(2, usuario.getUsername());
        ps.setString(3, usuario.getNombre());
        ps.setString(4, usuario.getApellido());
        ps.setString(5, usuario.getEmail());
        ps.setTimestamp(6, Timestamp.valueOf(
                usuario.getFechaRegistro() != null ? usuario.getFechaRegistro() : LocalDateTime.now()
        ));
        ps.setBoolean(7, usuario.isActivo());
        ps.setString(8, usuario.getEstado().dbValue());
        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                long id = rs.getLong(1);
                usuario.setIdUsuario(id);
                return id;
            } else {
                throw new SQLException("No se obtuvo la clave generada para usuario");
            }
        }
    }
}




    @Override
public Optional<Usuario> findById(Long id) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        return findById(id, conn);
    } catch (SQLException e) {
        throw new DataAccessException("Error al buscar usuario por id=" + id, e);
    }
}


    @Override
    public Optional<Usuario> findById(Long id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ? AND eliminado = FALSE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

   @Override
public List<Usuario> findAll() {
    try (Connection conn = DatabaseConnection.getConnection()) {
        return findAll(conn);
    } catch (SQLException e) {
        throw new DataAccessException("Error al listar usuarios", e);
    }
}


    @Override
    public List<Usuario> findAll(Connection conn) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE eliminado = FALSE ORDER BY id_usuario";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    @Override
    public void update(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            update(usuario, conn);
        }
    }

    @Override
    public void update(Usuario usuario, Connection conn) throws SQLException {
        String sql = """
            UPDATE usuario
            SET username=?, nombre=?, apellido=?, email=?, activo=?, estado=?, eliminado=?
            WHERE id_usuario=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getEmail());
            ps.setBoolean(5, usuario.isActivo());
            ps.setString(6, usuario.getEstado().dbValue());
            ps.setBoolean(7, usuario.isEliminado());
            ps.setLong(8, usuario.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public void softDeleteById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            softDeleteById(id, conn);
        }
    }

    @Override
    public void softDeleteById(Long id, Connection conn) throws SQLException {
        String sql = "UPDATE usuario SET eliminado = TRUE WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

  @Override
public void deleteById(Long id) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        deleteById(id, conn);
    } catch (SQLException e) {
        throw new DataAccessException("Error al eliminar usuario con id=" + id, e);
    }
}

@Override
public void deleteById(Long id, Connection conn) throws SQLException {
    String sql = "DELETE FROM usuario WHERE id_usuario = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, id);
        ps.executeUpdate();
    }
}


    // ================================
    // Métodos específicos
    // ================================

    @Override
    public Optional<Usuario> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE username = ? AND eliminado = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ? AND eliminado = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    // ================================
    // Helper: mapea un ResultSet a Usuario
    // ================================

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("id_usuario"));
        u.setEliminado(rs.getBoolean("eliminado"));
        u.setUsername(rs.getString("username"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setEmail(rs.getString("email"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) u.setFechaRegistro(ts.toLocalDateTime());
        u.setActivo(rs.getBoolean("activo"));
        u.setEstado(Estado.from(rs.getString("estado")));
        return u;
    }
}
