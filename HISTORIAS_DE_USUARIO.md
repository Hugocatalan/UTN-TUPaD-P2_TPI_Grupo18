# Historias de Usuario - Sistema de Gestión de Usuarios y Credenciales

Especificaciones funcionales completas del sistema CRUD de usuarios y credenciales de acceso.

## Tabla de Contenidos

- [Épica 1: Gestión de Usuarios](#épica-1-gestión-de-usuarios)
- [Épica 2: Gestión de Credenciales](#épica-2-gestión-de-credenciales)
- [Épica 3: Operaciones Asociadas](#épica-3-operaciones-asociadas)
- [Reglas de Negocio](#reglas-de-negocio)
- [Modelo de Datos](#modelo-de-datos)

---

## Épica 1: Gestión de Usuarios

### HU-001: Crear Usuario Simple
**Como** administrador  
**Quiero** crear un usuario con datos básicos  
**Para** registrarlo en el sistema  

#### Criterios de Aceptación
- Se ingresan username, nombre, apellido, email, estado.  
- El sistema genera automáticamente el ID y la fecha de registro.  
- El usuario queda activo y no eliminado.  

---

### HU-002: Listar Usuarios
**Como** administrador  
**Quiero** ver todos los usuarios activos  
**Para** consultar la información registrada  

#### Criterios de Aceptación
- Se listan solo usuarios con `eliminado = FALSE`.  
- Si no hay usuarios, se muestra "(sin usuarios)".  

---

### HU-003: Ver Usuario por ID
**Como** administrador  
**Quiero** consultar un usuario por su ID  
**Para** verificar sus datos  

#### Criterios de Aceptación
- Si el ID existe, se muestra el usuario.  
- Si no existe, se muestra "(no encontrado)".  

---

### HU-004: Buscar Usuario por Username
**Como** administrador  
**Quiero** buscar un usuario por su nombre de usuario  
**Para** localizarlo rápidamente  

#### Criterios de Aceptación
- Si existe, se muestra el usuario.  
- Si no existe, se muestra "(no encontrado)".  

---

### HU-005: Buscar Usuario por Email
**Como** administrador  
**Quiero** buscar un usuario por su email  
**Para** verificar si está registrado  

#### Criterios de Aceptación
- Si existe, se muestra el usuario.  
- Si no existe, se muestra "(no encontrado)".  

---

### HU-006: Actualizar Usuario
**Como** administrador  
**Quiero** modificar los datos de un usuario existente  
**Para** mantener la información actualizada  

#### Criterios de Aceptación
- Campos vacíos mantienen el valor original.  
- Se valida estado y activo.  
- Se muestra "Usuario actualizado." al finalizar.  

---

### HU-007: Eliminar Usuario (Baja Lógica)
**Como** administrador  
**Quiero** marcar un usuario como eliminado  
**Para** mantener historial sin borrar físicamente  

#### Criterios de Aceptación
- Se actualiza `eliminado = TRUE`.  
- Se muestra "Usuario marcado como eliminado."  

---

### HU-008: Eliminar Usuario (Baja Física)
**Como** administrador  
**Quiero** eliminar un usuario físicamente  
**Para** removerlo definitivamente de la base  

#### Criterios de Aceptación
- Se ejecuta `DELETE FROM usuario WHERE id = ?`.  
- Se muestra "Usuario eliminado físicamente."  

---

### HU-009: Crear Usuario con Credencial (Transacción)
**Como** administrador  
**Quiero** crear un usuario junto con su credencial en una sola operación  
**Para** garantizar consistencia  

#### Criterios de Aceptación
- Se crean usuario y credencial en la misma transacción.  
- Se muestra "Transacción OK. Usuario id=X + credencial creada."  

---

## Épica 2: Gestión de Credenciales

### HU-010: Crear Credencial para Usuario Existente
**Como** administrador  
**Quiero** asociar una credencial a un usuario ya creado  
**Para** habilitar su acceso seguro  

#### Criterios de Aceptación
- Se guarda hash de contraseña con salt.  
- Se muestra "Credencial creada con id=X".  

---

### HU-011: Login de Usuario
**Como** usuario final  
**Quiero** iniciar sesión con mi username y contraseña  
**Para** acceder al sistema  

#### Criterios de Aceptación
- Se valida username existente.  
- Se valida credencial asociada.  
- Se compara contraseña ingresada con hash+salt.  
- Si es correcto: "Login exitoso. Bienvenido, [nombre]".  
- Si es incorrecto: "Contraseña incorrecta."  

---

## Reglas de Negocio

### Validación de Datos (RN-001 a RN-013)
- **RN-001**: Username, nombre, apellido y email son obligatorios.  
- **RN-002**: El ID de usuario se genera automáticamente.  
- **RN-003**: Espacios iniciales y finales se eliminan.  
- **RN-004**: Estado debe ser ACTIVO o INACTIVO.  
- **RN-005**: Email debe ser único.  
- **RN-006**: No se permiten valores nulos en campos obligatorios.  
- **RN-007**: Fecha de registro se genera automáticamente.  
- **RN-008**: Usuario inicia como activo.  
- **RN-009**: Credencial requiere hash y salt.  
- **RN-010**: Contraseña nunca se guarda en texto plano.  
- **RN-011**: Salt debe almacenarse junto al hash.  
- **RN-012**: Última sesión se actualiza en login exitoso.  
- **RN-013**: Credencial inicia como ACTIVO y no eliminado.  

### Operaciones de Base de Datos (RN-014 a RN-027)
- **RN-014**: CRUD implementado con PreparedStatements.  
- **RN-015**: Baja lógica usa UPDATE, no DELETE.  
- **RN-016**: Baja física solo en casos explícitos.  
- **RN-017**: Se valida ID > 0 en operaciones.  
- **RN-018**: Se usa RETURN_GENERATED_KEYS para IDs.  
- **RN-019**: Listar usuarios solo muestra no eliminados.  
- **RN-020**: Listar credenciales solo muestra no eliminadas.  
- **RN-021**: Se verifica rowsAffected en UPDATE/DELETE.  
- **RN-022**: Transacciones garantizan consistencia.  
- **RN-023**: Usuario y credencial se crean juntos en HU-009.  
- **RN-024**: Stored procedure para actualizar password.  
- **RN-025**: Se usa CallableStatement para SP.  
- **RN-026**: Se permite rollback en transacciones.  
- **RN-027**: Conexión se cierra correctamente al finalizar.  

### Integridad Referencial (RN-028 a RN-041)
- **RN-028**: Credencial siempre ligada a un usuario.  
- **RN-029**: FK usuario_id en credencial no puede ser nulo.  
- **RN-030**: Usuario puede existir sin credencial.  
- **RN-031**: Credencial no puede existir sin usuario.  
- **RN-032**: Baja lógica de usuario no elimina credencial.  
- **RN-033**: Baja física de usuario elimina credencial asociada.  
- **RN-034**: Login requiere credencial asociada.  
- **RN-035**: Estado de usuario afecta login.  
- **RN-036**: Estado de credencial afecta login.  
- **RN-037**: Última sesión se guarda en credencial.  
- **RN-038**: Password se valida con hash+salt.  
- **RN-039**: Salt puede ser fijo o generado.  
- **RN-040**: Credencial puede requerir reset.  
- **RN-041**: Usuario eliminado no puede loguearse.  

### Transacciones y Coordinación (RN-042 a RN-051)
- **RN-042**: UsuarioService coordina con CredencialService.  
- **RN-043**: Crear usuario con credencial es transaccional.  
- **RN-044**: Se usa try-with-resources en JDBC.  
- **RN-045**: Se previene SQL injection con parámetros.  
- **RN-046**: Se usa Optional para evitar null.  
- **RN-047**: Se valida estado antes de persistir.  
- **RN-048**: Se permite rollback en errores.  
- **RN-049**: Password update usa SP.  
- **RN-050**: Login actualiza última sesión.  
- **RN-051**: Scanner se cierra al salir del menú.  

---

## Modelo de Datos

### Diagrama Entidad-Relación


```
┌───────────────────────────────────┐
│             usuario               │
├───────────────────────────────────┤
│ id_usuario: BIGINT PK             │
│ username: VARCHAR(50) NOT NULL    │
│ nombre: VARCHAR(50) NOT NULL      │
│ apellido: VARCHAR(50) NOT NULL    │
│ email: VARCHAR(100) NOT NULL      │
│ fecha_registro: DATETIME NOT NULL │
│ activo: BOOLEAN DEFAULT TRUE      │
│ estado: ENUM('ACTIVO','INACTIVO') │
│ eliminado: BOOLEAN DEFAULT FALSE  │
└───────────────┬───────────────────┘
                │ 1..1 (FK usuario_id)
                ▼
┌───────────────────────────────────┐
│        credencial_acceso          │
├───────────────────────────────────┤
│ id_credencial: BIGINT PK          │
│ usuario_id: BIGINT FK NOT NULL    │
│ estado: ENUM('ACTIVO','INACTIVO') │
│ ultima_sesion: DATETIME NULL      │
│ hash_password: VARCHAR(256)       │
│ salt: VARCHAR(50)                 │
│ ultimo_cambio: DATETIME           │
│ requiere_reset: BOOLEAN           │
│ eliminado: BOOLEAN DEFAULT FALSE  │
└───────────────────────────────────┘

```


---

### Constraints y Validaciones

```sql
-- Unicidad en username y email
ALTER TABLE usuario ADD CONSTRAINT uk_username UNIQUE (username);
ALTER TABLE usuario ADD CONSTRAINT uk_email UNIQUE (email);

-- Relación 1:1 entre usuario y credencial
ALTER TABLE credencial_acceso
  ADD CONSTRAINT fk_usuario_credencial
  FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);

-- Validación de estado
ALTER TABLE usuario
  ADD CONSTRAINT chk_estado_usuario CHECK (estado IN ('ACTIVO','INACTIVO'));

ALTER TABLE credencial_acceso
  ADD CONSTRAINT chk_estado_credencial CHECK (estado IN ('ACTIVO','INACTIVO'));

```

## Queries Principales

### Crear Usuario
```sql
INSERT INTO usuario (eliminado, username, nombre, apellido, email, fecha_registro, activo, estado)
VALUES (FALSE, ?, ?, ?, ?, NOW(), TRUE, ?);
```

### Crear Credencial
```sql
INSERT INTO credencial_acceso (eliminado, usuario_id, estado, ultima_sesion, hash_password, salt, ultimo_cambio, requiere_reset)
VALUES (FALSE, ?, 'ACTIVO', NULL, ?, ?, NOW(), FALSE);
```

### Login de Usuario
```sql
SELECT u.id_usuario, u.nombre, c.hash_password, c.salt
FROM usuario u
JOIN credencial_acceso c ON u.id_usuario = c.usuario_id
WHERE u.username = ? AND u.eliminado = FALSE AND c.eliminado = FALSE;
```


### Índices Recomendados
```sql
CREATE INDEX idx_usuario_username ON usuario(username);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_estado ON usuario(estado);
CREATE INDEX idx_credencial_usuario ON credencial_acceso(usuario_id);
CREATE INDEX idx_credencial_estado ON credencial_acceso(estado);
```

## Flujos Técnicos Críticos

### Flujo 1: Crear Usuario con Credencial (Transacción)
- Capturar datos de usuario.  
- Crear objeto `Usuario`.  
- Crear objeto `CredencialAcceso` con hash+salt.  
- Asociar credencial al usuario.  
- Ejecutar `usuarioService.createUsuarioConCredencial(u)` → inserta ambos en la misma transacción.  

---

### Flujo 2: Login de Usuario
- Buscar usuario por username.  
- Buscar credencial asociada.  
- Validar contraseña ingresada con `PasswordUtil.validatePassword()`.  
- Si es correcta, actualizar `ultima_sesion`.  
- Mostrar mensaje de bienvenida.  

---

### Flujo 3: Actualización de Password Seguro
- Capturar nuevo password.  
- Generar hash y salt.  
- Ejecutar stored procedure `sp_actualizar_password_seguro`.  
- Actualizar credencial en BD.  
- Confirmar con mensaje **"Password actualizada vía stored procedure"**.  

---

## Resumen de Operaciones del Menú

| Opción | Operación | Handler | HU |
|--------|-----------|---------|----|
| 1 | Crear Usuario (simple) | `crearUsuarioSimple()` | HU-001 |
| 2 | Listar Usuarios | `listarUsuarios()` | HU-002 |
| 3 | Ver Usuario por ID | `verUsuarioPorId()` | HU-003 |
| 4 | Buscar Usuario por Username | `buscarUsuarioPorUsername()` | HU-004 |
| 5 | Buscar Usuario por Email | `buscarUsuarioPorEmail()` | HU-005 |
| 6 | Actualizar Usuario | `actualizarUsuario()` | HU-006 |
| 7 | Eliminar Usuario (baja lógica) | `bajaLogicaUsuario()` | HU-007 |
| 8 | Eliminar Usuario (baja física) | `bajaFisicaUsuario()` | HU-008 |
| 9 | Crear Usuario + Credencial (Tx) | `crearUsuarioConCredencialTx()` | HU-009 |
| 10 | Crear Credencial para Usuario | `crearCredencialParaUsuario()` | HU-010 |
| 11 | Ver Credencial por Usuario ID | `verCredencialPorUsuarioId()` | - |
| 12 | Ver Datos de Credencial por ID (de la credencial) | `verCredencialPorId()` | - |
| 13 | Actualizar Password Seguro | `actualizarPasswordSeguro()` | - |
| 14 | Login de Usuario | `loginUsuario()` | HU-011 |
| 0 | Salir | `System.exit()` | - |

---

**Versión**: 1.0  
**Total Historias de Usuario**: 11  
**Total Reglas de Negocio**: 51  
**Arquitectura**: 4 capas (Main → Service → DAO → Models)

<br>

**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2 - Grupo 18 - Hugo Catalan, Ignacio Carné, Matias Carro, Gabriel Carbajal



