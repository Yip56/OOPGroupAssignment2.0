package my.edu.apu.utils;

import javax.swing.*;
import my.edu.apu.controllers.AuthController;
import my.edu.apu.repositories.*;
import my.edu.apu.views.LoginFrame;

public class AppNavigator {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;

    public AppNavigator(UserRepository userRepo, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;
    }

    public void displayLogin() {
        SwingUtilities.invokeLater(() -> {
            // Create login frame and assign its controller
            LoginFrame LoginFrame = new LoginFrame();
            AuthController authController = new AuthController(LoginFrame, this, userRepo, studentRepo, supervisorRepo, appointmentRepo, feedbackRepo);
            authController.displayLoginFrame();
        });
    }

    // Switch from current frame to login
    public void switchToLogin(JFrame currentFrame) {
        // Close current frame
        currentFrame.dispose();

        // Display login
        displayLogin();
    }
}
