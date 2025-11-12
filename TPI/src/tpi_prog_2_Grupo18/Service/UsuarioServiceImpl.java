/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tpi_prog_2_Grupo18.service;

import java.sql.Connection;
import java.util.List;
import tpi_prog_2_Grupo18.Models.Usuario;
import tpi_prog_2_Grupo18.Dao.UsuarioDAO;

public class UsuarioServiceImpl {

    private final UsuarioDAO usuarioDAO;

    public UsuarioServiceImpl(Connection connection) {
        // El DAO usa DatabaseConnection internamente, así que no necesita el connection
        this.usuarioDAO = new UsuarioDAO();
    }

    // Crear usuario (adaptando al DAO)
    public void insertarUsuario(Usuario usuario, String password, String estado) throws Exception {
        usuario.setEstado(estado);
        usuario.setActivo(true);
        usuarioDAO.insertar(usuario);
        // acá podrías también crear la credencial asociada con CredencialServiceImpl
    }

    // Listar todos
    public List<Usuario> getAll() throws Exception {
        return usuarioDAO.getAll();
    }

    // Buscar por ID
    public Usuario getUsuarioById(int id) throws Exception {
        return usuarioDAO.getById(id);
    }

    // Actualizar
    public void actualizarUsuario(Usuario usuario) throws Exception {
        usuarioDAO.actualizar(usuario);
    }

    // Eliminar lógico
    public void eliminarUsuario(int id) throws Exception {
        usuarioDAO.eliminar(id);
    }

    // Buscar por username
    public List<Usuario> buscarPorUsername(String filtro) throws Exception {
        return usuarioDAO.buscarPorUsername(filtro);
    }

    // Buscar por email
    public Usuario buscarPorEmail(String email) throws Exception {
        return usuarioDAO.buscarPorEmail(email);
    }
}