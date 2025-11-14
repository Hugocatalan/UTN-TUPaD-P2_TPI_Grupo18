package integradorfinal.programacion2.service.impl;

import integradorfinal.programacion2.config.DatabaseConnection;
import integradorfinal.programacion2.dao.CredencialAccesoDao;
import integradorfinal.programacion2.dao.UsuarioDao;
import integradorfinal.programacion2.dao.impl.CredencialAccesoDaoImpl;
import integradorfinal.programacion2.dao.impl.UsuarioDaoImpl;
import integradorfinal.programacion2.entities.CredencialAcceso;
import integradorfinal.programacion2.entities.Estado;
import integradorfinal.programacion2.entities.Usuario;
import integradorfinal.programacion2.service.UsuarioService;
import integradorfinal.programacion2.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Capa de negocio para Usuario. Orquesta DAO + transacciones (commit/rollback).
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
        if (entity.getFechaRegistro() == null) {
            entity.setFechaRegistro(LocalDateTime.now());
        }
        if (entity.getEstado() == null) {
            entity.setEstado(Estado.ACTIVO);
        }
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
        if (entity.getEstado() == null) {
            entity.setEstado(Estado.ACTIVO);
        }
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
     * Crea un Usuario y su Credencial en una sola transacción. Orden correcto
     * para FK en credencial: primero USUARIO -> luego CREDENCIAL (con
     * usuario_id).
     */
    @Override
    public Long createUsuarioConCredencial(Usuario usuario) throws SQLException {
        // Validaciones mínimas de negocio
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username es obligatorio");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email es obligatorio");
        }

        CredencialAcceso cred = usuario.getCredencial();
        if (cred == null) {
            throw new IllegalArgumentException("La credencial es obligatoria");
        }
        if (cred.getHashPassword() == null || cred.getHashPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        // Defaults razonables
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado(Estado.ACTIVO);
        }
        if (cred.getEstado() == null) {
            cred.setEstado(Estado.ACTIVO);
        }
        if (cred.getUltimoCambio() == null) {
            cred.setUltimoCambio(LocalDateTime.now());
        }

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
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            throw ex;
        } finally {
            // Restaurar autocommit y cerrar conexión
            if (conn != null) {
                try {
                    conn.setAutoCommit(prevAutoCommit);
                } catch (SQLException ignore) {
                }
            }
            DatabaseConnection.closeConnection();
        }
    }

    /**
     * DEMOSTRACION ROLLBACK 
     * Demostración de una transacción con rollbackforzado.
     *
     * <p>
     * Este método no tiene sirve como prueba para mostrar cómo funciona el
     * manejo transaccional en JDBC:</p>
     *
     * <ol>
     * <li>Se inicia una transacción desactivando el autocommit.</li>
     * <li>Se crea un usuario de prueba en la base de datos (sin commit).</li>
     * <li>Se lanza intencionalmente una {@link SQLException} para simular un
     * error.</li>
     * <li>El bloque {@code catch} ejecuta un {@code rollback()}, revirtiendo la
     * operación y asegurando que el usuario no quede persistido.</li>
     * <li>Finalmente se restaura el estado original de autocommit y se cierra
     * la conexión.</li>
     * </ol>
     *
     * <p>
     * Al ejecutar este método, se puede comprobar en la base de datos que no se
     * insertan registros nuevos, validando que el rollback funciona
     * correctamente.</p>
     *
     * @throws SQLException siempre, ya que el error es forzado para demostrar
     * el rollback
     */
    @Override
    public void demoRollback() throws SQLException {
        Connection conn = null;
        boolean prevAutoCommit = true;

        try {
            System.out.println(">>> Iniciando demo de rollback con error simulado...");

            conn = DatabaseConnection.getConnection();
            prevAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // --- INICIO TRANSACCIÓN ---

            // 1) Crear un usuario de prueba
            Usuario u = new Usuario();
            u.setUsername("rollback_demo_" + System.currentTimeMillis());
            u.setNombre("Demo");
            u.setApellido("Rollback");
            u.setEmail("demo.rollback@example.com");
            u.setFechaRegistro(LocalDateTime.now());
            u.setActivo(true);
            u.setEstado(Estado.ACTIVO);
            u.setEliminado(false);

            Long idUsuario = usuarioDao.create(u, conn);
            System.out.println("Usuario demo creado con ID (sin commit): " + idUsuario);

            // 💣 2) ERROR FORZADO para demostrar rollback
            throw new SQLException("Error simulado para demostrar ROLLBACK");

            // 3) (Nunca llega acá)
            // conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // ROLLBACK REAL
                    System.out.println(">>> Se ejecutó ROLLBACK correctamente.");
                } catch (SQLException ignore) {
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(prevAutoCommit);
                } catch (SQLException ignore) {
                }
            }
            DatabaseConnection.closeConnection();
        }
    }

}
