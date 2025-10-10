/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import my.edu.apu.enums.Role;
import my.edu.apu.models.User;
import my.edu.apu.repositories.*;
import my.edu.apu.views.LoginFrame;
import my.edu.apu.views.MainFrame;
import my.edu.apu.views.panels.StudentView;
import my.edu.apu.views.panels.SupervisorView;
import my.edu.apu.controllers.StudentViewController;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;
import my.edu.apu.utils.AppNavigator;
import my.edu.apu.views.panels.FacultyAdminView;
import my.edu.apu.views.panels.SystemAdminView;

/**
 *
 * @author pakdad
 */
public class AuthController {

    private final LoginFrame loginFrame;
    private final AppNavigator navigator;
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;

    public AuthController(LoginFrame loginFrame, AppNavigator navigator, UserRepository userRepo, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo) {
        this.loginFrame = loginFrame;
        this.navigator = navigator;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;

        // Authenticate the user when the login button is pressed
        loginFrame.getLoginButton().addActionListener(e -> authenticate());

        // Set the loginButton as the default button
        JRootPane root = loginFrame.getRootPane();
        root.setDefaultButton(loginFrame.getLoginButton());
    }

    public void displayLoginFrame() {
        // Display login frame
        loginFrame.setVisible(true);
    }

    private void authenticate() {
        String uniEmail = loginFrame.getUniEmail();
        String password = loginFrame.getPassword();

        Optional<User> userOpt = userRepo.findByUniEmail(uniEmail);
        userOpt.ifPresentOrElse(
                (user) -> handleAuthentication(user, password),
                () -> displayErrorDialog("Your email could not be found, please try again.")
        );
    }

    private void handleAuthentication(User user, String password) {
        // Ensure password is correct
        if (!user.getPassword().equals(password)) {
            displayErrorDialog("Your password is incorrect, please try again.");
        } // Ensure student has supervisor assigned (if user is a student)
        else if (user.getRole().equals(Role.STUDENT) && !isSupervisorAssigned(user)) {
            displayErrorDialog("You have not been assigned a supervisor yet. Please try again later.");
        } // Display the mainframe if authenticated
        else {
            displayMainFrame(user);
        }
    }

    private boolean isSupervisorAssigned(User user) {
        // Ensure student has a supervisor
        Student student = studentRepo.findById(user.getId()).get();
        Optional<Supervisor> supervisorProfile = supervisorRepo.findById(student.getSupervisorId());
        return !supervisorProfile.isEmpty();
    }

    private void displayMainFrame(User user) {
        SwingUtilities.invokeLater(() -> {
            loginFrame.dispose();
            MainFrame mainFrame = new MainFrame();
            switch (user.getRole()) {
                case Role.STUDENT -> {
                    StudentView studentView = new StudentView();
                    new StudentViewController(studentView, navigator, studentRepo, supervisorRepo, appointmentRepo, feedbackRepo, user.getId());
                    mainFrame.setContentPane(studentView);
                }
                case Role.SUPERVISOR -> {
                    SupervisorView supervisorView = new SupervisorView();
                    new SupervisorViewController(supervisorView, navigator, studentRepo, supervisorRepo, appointmentRepo, feedbackRepo, user.getId());
                    mainFrame.setContentPane(supervisorView);
                }
                case Role.FACULTY_ADMIN -> {
                    FacultyAdminView facultyAdminView = new FacultyAdminView();
                    new FacultyAdminController(facultyAdminView, navigator, userRepo, studentRepo, supervisorRepo, appointmentRepo, feedbackRepo, user.getId());
                    mainFrame.setContentPane(facultyAdminView);
                }
                case Role.SYSTEM_ADMIN -> {
                    SystemAdminView systemAdminView = new SystemAdminView();
                    new SystemAdminController(systemAdminView, navigator, userRepo, studentRepo, supervisorRepo, appointmentRepo, feedbackRepo, user.getId());
                    mainFrame.setContentPane(systemAdminView);
                }
            }

            mainFrame.setLocationRelativeTo(null); // Center on screen
            mainFrame.setVisible(true);
        });
    }

    private void displayErrorDialog(String msg) {
        JOptionPane.showMessageDialog(loginFrame, msg, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
