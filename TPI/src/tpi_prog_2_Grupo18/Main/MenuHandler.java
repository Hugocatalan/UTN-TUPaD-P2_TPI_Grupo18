/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Matias
 */
package tpi_prog_2_Grupo18.Main;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import tpi_prog_2_Grupo18.Models.Usuario;
import tpi_prog_2_Grupo18.Models.Credencial;
import tpi_prog_2_Grupo18.service.UsuarioServiceImpl;
import tpi_prog_2_Grupo18.Service.CredencialServiceImpl;
import tpi_prog_2_Grupo18.Config.DatabaseConnection;

public class MenuHandler {

    private final UsuarioServiceImpl usuarioService;
    private final CredencialServiceImpl credencialService;
    private final Scanner scanner;

    public MenuHandler(Connection connection) {
        this.usuarioService = new UsuarioServiceImpl(connection);
        this.credencialService = new CredencialServiceImpl(connection);
        this.scanner = new Scanner(System.in);
    }

    public void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> crearUsuario();
            case 2 -> mostrarUsuarios();
            case 3 -> actualizarUsuario();
            case 4 -> eliminarUsuario();
            case 5 -> crearCredencial();
            case 6 -> mostrarCredenciales();
            case 7 -> actualizarCredencial();
            case 10 -> mostrarUsuarioConCredencial();
            case 0 -> System.out.println("Saliendo...");
            default -> System.out.println("Opción inválida, intente de nuevo.");
        }
    }

private void crearUsuario() {
    try {
        System.out.print("Ingrese nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese apellido: ");
        String apellido = scanner.nextLine();

        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);

        usuarioService.insertarUsuario(nuevo, password, "ACTIVO");
        System.out.println("Usuario creado correctamente.");
    } catch (Exception e) {
        System.err.println("Error al crear usuario: " + e.getMessage());
    }
}


    private void mostrarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.getAll();
            System.out.println("--- LISTA DE USUARIOS ---");
            for (Usuario u : usuarios) {
                System.out.println("ID: " + u.getId() + " | Nombre: " + u.getNombre());
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar usuarios: " + e.getMessage());
        }
    }

    private void actualizarUsuario() {
        System.out.println(">> Actualizar usuario (pendiente de implementación)");
    }

    private void eliminarUsuario() {
        System.out.println(">> Eliminar usuario (pendiente de implementación)");
    }

    private void crearCredencial() {
        try {
            System.out.print("Ingrese ID de usuario: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Ingrese contraseña: ");
            String password = scanner.nextLine();

            Usuario usuario = usuarioService.getUsuarioById(id);
            if (usuario != null) {
                credencialService.insertarCredencial(usuario, password, "ACTIVO");
                System.out.println("Credencial creada correctamente.");
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (Exception e) {
            System.err.println("Error al crear credencial: " + e.getMessage());
        }
    }

    private void mostrarCredenciales() {
        try {
            List<Credencial> lista = credencialService.getAll();
            System.out.println("--- ESTADO DE CREDENCIALES ---");
            for (Credencial c : lista) {
                System.out.println("ID: " + c.getId() + " | Estado: " + c.getEstado());
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar credenciales: " + e.getMessage());
        }
    }

    private void actualizarCredencial() {
        System.out.println(">> Actualizar credencial (pendiente de implementación)");
    }

    private void mostrarUsuarioConCredencial() {
        System.out.println(">> Mostrar usuario con credencial (pendiente de implementación)");
    }

    // Método main para probar el menú con handler
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            MenuHandler handler = new MenuHandler(conn);
            Scanner sc = new Scanner(System.in);
            boolean running = true;

            while (running) {
                MenuPantalla.mostrarMenuPrincipal();
                int opcion = sc.nextInt();
                sc.nextLine();
                handler.ejecutarOpcion(opcion);
                if (opcion == 0) running = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}