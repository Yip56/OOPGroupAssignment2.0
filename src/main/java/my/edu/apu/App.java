/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu;

import javax.swing.SwingUtilities;
import my.edu.apu.views.LoginFrame;
import my.edu.apu.controllers.AuthController;

/**
 *
 * @author pakdad
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello world");

        SwingUtilities.invokeLater(() -> {

            LoginFrame LoginFrame = new LoginFrame();
            new AuthController(LoginFrame);
            LoginFrame.setVisible(true);
        });
    }
}
