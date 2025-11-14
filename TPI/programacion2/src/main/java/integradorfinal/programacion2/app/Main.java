/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package integradorfinal.programacion2.app;

/*
Necesario para que UTF 8 funcione correctamente
 */
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import integradorfinal.programacion2.Programacion2;

/**
 *
 * @author Matias
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Forzar salida estándar a UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        //Testeo de la conexion
        Programacion2.testConnection(); // se ejecuta una sola vez

        // Llamás al menú desde acá
        AppMenu menu = new AppMenu();
        menu.run();
    }
}
