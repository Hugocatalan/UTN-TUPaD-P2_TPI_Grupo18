package integradorfinal.programacion2.entities;

import java.time.LocalDateTime;

/**
 * Esta clase representa exactamente una fila de la tabla `credencial_acceso`.
 * 
 * La uso como un simple “contenedor de datos” (POJO/JavaBean). No tiene lógica,
 * solo almacena los valores que leo de la base o que voy a guardar.
 * 
 * Cada credencial está ligada a un usuario mediante una relación 1:1.
 * El campo `usuarioId` es la FK contra usuario.id_usuario.
 */
public class CredencialAcceso {

    // Identificador único de la credencial (PK)
    private Long idCredencial;

    // Flag de baja lógica (TRUE = eliminado)
    private boolean eliminado;

    // Foreign Key hacia la tabla usuario
    private Long usuarioId;

    // Estado funcional de la credencial (ACTIVO / INACTIVO)
    private Estado estado;

    // Fecha/hora de la última sesión exitosa del usuario
    private LocalDateTime ultimaSesion;

    // Hash SHA-256 de la contraseña (nunca guardo la contraseña en texto plano)
    private String hashPassword;

    // Salt usado para el hash (parte clave para validar contraseñas)
    private String salt;

    // Última vez que se modificó la contraseña
    private LocalDateTime ultimoCambio;

    // Indica si el usuario debe cambiar la contraseña en el próximo login
    private boolean requiereReset;


    // --- Constructores ---

    /** Constructor vacío: lo uso cuando armo el objeto manualmente campo por campo */
    public CredencialAcceso() {}

    /** Constructor completo: lo uso si ya conozco todos los valores */
    public CredencialAcceso(Long idCredencial, boolean eliminado, Long usuarioId,
                            Estado estado, LocalDateTime ultimaSesion,
                            String hashPassword, String salt,
                            LocalDateTime ultimoCambio, boolean requiereReset) {

        this.idCredencial = idCredencial;
        this.eliminado = eliminado;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.ultimaSesion = ultimaSesion;
        this.hashPassword = hashPassword;
        this.salt = salt;
        this.ultimoCambio = ultimoCambio;
        this.requiereReset = requiereReset;
    }


    // --- Getters y Setters (acceso estándar para JDBC y servicios) ---

    public Long getIdCredencial() { return idCredencial; }
    public void setIdCredencial(Long idCredencial) { this.idCredencial = idCredencial; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDateTime getUltimaSesion() { return ultimaSesion; }
    public void setUltimaSesion(LocalDateTime ultimaSesion) { this.ultimaSesion = ultimaSesion; }

    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public LocalDateTime getUltimoCambio() { return ultimoCambio; }
    public void setUltimoCambio(LocalDateTime ultimoCambio) { this.ultimoCambio = ultimoCambio; }

    public boolean isRequiereReset() { return requiereReset; }
    public void setRequiereReset(boolean requiereReset) { this.requiereReset = requiereReset; }


    // --- toString ---
    /**
     * Dejo un toString reducido para no exponer información sensible
     * como el hash de la contraseña o el salt.
     */
    @Override
    public String toString() {
        return "CredencialAcceso{" +
                "idCredencial=" + idCredencial +
                ", usuarioId=" + usuarioId +
                ", estado=" + estado +
                ", requiereReset=" + requiereReset +
                '}';
    }
}
