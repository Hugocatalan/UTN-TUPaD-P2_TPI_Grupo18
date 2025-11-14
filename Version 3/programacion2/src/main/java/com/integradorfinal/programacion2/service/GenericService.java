package com.integradorfinal.programacion2.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para la capa de servicio.
 * Envuelve las operaciones CRUD de los DAO
 * y permite aplicar reglas de negocio adicionales.
 *
 * @param <T>  tipo de entidad (p.ej., Usuario)
 * @param <ID> tipo del identificador (p.ej., Long)
 */
public interface GenericService<T, ID> {

    ID create(T entity) throws SQLException;

    Optional<T> findById(ID id) throws SQLException;

    List<T> findAll() throws SQLException;

    void update(T entity) throws SQLException;

    void softDeleteById(ID id) throws SQLException;

    void deleteById(ID id) throws SQLException;
}
