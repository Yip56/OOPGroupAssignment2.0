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
import my.edu.apu.repositories.StudentRepository;
import my.edu.apu.repositories.UserRepository;

/**
 *
 * @author pakdad
 */
public class App {

    public static void main(String[] args) throws URISyntaxException {
        // Initialize user repository
        Path userFilePath = Paths.get("data", "users.txt");
        Path studentFilePath = Paths.get("data", "students.txt");
        UserRepository userRepo = new UserRepository(userFilePath.toString());
        StudentRepository studentRepo = new StudentRepository(studentFilePath.toString(), userRepo);

        SwingUtilities.invokeLater(() -> {
            // Create login frame and assign its controller
            LoginFrame LoginFrame = new LoginFrame();
            new AuthController(LoginFrame, userRepo, studentRepo);
            LoginFrame.setVisible(true);
        });
    }
}
