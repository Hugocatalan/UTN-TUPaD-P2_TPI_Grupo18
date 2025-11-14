package com.integradorfinal.programacion2.dao;

import com.integradorfinal.programacion2.entities.CredencialAcceso;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Interfaz DAO para la entidad CredencialAcceso.
 * Extiende las operaciones genéricas y agrega métodos específicos.
 */
public interface CredencialAccesoDao extends GenericDao<CredencialAcceso, Long> {

    /**
     * Busca la credencial de un usuario por su ID.
     *
     * @param usuarioId identificador del usuario
     * @return Optional con la credencial si existe
     * @throws SQLException si ocurre un error SQL
     */
    Optional<CredencialAcceso> findByUsuarioId(Long usuarioId) throws SQLException;

    /**
     * Actualiza de forma segura la contraseña y el salt del usuario.
     * Puede implementarse llamando al procedimiento almacenado
     * sp_actualizar_password_seguro().
     *
     * @param usuarioId    ID del usuario
     * @param nuevoHash    nuevo hash de contraseña
     * @param nuevoSalt    nuevo valor salt
     * @throws SQLException si ocurre un error SQL
     */
    void updatePasswordSeguro(Long usuarioId, String nuevoHash, String nuevoSalt) throws SQLException;
}
