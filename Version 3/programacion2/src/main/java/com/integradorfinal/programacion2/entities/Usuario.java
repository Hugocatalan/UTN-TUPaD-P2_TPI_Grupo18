package com.integradorfinal.programacion2.entities;

import java.time.LocalDateTime;

/**
 * Representa la tabla 'usuario' en la base de datos.
 * Un usuario puede tener asociada una CredencialAcceso (1:1).
 */
public class Usuario {

    private Long idUsuario;              // id_usuario
    private boolean eliminado;           // eliminado
    private String username;             // username (UNIQUE)
    private String nombre;               // nombre
    private String apellido;             // apellido
    private String email;                // email (UNIQUE)
    private LocalDateTime fechaRegistro; // fecha_registro (DATETIME)
    private boolean activo;              // activo
    private Estado estado;               // ACTIVO / INACTIVO

    // Relación 1:1 (un usuario tiene una credencial)
    private CredencialAcceso credencial;

    // --- Constructores ---
    public Usuario() {}

    public Usuario(Long idUsuario, boolean eliminado, String username,
                   String nombre, String apellido, String email,
                   LocalDateTime fechaRegistro, boolean activo, Estado estado) {
        this.idUsuario = idUsuario;
        this.eliminado = eliminado;
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
        this.estado = estado;
    }

    // --- Getters y Setters ---
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public CredencialAcceso getCredencial() { return credencial; }
    public void setCredencial(CredencialAcceso credencial) { this.credencial = credencial; }

    // --- toString ---
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", estado=" + estado +
                '}';
    }
}
