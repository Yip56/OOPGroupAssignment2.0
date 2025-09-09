/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import my.edu.apu.models.User;
import my.edu.apu.repositories.UserRepository;
import my.edu.apu.views.LoginFrame;
import my.edu.apu.views.MainFrame;

/**
 *
 * @author pakdad
 */
public class AuthController {

    private final LoginFrame loginFrame;
    private final UserRepository userRepo;

    public AuthController(LoginFrame loginFrame, UserRepository userRepo) {
        this.loginFrame = loginFrame;
        this.userRepo = userRepo;

        // Authenticate the user when the login button is pressed
        loginFrame.getLoginButton().addActionListener(e -> authenticate());
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
        if (user.getPassword().equals(password)) {
            displayMainFrame(user);
        } else {
            displayErrorDialog("Your password is incorrect, please try again.");
        }
    }

    private void displayMainFrame(User user) {
        SwingUtilities.invokeLater(() -> {
            loginFrame.dispose();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    private void displayErrorDialog(String msg) {
        JOptionPane.showMessageDialog(loginFrame, msg, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
