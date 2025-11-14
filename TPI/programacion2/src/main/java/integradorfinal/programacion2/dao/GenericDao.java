package integradorfinal.programacion2.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO genérico con operaciones CRUD y soporte opcional para transacciones
 * (métodos sobrecargados que aceptan una Connection externa).
 *
 * @param <T>  Tipo de entidad (p.ej., Usuario, CredencialAcceso)
 * @param <ID> Tipo del identificador (p.ej., Long)
 */
public interface GenericDao<T, ID> {

    // --------- CRUD básico ---------
    ID create(T entity) throws SQLException;

    Optional<T> findById(ID id) throws SQLException;

    List<T> findAll() throws SQLException;

    void update(T entity) throws SQLException;

    /**
     * Baja lógica (si aplica). Implementación concreta decide el mecanismo.
     */
    void softDeleteById(ID id) throws SQLException;

    /**
     * Baja física (opcional). Úsese con criterio.
     */
    void deleteById(ID id) throws SQLException;


    // --------- Versión transaccional (misma Connection) ---------
    ID create(T entity, Connection conn) throws SQLException;

    Optional<T> findById(ID id, Connection conn) throws SQLException;

    List<T> findAll(Connection conn) throws SQLException;

    void update(T entity, Connection conn) throws SQLException;

    void softDeleteById(ID id, Connection conn) throws SQLException;

    void deleteById(ID id, Connection conn) throws SQLException;
}
