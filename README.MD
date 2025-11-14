# Proyecto Integrador Final – Programación 2 - UTN TUPAD
# Grupo 18
# Integrantes: 

## Hugo Catalan, Ignacio Carné, Matias Carro, Gabriel Carbajal


## 📖 Descripción del dominio elegido
El sistema desarrollado corresponde a un **módulo de gestión de usuarios y credenciales de acceso**.  
El dominio elegido se centra en:
- Registro y administración de usuarios.
- Manejo de credenciales seguras con hash + salt.
- Validación de login y actualización de última sesión.
- Control de estados (`ACTIVO` / `INACTIVO`) tanto en usuarios como en credenciales.

Este proyecto busca simular un **sistema de autenticación básico**, aplicando buenas prácticas de arquitectura en Java, JDBC y MySQL.

---

## ⚙️ Requisitos

- **Java JDK 17** o superior.
- **Maven 3.8+** para compilación y gestión de dependencias.
- **MySQL 8.0** como motor de base de datos.

---

## 🗄️ Pasos para crear la base de datos

1. Crear la base de datos, utilizando el script que se encuentra en:

2. Agregar los usuarios de prueba (💻 Los integrantes del grupo 💻) 
nota: Los usuarios por defecto tienen la clave "12345" para realizar pruebas

3. Modificar el archivo db.properties que se encuentra en src/main/resources/ (src/main/resources/db.properties dentro de proyecto)

Este archivo permite cambiar la configuracion de la conexion con la base de datos, sin modificar codigo.

Dentro de este estan lo valores por defecto y se puede modificar lo que se requiera luego de los =
por ejemplo, para cambiar el puerto db.port=####

### ==============================
### CONFIGURACI\u00d3N BASE DE DATOS
### ==============================

### Host del servidor MySQL (ej: 127.0.0.1 o localhost)
db.host=127.0.0.1

### Puerto del servidor MySQL (por defecto 3306)
db.port=3306

### Nombre de la conexion / base de datos
db.name=tpi_prog_2

### Usuario y contrase\u00f1a
jdbc.user=root
jdbc.pass=1234

### Driver JDBC de MySQL
db.driver=com.mysql.cj.jdbc.Driver


