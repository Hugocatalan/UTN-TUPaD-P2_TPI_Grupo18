package integradorfinal.programacion2.service.impl;

import integradorfinal.programacion2.dao.CredencialAccesoDao;
import integradorfinal.programacion2.dao.impl.CredencialAccesoDaoImpl;
import integradorfinal.programacion2.entities.CredencialAcceso;
import integradorfinal.programacion2.service.CredencialAccesoService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de CredencialAcceso.
 * Orquesta los DAO y aplica lógica de negocio básica.
 */
public class CredencialAccesoServiceImpl implements CredencialAccesoService {

    private final CredencialAccesoDao credencialDao;

    public CredencialAccesoServiceImpl() {
        this.credencialDao = new CredencialAccesoDaoImpl();
    }

    public CredencialAccesoServiceImpl(CredencialAccesoDao credencialDao) {
        this.credencialDao = credencialDao;
    }

    // ================================
    // CRUD genérico
    // ================================
    @Override
    public Long create(CredencialAcceso entity) throws SQLException {
        return credencialDao.create(entity);
    }

    @Override
    public Optional<CredencialAcceso> findById(Long id) throws SQLException {
        return credencialDao.findById(id);
    }

    @Override
    public List<CredencialAcceso> findAll() throws SQLException {
        return credencialDao.findAll();
    }

    @Override
    public void update(CredencialAcceso entity) throws SQLException {
        credencialDao.update(entity);
    }

    @Override
    public void softDeleteById(Long id) throws SQLException {
        credencialDao.softDeleteById(id);
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        credencialDao.deleteById(id);
    }

    // ================================
    // Métodos específicos
    // ================================
    @Override
    public Optional<CredencialAcceso> findByUsuarioId(Long usuarioId) throws SQLException {
        return credencialDao.findByUsuarioId(usuarioId);
    }

 @Override
public void updatePasswordSeguro(Long usuarioId, String nuevoPasswordPlano, String ignorar) throws SQLException {
    if (nuevoPasswordPlano == null || nuevoPasswordPlano.isBlank())
        throw new IllegalArgumentException("El password no puede estar vacío");

    String salt = integradorfinal.programacion2.util.PasswordUtil.generateSalt(16);
    String hash = integradorfinal.programacion2.util.PasswordUtil.hashPassword(nuevoPasswordPlano, salt);

    credencialDao.updatePasswordSeguro(usuarioId, hash, salt);
}


}
