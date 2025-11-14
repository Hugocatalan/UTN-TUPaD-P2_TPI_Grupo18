package com.integradorfinal.programacion2.service;

import com.integradorfinal.programacion2.entities.CredencialAcceso;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Servicio de negocio para CredencialAcceso.
 * Extiende el CRUD genérico y agrega operaciones específicas.
 */
public interface CredencialAccesoService extends GenericService<CredencialAcceso, Long> {

    /**
     * Obtiene la credencial asociada a un usuario.
     *
     * @param usuarioId ID del usuario
     * @return Optional con la credencial si existe
     * @throws SQLException error de base de datos
     */
    Optional<CredencialAcceso> findByUsuarioId(Long usuarioId) throws SQLException;

    /**
     * Actualiza de forma segura el hash y el salt de la contraseña,
     * idealmente llamando al procedimiento almacenado
     * sp_actualizar_password_seguro().
     *
     * @param usuarioId ID del usuario dueño de la credencial
     * @param nuevoHash nuevo hash de contraseña
     * @param nuevoSalt nuevo salt
     * @throws SQLException error de base de datos
     */
    void updatePasswordSeguro(Long usuarioId, String nuevoHash, String nuevoSalt) throws SQLException;
}
