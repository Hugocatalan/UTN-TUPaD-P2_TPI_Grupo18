package integradorfinal.programacion2.dao;

import integradorfinal.programacion2.entities.Usuario;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Interfaz DAO para la entidad Usuario.
 * Extiende las operaciones CRUD genéricas y puede declarar métodos específicos.
 */
public interface UsuarioDao extends GenericDao<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario (username).
     *
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     * @throws SQLException si ocurre un error SQL
     */
    Optional<Usuario> findByUsername(String username) throws SQLException;

    /**
     * Busca un usuario por su email.
     *
     * @param email correo electrónico
     * @return Optional con el usuario si existe
     * @throws SQLException si ocurre un error SQL
     */
    Optional<Usuario> findByEmail(String email) throws SQLException;
}
