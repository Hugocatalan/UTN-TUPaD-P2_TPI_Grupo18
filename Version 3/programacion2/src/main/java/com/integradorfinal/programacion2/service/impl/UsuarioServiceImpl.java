package com.integradorfinal.programacion2.service.impl;

import com.integradorfinal.programacion2.config.DatabaseConnection;
import com.integradorfinal.programacion2.dao.CredencialAccesoDao;
import com.integradorfinal.programacion2.dao.UsuarioDao;
import com.integradorfinal.programacion2.dao.impl.CredencialAccesoDaoImpl;
import com.integradorfinal.programacion2.dao.impl.UsuarioDaoImpl;
import com.integradorfinal.programacion2.entities.CredencialAcceso;
import com.integradorfinal.programacion2.entities.Estado;
import com.integradorfinal.programacion2.entities.Usuario;
import com.integradorfinal.programacion2.service.UsuarioService;
import com.integradorfinal.programacion2.util.PasswordUtil;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Capa de negocio para Usuario.
 * Orquesta DAO + transacciones (commit/rollback).
 */
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDao usuarioDao;
    private final CredencialAccesoDao credencialDao;

    // Inyección simple por defecto
    public UsuarioServiceImpl() {
        this.usuarioDao = new UsuarioDaoImpl();
        this.credencialDao = new CredencialAccesoDaoImpl();
    }

    // (Opcional) Inyección por constructor para tests
    public UsuarioServiceImpl(UsuarioDao usuarioDao, CredencialAccesoDao credencialDao) {
        this.usuarioDao = usuarioDao;
        this.credencialDao = credencialDao;
    }

    // ================================
    // CRUD (delegan en DAO)
    // ================================
    @Override
    public Long create(Usuario entity) throws SQLException {
        // Si viene sin fecha/estado, default razonables
        if (entity.getFechaRegistro() == null) entity.setFechaRegistro(LocalDateTime.now());
        if (entity.getEstado() == null) entity.setEstado(Estado.ACTIVO);
        return usuarioDao.create(entity);
    }

    @Override
    public Optional<Usuario> findById(Long id) throws SQLException {
        return usuarioDao.findById(id);
    }

    @Override
    public List<Usuario> findAll() throws SQLException {
        return usuarioDao.findAll();
    }

    @Override
    public void update(Usuario entity) throws SQLException {
        if (entity.getEstado() == null) entity.setEstado(Estado.ACTIVO);
        usuarioDao.update(entity);
    }

    @Override
    public void softDeleteById(Long id) throws SQLException {
        usuarioDao.softDeleteById(id);
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        usuarioDao.deleteById(id);
    }

    // ================================
    // Métodos específicos
    // ================================
    @Override
    public Optional<Usuario> findByUsername(String username) throws SQLException {
        return usuarioDao.findByUsername(username);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) throws SQLException {
        return usuarioDao.findByEmail(email);
    }

    /**
     * Crea un Usuario y su Credencial en una sola transacción.
     * Orden correcto para FK en credencial: primero USUARIO -> luego CREDENCIAL (con usuario_id).
     */
  @Override
public Long createUsuarioConCredencial(Usuario usuario) throws SQLException {
    // Validaciones mínimas de negocio
    if (usuario == null) throw new IllegalArgumentException("Usuario no puede ser null");
    if (usuario.getUsername() == null || usuario.getUsername().isBlank())
        throw new IllegalArgumentException("Username es obligatorio");
    if (usuario.getEmail() == null || usuario.getEmail().isBlank())
        throw new IllegalArgumentException("Email es obligatorio");

    CredencialAcceso cred = usuario.getCredencial();
    if (cred == null) throw new IllegalArgumentException("La credencial es obligatoria");
    if (cred.getHashPassword() == null || cred.getHashPassword().isBlank())
        throw new IllegalArgumentException("La contraseña es obligatoria");

    // Defaults razonables
    if (usuario.getFechaRegistro() == null) usuario.setFechaRegistro(LocalDateTime.now());
    if (usuario.getEstado() == null) usuario.setEstado(Estado.ACTIVO);
    if (cred.getEstado() == null) cred.setEstado(Estado.ACTIVO);
    if (cred.getUltimoCambio() == null) cred.setUltimoCambio(LocalDateTime.now());

    // ===== Seguridad de contraseña =====
    String salt = PasswordUtil.generateSalt(16); // genera un salt aleatorio de 16 bytes
    String hash = PasswordUtil.hashPassword(cred.getHashPassword(), salt);

    cred.setSalt(salt);
    cred.setHashPassword(hash);

    // ===================================

    Connection conn = null;
    boolean prevAutoCommit = true;

    try {
        conn = DatabaseConnection.getConnection();
        prevAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false); // --- INICIO TRANSACCIÓN ---

        // 1) Crear USUARIO (genera id)
        Long userId = usuarioDao.create(usuario, conn);

        // 2) Crear CREDENCIAL con FK al usuario recién creado
        cred.setUsuarioId(userId);
        credencialDao.create(cred, conn);

        // 3) Commit si todo OK
        conn.commit();
        return userId;

    } catch (SQLException ex) {
        // Rollback ante cualquier problema
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignore) {}
        }
        throw ex;
    } finally {
        // Restaurar autocommit y cerrar conexión
        if (conn != null) {
            try {
                conn.setAutoCommit(prevAutoCommit);
            } catch (SQLException ignore) {}
        }
        DatabaseConnection.closeConnection();
    }
}

}
