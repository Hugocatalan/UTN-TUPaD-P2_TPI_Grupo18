## 💾 Proyecto Integrador Final – Programación 2 - UTN TUPAD 💾

| **Asignatura** | Programación 2 |
| :--- | :--- |
| **Carrera** | TUPAD - Tecnicatura Universitaria en Programación |
| **Universidad** | UTN - Universidad Tecnológica Nacional |

---

## 🚀 Equipo de Desarrollo (Grupo 18)

| 👤 Integrantes |
| :--- |
| **Hugo Catalan** |
| **Ignacio Carné** |
| **Matias Carro** |
| **Gabriel Carbajal**|

---

## 📖 Descripción del dominio elegido
El sistema desarrollado corresponde a un **módulo de gestión de usuarios y credenciales de acceso**.  

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos aprendidos durante el cursado de Programación 2

El dominio elegido se centra en:
- Registro y administración de usuarios.
- Manejo de credenciales seguras con hash + salt.
- Validación de login y actualización de última sesión.
- Control de estados (`ACTIVO` / `INACTIVO`) tanto en usuarios como en credenciales.

Este proyecto busca simular un **sistema de autenticación básico**, aplicando buenas prácticas de arquitectura en Java, JDBC y MySQL.

---

## ⚙️ Requisitos del Sistema

- **Java JDK 17** o superior.
- **Maven 3.9+** para compilación y gestión de dependencias.
- **MySQL 8.0** como motor de base de datos.



<br>

## Instalación
---

## 🗄️ Pasos para configurar la base de datos

#### 1. Crear la base de datos, podemos utilizar el script que se encuentra en:
<br>

