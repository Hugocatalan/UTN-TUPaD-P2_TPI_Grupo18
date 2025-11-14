package com.integradorfinal.programacion2.service;

import com.integradorfinal.programacion2.entities.Usuario;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Servicio de negocio para Usuario.
 * Extiende el CRUD genérico y agrega métodos específicos.
 */
public interface UsuarioService extends GenericService<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     * @throws SQLException si ocurre un error de base de datos
     */
    Optional<Usuario> findByUsername(String username) throws SQLException;

    /**
     * Busca un usuario por su email.
     *
     * @param email correo electrónico
     * @return Optional con el usuario si existe
     * @throws SQLException si ocurre un error de base de datos
     */
    Optional<Usuario> findByEmail(String email) throws SQLException;

    /**
     * Caso de uso típico del integrador:
     * crear un Usuario y su Credencial en una única transacción.
     * La implementación abrirá una Connection con setAutoCommit(false),
     * usará los DAO correspondientes y realizará commit o rollback según el resultado.
     *
     * @param usuario entidad usuario con sus datos (y credencial asociada)
     * @return ID generado del nuevo usuario
     * @throws SQLException si ocurre un error de base de datos
     */
    Long createUsuarioConCredencial(Usuario usuario) throws SQLException;
}
