/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu;

import javax.swing.SwingUtilities;
import java.nio.file.*;
import java.net.URISyntaxException;
import my.edu.apu.views.LoginFrame;
import my.edu.apu.controllers.AuthController;
import my.edu.apu.repositories.UserRepository;

/**
 *
 * @author pakdad
 */
public class App {

    public static void main(String[] args) throws URISyntaxException {
        // Initialize user repository
        Path userFilePath = Paths.get("data", "users.txt");
        UserRepository userRepo = new UserRepository(userFilePath.toString());

        SwingUtilities.invokeLater(() -> {
            // Create login frame and assign its controller
            LoginFrame LoginFrame = new LoginFrame();
            new AuthController(LoginFrame, userRepo);
            LoginFrame.setVisible(true);
        });
    }
}
