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
 *
 * En esta clase centralizo la lógica de "negocio" relacionada con las credenciales:
 * - Delego las operaciones CRUD al DAO.
 * - Aplico validaciones mínimas cuando corresponde (por ejemplo, al actualizar password).
 *
 * La idea es que desde el menú o desde otras capas se hable con el Service
 * y no directamente con el DAO.
 */
public class CredencialAccesoServiceImpl implements CredencialAccesoService {

    // DAO que realmente se conecta a la base y ejecuta SQL
    private final CredencialAccesoDao credencialDao;

    /**
     * Constructor por defecto: instancio el DAO concreto que usa JDBC.
     */
    public CredencialAccesoServiceImpl() {
        this.credencialDao = new CredencialAccesoDaoImpl();
    }

    /**
     * Constructor alternativo: lo uso para inyectar un DAO "mock" en tests
     * o para cambiar la implementación sin modificar el código de arriba.
     */
    public CredencialAccesoServiceImpl(CredencialAccesoDao credencialDao) {
        this.credencialDao = credencialDao;
    }

    // ================================
    // CRUD genérico
    // ================================

    /**
     * Creo una nueva credencial delegando directamente en el DAO.
     */
    @Override
    public Long create(CredencialAcceso entity) throws SQLException {
        return credencialDao.create(entity);
    }

    /**
     * Busco una credencial por su ID.
     */
    @Override
    public Optional<CredencialAcceso> findById(Long id) throws SQLException {
        return credencialDao.findById(id);
    }

    /**
     * Listo todas las credenciales no eliminadas.
     */
    @Override
    public List<CredencialAcceso> findAll() throws SQLException {
        return credencialDao.findAll();
    }

    /**
     * Actualizo una credencial existente.
     */
    @Override
    public void update(CredencialAcceso entity) throws SQLException {
        credencialDao.update(entity);
    }

    /**
     * Realizo baja lógica de una credencial (marco eliminado = TRUE).
     */
    @Override
    public void softDeleteById(Long id) throws SQLException {
        credencialDao.softDeleteById(id);
    }

    /**
     * Realizo baja física de una credencial (DELETE).
     */
    @Override
    public void deleteById(Long id) throws SQLException {
        credencialDao.deleteById(id);
    }

    // ================================
    // Métodos específicos
    // ================================

    /**
     * Busco una credencial a partir del ID de usuario.
     * Esto lo uso en el login y en operaciones donde necesito la credencial asociada.
     */
    @Override
    public Optional<CredencialAcceso> findByUsuarioId(Long usuarioId) throws SQLException {
        return credencialDao.findByUsuarioId(usuarioId);
    }

    /**
     * Actualizo la contraseña de forma segura:
     * - Primero valido que el password no venga vacío.
     * - Genero un salt aleatorio.
     * - Calculo el hash usando SHA-256 + salt (PasswordUtil).
     * - Delego en el DAO para que llame al stored procedure que actualiza la contraseña.
     *
     * El parámetro "ignorar" queda solo para respetar la firma acordada en la interfaz
     * (por ejemplo, si en algún momento necesito pasar algo extra o para compatibilidad).
     */
    @Override
    public void updatePasswordSeguro(Long usuarioId, String nuevoPasswordPlano, String ignorar) throws SQLException {
        if (nuevoPasswordPlano == null || nuevoPasswordPlano.isBlank())
            throw new IllegalArgumentException("El password no puede estar vacío");

        // Genero un salt nuevo para esta actualización
        String salt = integradorfinal.programacion2.util.PasswordUtil.generateSalt(16);

        // Calculo el hash mezclando el password con el salt
        String hash = integradorfinal.programacion2.util.PasswordUtil.hashPassword(nuevoPasswordPlano, salt);

        // Delego al DAO para que llame al stored procedure con el hash y el salt
        credencialDao.updatePasswordSeguro(usuarioId, hash, salt);
    }

}