```sql
O ejecutar el siguiente Script en MySQL:

DROP DATABASE IF EXISTS tpi_prog_2;
CREATE DATABASE IF NOT EXISTS tpi_prog_2
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE tpi_prog_2;	

CREATE TABLE usuario (
id_usuario INT NOT NULL AUTO_INCREMENT,
eliminado BOOLEAN NOT NULL DEFAULT FALSE, 
username VARCHAR(60) NOT NULL,
nombre VARCHAR(100) NOT NULL,
apellido VARCHAR(100) NOT NULL,
email VARCHAR(120) NOT NULL,
fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
activo BOOLEAN NOT NULL DEFAULT TRUE,
estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO', 
CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
CONSTRAINT uq_usuario_username UNIQUE (username),
CONSTRAINT uq_usuario_email UNIQUE (email),
CONSTRAINT ck_usuario_estado CHECK (estado IN ('ACTIVO','INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE credencial_acceso (
id_credencial INT NOT NULL AUTO_INCREMENT,
eliminado BOOLEAN NOT NULL DEFAULT FALSE,
usuario_id INT NOT NULL,
estado VARCHAR(15) NOT NULL,
ultima_sesion TIMESTAMP NULL,
hash_password VARCHAR(255) NOT NULL,
salt VARCHAR(64) NULL,
ultimo_cambio DATETIME NULL,
requiere_reset BOOLEAN NOT NULL DEFAULT FALSE,
CONSTRAINT pk_credencial PRIMARY KEY (id_credencial),
CONSTRAINT uq_credencial_usuario UNIQUE (usuario_id),
CONSTRAINT fk_credencial_usuario FOREIGN KEY (usuario_id)
REFERENCES usuario(id_usuario)
ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT ck_cred_estado CHECK (estado IN ('ACTIVO','INACTIVO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX ix_usuario_activo ON usuario(activo);
CREATE INDEX ix_usuario_estado ON usuario(estado);
CREATE INDEX ix_cred_estado ON credencial_acceso(estado);

DELIMITER $$
-- 1) Si el usuario está INACTIVO, su credencial no puede quedar ACTIVA.
CREATE TRIGGER bi_credencial_no_activa_si_usuario_inactivo
BEFORE INSERT ON credencial_acceso
FOR EACH ROW
BEGIN
DECLARE v_estado_usuario VARCHAR(15);
SELECT estado INTO v_estado_usuario
FROM usuario
WHERE id_usuario = NEW.usuario_id;
IF v_estado_usuario = 'INACTIVO' AND NEW.estado = 'ACTIVO' THEN
-- Forzamos a INACTIVO para mantener coherencia
SET NEW.estado = 'INACTIVO';
END IF;
END$$
CREATE TRIGGER bu_credencial_no_activa_si_usuario_inactivo
BEFORE UPDATE ON credencial_acceso
FOR EACH ROW
BEGIN
DECLARE v_estado_usuario VARCHAR(15);
SELECT estado INTO v_estado_usuario
FROM usuario
WHERE id_usuario = NEW.usuario_id;
IF v_estado_usuario = 'INACTIVO' AND NEW.estado = 'ACTIVO' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'No se puede activar la credencial de un usuario
INACTIVO.';
END IF;
END$$

-- 2) Si cambia el estado del usuario, sincronizamos la credencial.
CREATE TRIGGER au_usuario_sync_estado_credencial
AFTER UPDATE ON usuario
FOR EACH ROW
BEGIN
IF NEW.estado <> OLD.estado THEN
UPDATE credencial_acceso
SET estado = CASE
WHEN NEW.estado = 'INACTIVO' THEN 'INACTIVO'
ELSE estado -- si el usuario pasa a ACTIVO, no forzamos; respetamos la cred actual
END
WHERE usuario_id = NEW.id_usuario;
END IF;
END$$
DELIMITER ;

-- catalogo

CREATE TABLE estado (
    id_estado INT NOT NULL AUTO_INCREMENT,
    nombre_estado VARCHAR(20) NOT NULL,
    CONSTRAINT pk_estado PRIMARY KEY (id_estado),
    CONSTRAINT uq_estado UNIQUE (nombre_estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Carga inicial de valores del catálogo
INSERT INTO estado (nombre_estado)
VALUES ('ACTIVO'), ('INACTIVO');

-- Indices

-- elimina los indices si existian anteriormente
DROP INDEX ix_usuario_activo ON usuario;
DROP INDEX ix_usuario_estado ON usuario;
DROP INDEX ix_cred_estado ON credencial_acceso;

-- se crean los indices para la tabla.
CREATE INDEX ix_usuario_activo ON usuario(activo);
CREATE INDEX ix_usuario_estado ON usuario(estado);
CREATE INDEX ix_cred_estado ON credencial_acceso(estado);


/* ===========================================================
 CREDENCIALES 1:1 (coherentes con usuario.estado)
=========================================================== */
INSERT INTO credencial_acceso
(usuario_id, estado, ultima_sesion, eliminado, hash_password, salt,
ultimo_cambio, requiere_reset)
SELECT
u.id_usuario,
-- Si el usuario está INACTIVO, la credencial queda INACTIVA; si no, patrón de estados
CASE
WHEN u.estado = 'INACTIVO' THEN 'INACTIVO'
WHEN u.id_usuario % 5 = 0 THEN 'INACTIVO'
ELSE 'ACTIVO'
END AS estado,
TIMESTAMP(
DATE_SUB(CURDATE(), INTERVAL (u.id_usuario % 365) DAY),
SEC_TO_TIME((u.id_usuario % 86400))
),
FALSE,
UPPER(SHA2(CONCAT('pw:', u.id_usuario), 256)),
SUBSTRING(UPPER(SHA2(CONCAT('salt:', u.id_usuario), 256)), 1, 32),
DATE_SUB(NOW(), INTERVAL (u.id_usuario % 180) DAY),
(u.id_usuario % 20 = 0)
FROM usuario u
LEFT JOIN credencial_acceso c ON c.usuario_id = u.id_usuario
WHERE c.usuario_id IS NULL;


/* ===========================================================
 INSERCIÓN DE USUARIOS
=========================================================== */

INSERT INTO usuario 
(id_usuario, eliminado, username, nombre, apellido, email, fecha_registro, activo, estado)
VALUES
(1, 0, 'matias.carro', 'Matias', 'Carro', 'matias.carro@mail.com', '2025-11-14 00:22:53', 1, 'ACTIVO'),
(2, 0, 'hugo.catalan', 'Hugo', 'Catalan', 'hugo.catalan@mail.com', '2025-11-14 00:23:53', 1, 'ACTIVO'),
(3, 0, 'ignacio.carne', 'Ignacio', 'Carne', 'ignacio.carne@mail.com', '2025-11-14 00:24:40', 1, 'ACTIVO'),
(4, 0, 'gabriel.carbajal', 'Gabriel', 'Carbajal', 'gabriel.carbajal@mail.com', '2025-11-14 00:25:15', 1, 'ACTIVO');

INSERT INTO credencial_acceso 
(id_credencial, eliminado, usuario_id, estado, ultima_sesion, hash_password, salt, ultimo_cambio, requiere_reset)
VALUES
(1, 0, 1, 'ACTIVO', NULL, '9c54c37d7d49fc1f6bee5de14313915ba2aa7252550c8a63dbccdae791aed45f', '1f05a244324fbc1c2845c47911c81416', '2025-11-14 00:22:59', 0),
(2, 0, 2, 'ACTIVO', NULL, '83de667011fdd37c72b91dfa80754634309ce7590b28e5b8103134bb9d4ce9f2', 'a88c6229e545981effb8516c821f0cc2', '2025-11-14 00:23:59', 0),
(3, 0, 3, 'ACTIVO', NULL, 'b2b9d791185f19cf6df32ce9b8426583cd124b8272a9064f7c596515aeb34246', 'b2fe845062eae5cbcb78e726e5d66524', '2025-11-14 00:24:46', 0),
(4, 0, 4, 'ACTIVO', NULL, 'a5fed4461e10a0bd3f283be1658fd1eb7712f15fa2ec792d89c3068345801e84', '125b28f83d02162dbd9fc432a6c69fc0', '2025-11-14 00:25:21', 0);

```


