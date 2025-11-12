/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tpi_prog_2_Grupo18.service;
import tpi_prog_2_Grupo18.Util.PasswordUtil;

import java.sql.*;

public class CredencialServiceImpl {
    private Connection connection;

    public CredencialServiceImpl(Connection connection) {
        this.connection = connection;
    }

    public boolean validarCredencial(int usuarioId, String password) {
        try {
            String sql = "SELECT hash_password, salt FROM credenciales WHERE usuario_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashGuardado = rs.getString("hash_password");
                String salt = rs.getString("salt");

                String hashIngresado = PasswordUtil.hashPassword(password, salt);

                return hashGuardado.equals(hashIngresado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
