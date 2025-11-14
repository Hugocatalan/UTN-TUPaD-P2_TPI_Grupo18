package com.integradorfinal.programacion2.app;

import com.integradorfinal.programacion2.entities.*;
import com.integradorfinal.programacion2.service.UsuarioService;
import com.integradorfinal.programacion2.service.CredencialAccesoService;
import com.integradorfinal.programacion2.service.impl.UsuarioServiceImpl;
import com.integradorfinal.programacion2.service.impl.CredencialAccesoServiceImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AppMenu {

    private final Scanner sc = new Scanner(System.in);
    private final UsuarioService usuarioService = new UsuarioServiceImpl();
    private final CredencialAccesoService credService = new CredencialAccesoServiceImpl();

    public static void main(String[] args) {
        new AppMenu().run();
    }

    public void run() {
        int op;
        do {
            mostrarMenu();
            op = leerInt("Opción");
            try {
                switch (op) {
                    case 1 -> crearUsuarioSimple();
                    case 2 -> listarUsuarios();
                    case 3 -> verUsuarioPorId();
                    case 4 -> buscarUsuarioPorUsername();
                    case 5 -> buscarUsuarioPorEmail();
                    case 6 -> actualizarUsuario();
                    case 7 -> bajaLogicaUsuario();
                    case 8 -> bajaFisicaUsuario();
                    case 9 -> crearUsuarioConCredencialTx();
                    case 10 -> crearCredencialParaUsuario();
                    case 11 -> verCredencialPorId();
                    case 12 -> verCredencialPorUsuarioId();
                    case 13 -> actualizarPasswordSeguro();
                    case 14 -> loginUsuario();
                    case 0 -> System.out.println("👋 Saliendo...");
                    default -> System.out.println("⚠️ Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            System.out.println();
        } while (op != 0);
    }

    private void mostrarMenu() {
        System.out.println("========= MENÚ TFI (Usuario / Credencial) =========");
        System.out.println(" 1) Crear Usuario (simple)");
        System.out.println(" 2) Listar Usuarios");
        System.out.println(" 3) Ver Usuario por ID");
        System.out.println(" 4) Buscar Usuario por USERNAME");
        System.out.println(" 5) Buscar Usuario por EMAIL");
        System.out.println(" 6) Actualizar Usuario");
        System.out.println(" 7) Eliminar Usuario (baja lógica)");
        System.out.println(" 8) Eliminar Usuario (baja física)");
        System.out.println(" 9) Crear Usuario + Credencial (TRANSACCIÓN)");
        System.out.println("10) Crear Credencial para Usuario existente");
        System.out.println("11) Ver Credencial por ID");
        System.out.println("12) Ver Credencial por usuarioId");
        System.out.println("13) Actualizar password (stored procedure)");
        System.out.println("14) Login de Usuario (validar password)");
        System.out.println(" 0) Salir");
    }

    // ===================== USUARIO =====================

    private void crearUsuarioSimple() throws SQLException {
        Usuario u = new Usuario();
        u.setEliminado(false);
        u.setUsername(leerStr("Username"));
        u.setNombre(leerStr("Nombre"));
        u.setApellido(leerStr("Apellido"));
        u.setEmail(leerStr("Email"));
        u.setFechaRegistro(LocalDateTime.now());
        u.setActivo(true);
        u.setEstado(leerEstado());

        Long id = usuarioService.create(u);
        System.out.println("✅ Usuario creado con id=" + id);
    }

    private void listarUsuarios() throws SQLException {
        List<Usuario> lista = usuarioService.findAll();
        if (lista.isEmpty()) {
            System.out.println("(sin usuarios)");
            return;
        }
        lista.forEach(System.out::println);
    }

    private void verUsuarioPorId() throws SQLException {
        long id = leerLong("ID de usuario");
        Optional<Usuario> u = usuarioService.findById(id);
        System.out.println(u.map(Object::toString).orElse("(no encontrado)"));
    }

    private void buscarUsuarioPorUsername() throws SQLException {
        String username = leerStr("Username");
        Optional<Usuario> u = usuarioService.findByUsername(username);
        System.out.println(u.map(Object::toString).orElse("(no encontrado)"));
    }

    private void buscarUsuarioPorEmail() throws SQLException {
        String email = leerStr("Email");
        Optional<Usuario> u = usuarioService.findByEmail(email);
        System.out.println(u.map(Object::toString).orElse("(no encontrado)"));
    }

    private void actualizarUsuario() throws SQLException {
        long id = leerLong("ID de usuario");
        Optional<Usuario> opt = usuarioService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("(no existe ese usuario)");
            return;
        }
        Usuario u = opt.get();
        System.out.println("Editando: " + u);

        String username = leerStrOpc("Nuevo username (enter para dejar igual)");
        if (!username.isBlank()) u.setUsername(username);

        String nombre = leerStrOpc("Nuevo nombre (enter para dejar igual)");
        if (!nombre.isBlank()) u.setNombre(nombre);

        String apellido = leerStrOpc("Nuevo apellido (enter para dejar igual)");
        if (!apellido.isBlank()) u.setApellido(apellido);

        String email = leerStrOpc("Nuevo email (enter para dejar igual)");
        if (!email.isBlank()) u.setEmail(email);

        String activoStr = leerStrOpc("Activo? (S/N, enter para dejar igual)");
        if (!activoStr.isBlank()) u.setActivo(activoStr.equalsIgnoreCase("S"));

        String estStr = leerStrOpc("Estado (ACTIVO/INACTIVO, enter para dejar igual)");
        if (!estStr.isBlank()) u.setEstado(Estado.from(estStr));

        usuarioService.update(u);
        System.out.println("✅ Usuario actualizado.");
    }

    private void bajaLogicaUsuario() throws SQLException {
        long id = leerLong("ID de usuario a dar de baja (lógica)");
        usuarioService.softDeleteById(id);
        System.out.println("🗂️ Usuario marcado como eliminado.");
    }

    private void bajaFisicaUsuario() throws SQLException {
        long id = leerLong("ID de usuario a eliminar (físico)");
        usuarioService.deleteById(id);
        System.out.println("🗑️ Usuario eliminado físicamente.");
    }

    private void crearUsuarioConCredencialTx() throws SQLException {
        // Usuario
        Usuario u = new Usuario();
        u.setEliminado(false);
        u.setUsername(leerStr("Username"));
        u.setNombre(leerStr("Nombre"));
        u.setApellido(leerStr("Apellido"));
        u.setEmail(leerStr("Email"));
        u.setFechaRegistro(LocalDateTime.now());
        u.setActivo(true);
        u.setEstado(leerEstado());

        // Credencial
        CredencialAcceso c = new CredencialAcceso();
        c.setEliminado(false);
        c.setEstado(Estado.ACTIVO);
        c.setHashPassword(leerStr("Password (se guardará el hash SHA-256)"));
        c.setSalt("manual"); // si querés, después agregamos generación de salt
        c.setUltimoCambio(LocalDateTime.now());
        c.setRequiereReset(false);
        // set usuarioId luego de crear usuario -> lo hace el service

        // ligamos la credencial al usuario antes de llamar al service
        u.setCredencial(c);

        Long nuevoId = usuarioService.createUsuarioConCredencial(u);
        System.out.println("✅ Transacción OK. Usuario id=" + nuevoId + " + credencial creada.");
    }

    // ===================== CREDENCIAL =====================

    private void crearCredencialParaUsuario() throws SQLException {
        long usuarioId = leerLong("Usuario ID para asociar credencial");
        CredencialAcceso c = new CredencialAcceso();
        c.setEliminado(false);
        c.setUsuarioId(usuarioId);
        c.setEstado(Estado.ACTIVO);
        c.setUltimaSesion(null);
        c.setHashPassword(leerStr("Password (se guardará el hash SHA-256)"));
        c.setSalt("manual");
        c.setUltimoCambio(LocalDateTime.now());
        c.setRequiereReset(false);

        Long id = credService.create(c);
        System.out.println("✅ Credencial creada con id=" + id);
    }

    private void verCredencialPorId() throws SQLException {
        long id = leerLong("ID de credencial");
        Optional<CredencialAcceso> c = credService.findById(id);
        System.out.println(c.map(Object::toString).orElse("(no encontrada)"));
    }

    private void verCredencialPorUsuarioId() throws SQLException {
        long usuarioId = leerLong("Usuario ID");
        Optional<CredencialAcceso> c = credService.findByUsuarioId(usuarioId);
        System.out.println(c.map(Object::toString).orElse("(no encontrada)"));
    }

   private void actualizarPasswordSeguro() throws SQLException {
    long usuarioId = leerLong("Usuario ID");
    String nuevoPassword = leerStr("Nuevo password (se guardará hash SHA-256)");
    credService.updatePasswordSeguro(usuarioId, nuevoPassword, "IGNORAR");
    System.out.println("🔐 Password actualizada vía stored procedure.");
}

    // ===================== Helpers de entrada =====================

    private String leerStr(String label) {
        System.out.print(label + ": ");
        String s = sc.nextLine();
        while (s == null || s.isBlank()) {
            System.out.print("Requerido. " + label + ": ");
            s = sc.nextLine();
        }
        return s.trim();
    }

    private String leerStrOpc(String label) {
        System.out.print(label + ": ");
        String s = sc.nextLine();
        return s == null ? "" : s.trim();
    }

    private int leerInt(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                String s = sc.nextLine();
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    private long leerLong(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                String s = sc.nextLine();
                return Long.parseLong(s.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido (long).");
            }
        }
    }

    private Estado leerEstado() {
        while (true) {
            String s = leerStr("Estado (ACTIVO/INACTIVO)").toUpperCase();
            if (s.equals("ACTIVO") || s.equals("INACTIVO")) {
                return Estado.valueOf(s);
            }
            System.out.println("Valor inválido. Use ACTIVO o INACTIVO.");
        }
    }
    private void loginUsuario() throws SQLException {
    System.out.println("=== LOGIN DE USUARIO ===");

    String username = leerStr("Username");
    String passwordIngresada = leerStr("Password");

    // 1. Buscar usuario por username
    Optional<Usuario> optUser = usuarioService.findByUsername(username);
    if (optUser.isEmpty()) {
        System.out.println("❌ Usuario no encontrado.");
        return;
    }

    Usuario u = optUser.get();

    // 2. Buscar credencial asociada al usuario
    Optional<CredencialAcceso> optCred = credService.findByUsuarioId(u.getIdUsuario());
    if (optCred.isEmpty()) {
        System.out.println("⚠️ No hay credencial asociada a este usuario.");
        return;
    }

    CredencialAcceso cred = optCred.get();

    // 3. Validar password ingresada contra hash y salt
    boolean ok = com.integradorfinal.programacion2.util.PasswordUtil.validatePassword(
            passwordIngresada,
            cred.getSalt(),
            cred.getHashPassword()
    );

    if (ok) {
        System.out.println("✅ Login exitoso. Bienvenido, " + u.getNombre() + "!");
        cred.setUltimaSesion(LocalDateTime.now());
        credService.update(cred); // actualiza la última sesión
    } else {
        System.out.println("❌ Contraseña incorrecta.");
    }
}

}
