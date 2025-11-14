package integradorfinal.programacion2.dao.impl;

import integradorfinal.programacion2.config.DatabaseConnection;
import integradorfinal.programacion2.dao.CredencialAccesoDao;
import integradorfinal.programacion2.entities.CredencialAcceso;
import integradorfinal.programacion2.entities.Estado;
import integradorfinal.programacion2.exceptions.DataAccessException;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del DAO de CredencialAcceso.
 */
public class CredencialAccesoDaoImpl implements CredencialAccesoDao {

    // ======================================================
    // CRUD BÁSICO (maneja su propia Connection)
    // ======================================================
  @Override
public Long create(CredencialAcceso c) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        return create(c, conn);
    } catch (SQLException e) {
        throw new DataAccessException("Error al crear credencial de usuario con ID=" + c.getUsuarioId(), e);
    }
}


    @Override
    public Optional<CredencialAcceso> findById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(id, conn);
        }
    }

    @Override
    public List<CredencialAcceso> findAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findAll(conn);
        }
    }

    @Override
    public void update(CredencialAcceso c) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            update(c, conn);
        }
    }

    @Override
    public void softDeleteById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            softDeleteById(id, conn);
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteById(id, conn);
        }
    }

    // ======================================================
    // CRUD con Connection externa (para transacciones)
    // ======================================================
    @Override
    public Long create(CredencialAcceso c, Connection conn) throws SQLException {
        final String sql = """
            INSERT INTO credencial_acceso
              (eliminado, usuario_id, estado, ultima_sesion, hash_password, salt, ultimo_cambio, requiere_reset)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, c.isEliminado());
            ps.setLong(2, c.getUsuarioId());
            ps.setString(3, c.getEstado().name());
            setNullableTimestamp(ps, 4, c.getUltimaSesion());
            ps.setString(5, c.getHashPassword());
            ps.setString(6, c.getSalt());
            setNullableTimestamp(ps, 7, c.getUltimoCambio());
            ps.setBoolean(8, c.isRequiereReset());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    c.setIdCredencial(id);
                    return id;
                }
            }
        }
        return null;
    }

    @Override
    public Optional<CredencialAcceso> findById(Long id, Connection conn) throws SQLException {
        final String sql = "SELECT * FROM credencial_acceso WHERE id_credencial = ? AND eliminado = FALSE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CredencialAcceso> findAll(Connection conn) throws SQLException {
        final String sql = "SELECT * FROM credencial_acceso WHERE eliminado = FALSE ORDER BY id_credencial";
        List<CredencialAcceso> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    @Override
    public void update(CredencialAcceso c, Connection conn) throws SQLException {
        final String sql = """
            UPDATE credencial_acceso
               SET eliminado=?, usuario_id=?, estado=?, ultima_sesion=?, hash_password=?, salt=?, ultimo_cambio=?, requiere_reset=?
             WHERE id_credencial=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, c.isEliminado());
            ps.setLong(2, c.getUsuarioId());
            ps.setString(3, c.getEstado().name());
            setNullableTimestamp(ps, 4, c.getUltimaSesion());
            ps.setString(5, c.getHashPassword());
            ps.setString(6, c.getSalt());
            setNullableTimestamp(ps, 7, c.getUltimoCambio());
            ps.setBoolean(8, c.isRequiereReset());
            ps.setLong(9, c.getIdCredencial());
            ps.executeUpdate();
        }
    }

    @Override
    public void softDeleteById(Long id, Connection conn) throws SQLException {
        final String sql = "UPDATE credencial_acceso SET eliminado = TRUE WHERE id_credencial = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(Long id, Connection conn) throws SQLException {
        final String sql = "DELETE FROM credencial_acceso WHERE id_credencial = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // ======================================================
    // MÉTODOS ESPECÍFICOS
    // ======================================================
    @Override
    public Optional<CredencialAcceso> findByUsuarioId(Long usuarioId) throws SQLException {
        final String sql = "SELECT * FROM credencial_acceso WHERE usuario_id = ? AND eliminado = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public void updatePasswordSeguro(Long usuarioId, String nuevoHash, String nuevoSalt) throws SQLException {
        // Llama al procedimiento almacenado: sp_actualizar_password_seguro
        try (Connection conn = DatabaseConnection.getConnection()) {
            updatePasswordSeguro(usuarioId, nuevoHash, nuevoSalt, conn);
        }
    }

    // Sobrecarga para uso transaccional
    public void updatePasswordSeguro(Long usuarioId, String nuevoHash, String nuevoSalt, Connection conn) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{ call sp_actualizar_password_seguro(?, ?, ?) }")) {
            cs.setLong(1, usuarioId);
            cs.setString(2, nuevoHash);
            cs.setString(3, nuevoSalt);
            cs.execute();
        }
    }

    // ======================================================
    // MÉTODOS AUXILIARES
    // ======================================================
    private CredencialAcceso mapRow(ResultSet rs) throws SQLException {
        CredencialAcceso c = new CredencialAcceso();
        c.setIdCredencial(rs.getLong("id_credencial"));
        c.setEliminado(rs.getBoolean("eliminado"));
        c.setUsuarioId(rs.getLong("usuario_id"));
        c.setEstado(Estado.from(rs.getString("estado")));

        Timestamp tsUlt = rs.getTimestamp("ultima_sesion");
        if (tsUlt != null) c.setUltimaSesion(tsUlt.toLocalDateTime());

        c.setHashPassword(rs.getString("hash_password"));
        c.setSalt(rs.getString("salt"));

        Timestamp tsCambio = rs.getTimestamp("ultimo_cambio");
        if (tsCambio != null) c.setUltimoCambio(tsCambio.toLocalDateTime());

        c.setRequiereReset(rs.getBoolean("requiere_reset"));
        return c;
    }

    private void setNullableTimestamp(PreparedStatement ps, int index, LocalDateTime ldt) throws SQLException {
        if (ldt == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(ldt));
        }
    }
}
