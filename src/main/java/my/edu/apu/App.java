/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu;

import javax.swing.SwingUtilities;
import java.net.URL;
import java.nio.file.*;
import java.io.*;
import java.net.URISyntaxException;
import my.edu.apu.views.LoginFrame;
import my.edu.apu.controllers.AuthController;
import my.edu.apu.enums.Role;
import my.edu.apu.models.User;
import my.edu.apu.repositories.UserRepository;

/**
 *
 * @author pakdad
 */
public class App {

    public static void main(String[] args) throws URISyntaxException {
        Path userFilePath = Paths.get("data", "users.txt");
        UserRepository repo = new UserRepository(userFilePath.toString());
        
        SwingUtilities.invokeLater(() -> {

            LoginFrame LoginFrame = new LoginFrame();
            new AuthController(LoginFrame);
            LoginFrame.setVisible(true);
        });
    }
}
