package integradorfinal.programacion2.exceptions;

/**
 * Excepción unchecked para la capa de persistencia/DAO.
 * Envuelve errores de SQL u otros de infraestructura para
 * no filtrar detalles técnicos a la capa superior.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
