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
import my.edu.apu.repositories.*;
import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author pakdad
 */
public class App {

    public static void main(String[] args) throws URISyntaxException {
        // Initialize repository filepaths
        Path userFilePath = Paths.get("data", "users.txt");
        Path studentFilePath = Paths.get("data", "students.txt");
        Path supervisorFilePath = Paths.get("data", "supervisors.txt");
        Path appointmentFilePath = Paths.get("data", "appointments.txt");

        // Initialize repositories
        UserRepository userRepo = new UserRepository(userFilePath.toString());
        StudentRepository studentRepo = new StudentRepository(studentFilePath.toString(), userRepo);
        SupervisorRepository supervisorRepo = new SupervisorRepository(supervisorFilePath.toString(), userRepo);
        AppointmentRepository appointmentRepo = new AppointmentRepository(appointmentFilePath.toString());
        
        // Set up LAF
        FlatLightLaf.setup();
        
        // Create login frame and auth controller
        SwingUtilities.invokeLater(() -> {
            // Create login frame and assign its controller
            LoginFrame LoginFrame = new LoginFrame();
            new AuthController(LoginFrame, userRepo, studentRepo, supervisorRepo, appointmentRepo);
            LoginFrame.setVisible(true);
        });
    }
}
