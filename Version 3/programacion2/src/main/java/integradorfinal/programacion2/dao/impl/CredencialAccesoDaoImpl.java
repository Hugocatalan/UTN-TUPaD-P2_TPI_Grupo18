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
 * 
 * En esta clase concentro toda la lógica de acceso a datos para la tabla
 * credencial_acceso: creación, lectura, actualización y borrado.
 * 
 * La estructura la separé en dos partes:
 * - Métodos CRUD que se manejan solos (piden su propia Connection).
 * - Métodos CRUD que reciben una Connection externa (para trabajar en transacciones).
 */
public class CredencialAccesoDaoImpl implements CredencialAccesoDao {

    // ======================================================
    // CRUD BÁSICO (maneja su propia Connection)
    // ======================================================

    /**
     * Creo una credencial nueva usando una Connection propia.
     * Si algo falla a nivel SQL, lo envuelvo en un DataAccessException.
     */
    @Override
    public Long create(CredencialAcceso c) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(c, conn);
        } catch (SQLException e) {
            throw new DataAccessException("Error al crear credencial de usuario con ID=" + c.getUsuarioId(), e);
        }
    }

    /**
     * Busco una credencial por su ID usando una Connection propia.
     */
    @Override
    public Optional<CredencialAcceso> findById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(id, conn);
        }
    }

    /**
     * Devuelvo todas las credenciales no eliminadas lógicamente,
     * usando su propia Connection.
     */
    @Override
    public List<CredencialAcceso> findAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findAll(conn);
        }
    }

    /**
     * Actualizo una credencial completa usando una Connection propia.
     */
    @Override
    public void update(CredencialAcceso c) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            update(c, conn);
        }
    }

    /**
     * Baja lógica de una credencial (marco eliminado=TRUE)
     * usando una Connection propia.
     */
    @Override
    public void softDeleteById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            softDeleteById(id, conn);
        }
    }

    /**
     * Baja física de una credencial (DELETE) usando una Connection propia.
     */
    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            deleteById(id, conn);
        }
    }

    // ======================================================
    // CRUD con Connection externa (para transacciones)
    // ======================================================

    /**
     * Inserto una credencial recibiendo la Connection desde afuera.
     * Esto me permite usarla dentro de una transacción compartida con Usuario.
     */
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

            // Recupero el ID autogenerado por la base y lo seteo en el objeto.
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

    /**
     * Busco una credencial por ID usando una Connection externa.
     * Solo traigo registros que no estén marcados como eliminados.
     */
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

    /**
     * Devuelvo todas las credenciales usando una Connection externa.
     * Solo incluyo las que no estén eliminadas lógicamente.
     */
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

    /**
     * Actualizo todos los campos de una credencial, usando una Connection externa.
     */
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

    /**
     * Baja lógica de credencial usando una Connection externa.
     * Solo marco eliminado = TRUE.
     */
    @Override
    public void softDeleteById(Long id, Connection conn) throws SQLException {
        final String sql = "UPDATE credencial_acceso SET eliminado = TRUE WHERE id_credencial = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Baja física de credencial usando una Connection externa.
     */
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

    /**
     * Busco la credencial asociada a un usuario, filtrando por usuario_id.
     * Solo traigo la credencial si no está eliminada lógicamente.
     */
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

    /**
     * Actualizo el password de forma "segura" llamando a un stored procedure:
     * sp_actualizar_password_seguro.
     * 
     * Este método pide su propia Connection.
     */
    @Override
    public void updatePasswordSeguro(Long usuarioId, String nuevoHash, String nuevoSalt) throws SQLException {
        // Llama al procedimiento almacenado: sp_actualizar_password_seguro
        try (Connection conn = DatabaseConnection.getConnection()) {
            updatePasswordSeguro(usuarioId, nuevoHash, nuevoSalt, conn);
        }
    }

    /**
     * Versión sobrecargada que recibe la Connection desde afuera,
     * para poder ser usada dentro de una transacción más grande.
     */
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

    /**
     * Convierto una fila del ResultSet en un objeto CredencialAcceso.
     * 
     * Acá mapeo cada columna de la tabla a su campo correspondiente en la entidad.
     */
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

    /**
     * Helper para setear un LocalDateTime en un PreparedStatement,
     * permitiendo también valores nulos.
     */
    private void setNullableTimestamp(PreparedStatement ps, int index, LocalDateTime ldt) throws SQLException {
        if (ldt == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(ldt));
        }
    }
}
