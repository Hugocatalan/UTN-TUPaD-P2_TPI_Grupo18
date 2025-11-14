package integradorfinal.programacion2.entities;

import java.time.LocalDateTime;

/**
 * Esta clase representa una fila de la tabla `usuario`.
 * 
 * La uso como contenedor de datos (POJO), sin lógica, para poder mover valores
 * entre la base de datos, los servicios y el menú.
 * 
 * Además, cada Usuario puede tener asociada una CredencialAcceso en una
 * relación 1:1, lo cual reflejo con el atributo `credencial`.
 */
public class Usuario {

    // Identificador del usuario (PK de la tabla)
    private Long idUsuario;

    // Baja lógica: TRUE = oculto / FALSE = activo
    private boolean eliminado;

    // Nombre único elegido por el usuario para loguearse
    private String username;

    // Datos personales básicos
    private String nombre;
    private String apellido;

    // Email único del usuario
    private String email;

    // Fecha y hora en la que el usuario fue registrado en el sistema
    private LocalDateTime fechaRegistro;

    // Activo indicando si el usuario puede operar o no
    private boolean activo;

    // Estado funcional del usuario (ACTIVO / INACTIVO)
    private Estado estado;

    // Relación 1:1 → cada usuario puede tener exactamente una credencial
    private CredencialAcceso credencial;


    // --- Constructores ---

    /** Constructor vacío: ideal para crear el objeto e ir cargando los campos uno a uno. */
    public Usuario() {}

    /**
     * Constructor completo: lo uso cuando ya conozco todos los campos.
     */
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

    /**
     * Mantengo este toString lo más limpio posible y sin datos sensibles.
     * Así puedo imprimir usuarios en consola sin mostrar emails completos,
     * contraseñas, etc.
     */
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
