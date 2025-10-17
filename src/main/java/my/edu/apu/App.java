/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu;

import java.nio.file.*;
import java.net.URISyntaxException;
import my.edu.apu.repositories.*;
import com.formdev.flatlaf.FlatLightLaf;
import my.edu.apu.utils.AppNavigator;

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
        Path feedbackFilePath = Paths.get("data", "feedbacks.txt");
        Path failedLoginAttemptsPath = Paths.get("data", "failed-login-attempts.txt");

        // Initialize repositories
        UserRepository userRepo = new UserRepository(userFilePath.toString());
        StudentRepository studentRepo = new StudentRepository(studentFilePath.toString(), userRepo);
        SupervisorRepository supervisorRepo = new SupervisorRepository(supervisorFilePath.toString(), userRepo);
        AppointmentRepository appointmentRepo = new AppointmentRepository(appointmentFilePath.toString());
        FeedbackRepository feedbackRepo = new FeedbackRepository(feedbackFilePath.toString());
        FailedLoginAttemptRepository loginAttemptRepo = new FailedLoginAttemptRepository(failedLoginAttemptsPath.toString());

        // Set up LAF
        FlatLightLaf.setup();

        // Display login frame
        AppNavigator navigator = new AppNavigator(userRepo, studentRepo, supervisorRepo, appointmentRepo, feedbackRepo, loginAttemptRepo);
        navigator.displayLogin();
    }
}