#### 2. Agregar los usuarios de prueba (💻 Los integrantes del grupo 💻) 
nota: Los usuarios por defecto tienen la clave "12345" para realizar pruebas
<br>

#### 3. Modificar el archivo db.properties que se encuentra en src/main/resources/ (src/main/resources/db.properties dentro de proyecto)

- Este archivo permite cambiar la configuracion de la conexion con la base de datos, sin modificar codigo.

- “Definir variables de entorno DB_USER y DB_PASS antes de ejecutar”.

<br>


> ****************************
> CONFIGURACION BASE DE DATOS
> ****************************

> Host del servidor MySQL (ej: 127.0.0.1 o localhost)
> db.host=127.0.0.1

> Puerto del servidor MySQL (por defecto 3306)
> db.port=3306

> Nombre de la conexion / base de datos
> db.name=tpi_prog_2

> Usuario y contrase\u00f1a
> jdbc.user={DB_USER}
> jdbc.pass={DB_PASS}


> Driver JDBC de MySQL
> db.driver=com.mysql.cj.jdbc.Driver

## 🔨 Cómo Ejecutar

Luego de crear la base de datos y configurar la conexion:

1. Ejecutar el MAIN

2. Ya podes navegar el Menu. Los usuarios disponibles son:

> matias.carro

> hugo.catalan

> ignacio.carne

> gabriel.carbajal

Las credenciales: 12345


## COMPILAR:

### Windows
mvn clean compile
mvn clean package


3. Configurar Conexión (Opcional)

Por defecto conecta a:

    Host: localhost:3306
    Base de datos: dbtpi3
    Usuario: root
    Contraseña: (vacía)

- Para cambiar la configuración (en el uso del proyecto ) ir dentro del proyecto a:

src/main/resources y modificar el archivo "db.properties"

> \src\main\resources\db.properties

    ------------------------------
    CONFIGURACION BASE DE DATOS
    ------------------------------
    
    Host del servidor MySQL (ej: 127.0.0.1 o localhost)
    db.host=127.0.0.1
    
    Puerto del servidor MySQL (por defecto 3306)
    db.port=3306
    
    Nombre de la conexion / base de datos
    db.name=tpi_prog_2
    
    Usuario y contrase\u00f1a
    jdbc.user=root
    jdbc.pass=1234
    
    Driver JDBC de MySQL
    db.driver=com.mysql.cj.jdbc.Driver


- Para cambiar la configuracion y ejecutar el proyecto compilado ir a:

> target\classes\db.properties

Y modificar el archivo de la misma manera


## Uso del Sistema

### Menú Principal

```
========= MENU TFI (Usuario / Credencial) =========
 1) Crear Usuario (simple)
 2) Listar Usuarios
 3) Ver Usuario por ID
 4) Buscar Usuario por USERNAME
 5) Buscar Usuario por EMAIL
 6) Actualizar Usuario
 7) Eliminar Usuario (baja logica)
 8) Eliminar Usuario (baja fisica)
 9) Crear Usuario + Credencial (TRANSACCIÓN)
10) Crear Credencial para Usuario existente
11) Ver Credencial por ID
12) Ver Credencial por usuarioId
13) Actualizar password (stored procedure)
14) Login de Usuario (validar password)
 0) Salir
```

## Operaciones Disponibles

#### 1. Crear Usuario (simple)
- Captura Username, Nombre, Apellido, Email y  Estado (ACTIVO / INACTIVO) 
- Valida Username e Email unico

**Ejemplo:**
```
Username: juan.perez
Nombre: Juan
Apellido: Perez
Email: juan.perez@mail.com
Estado (ACTIVO/INACTIVO): activo
```

#### 2. Listar Usuarios
- Muestra todos los usuarios 

### 3. Ver Usuario por ID
- Busca un usuario por su ID (se puede ver en listar, la misma es AUTOINCREMENT)

- Ejemplo:

'''
Opcion: 3

Conectado correctamente a la base de datos.
Usuario{idUsuario=1, username='matias.carro', email='matias.carro@mail.com', activo=true, estado=ACTIVO}
'''

### 4. Buscar Usuario por NOMBRE DE USUARIO

- Busca por username

Ejemplo:

'''
Opcion: 4
Username: matias.carro
✅ Conectado correctamente a la base de datos.
Usuario{idUsuario=1, username='matias.carro', email='matias.carro@mail.com', activo=true, estado=ACTIVO}
'''

