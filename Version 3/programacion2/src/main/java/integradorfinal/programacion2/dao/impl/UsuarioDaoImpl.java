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
 *
 * En esta clase concentro todo el acceso a datos de la entidad Usuario:
 * crear, leer, actualizar, eliminar (tanto lógico como físico) y
 * algunas búsquedas específicas por username y por email.
 *
 * También separo claramente:
 * - Métodos que se manejan solos (crean/cierra su propia Connection).
 * - Métodos que reciben una Connection externa para usarse dentro de transacciones.
 */
public class UsuarioDaoImpl implements UsuarioDao {

    // ================================
    // Métodos CRUD básicos
    // ================================

    /**
     * Creo un usuario usando una Connection propia.
     * Internamente delego en la versión que recibe Connection para no repetir lógica.
     */
    @Override
    public Long create(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(usuario, conn);
        }
    }

    // 2) Método transaccional (con Connection): PROPAGA SQLException
    /**
     * Creo un usuario recibiendo la Connection desde afuera.
     * Este método está pensado para usarlo en una transacción junto con otros DAOs.
     */
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
            // Si por alguna razón no viene fecha de registro, uso la fecha/hora actual.
            ps.setTimestamp(6, Timestamp.valueOf(
                    usuario.getFechaRegistro() != null ? usuario.getFechaRegistro() : LocalDateTime.now()
            ));
            ps.setBoolean(7, usuario.isActivo());
            // Acá uso el valor que corresponde a cómo lo guardo en la BD (dbValue).
            ps.setString(8, usuario.getEstado().dbValue());
            ps.executeUpdate();

            // Recupero la clave primaria autogenerada y la guardo en el objeto.
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

    /**
     * Busco un usuario por ID usando una Connection propia.
     * Si algo falla, lo envuelvo en un DataAccessException para tener un error más de "capa DAO".
     */
    @Override
    public Optional<Usuario> findById(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(id, conn);
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar usuario por id=" + id, e);
        }
    }

    /**
     * Versión que recibe Connection para buscar por ID.
     * Solo traigo usuarios que no estén eliminados lógicamente.
     */
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

    /**
     * Devuelvo la lista completa de usuarios (no eliminados) usando Connection propia.
     */
    @Override
    public List<Usuario> findAll() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findAll(conn);
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar usuarios", e);
        }
    }

    /**
     * Versión que recibe Connection para listar usuarios.
     */
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

    /**
     * Actualizo un usuario completo usando Connection propia.
     * Delego en la versión con Connection para reutilizar la lógica.
     */
    @Override
    public void update(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            update(usuario, conn);
        }
    }

    /**
     * Versión que recibe Connection para actualizar todos los datos del usuario.
     */
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

    /**
     * Baja lógica de un usuario usando Connection propia.
     * Básicamente, marco el flag eliminado en TRUE.
     */
    @Override
    public void softDeleteById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            softDeleteById(id, conn);
        }
    }

    /**
     * Versión que recibe Connection para baja lógica.
     */
    @Override
    public void softDeleteById(Long id, Connection conn) throws SQLException {
        String sql = "UPDATE usuario SET eliminado = TRUE WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Baja física de un usuario (DELETE) usando Connection propia.
     * Si algo sale mal, lo envuelvo en DataAccessException.
     */
    @Override
    public void deleteById(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteById(id, conn);
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar usuario con id=" + id, e);
        }
    }

    /**
     * Versión que recibe Connection para realizar el DELETE definitivo del usuario.
     */
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

    /**
     * Busco un usuario por su username (campo único).
     * Uso una Connection propia y solo devuelvo usuarios no eliminados.
     */
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

    /**
     * Busco un usuario por email (campo único).
     * También filtro por eliminado = FALSE.
     */
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

    /**
     * Convierto una fila del ResultSet en un objeto Usuario.
     * Acá hago el mapeo campo a campo de las columnas de la tabla a mi entidad.
     */
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
