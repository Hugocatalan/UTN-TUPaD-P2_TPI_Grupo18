package com.integradorfinal.programacion2.entities;

import java.time.LocalDateTime;

/**
 * Representa la tabla 'credencial_acceso' en la base de datos.
 * Cada credencial pertenece a un usuario (relación 1:1).
 */
public class CredencialAcceso {

    private Long idCredencial;           // id_credencial
    private boolean eliminado;           // eliminado
    private Long usuarioId;              // FK a usuario.id_usuario
    private Estado estado;               // ACTIVO / INACTIVO
    private LocalDateTime ultimaSesion;  // TIMESTAMP
    private String hashPassword;         // hash_password
    private String salt;                 // salt
    private LocalDateTime ultimoCambio;  // DATETIME
    private boolean requiereReset;       // requiere_reset

    // --- Constructores ---
    public CredencialAcceso() {}

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

    // --- Getters y Setters ---
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
