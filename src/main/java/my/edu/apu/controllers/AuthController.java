/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import my.edu.apu.views.LoginFrame;
import my.edu.apu.views.MainFrame;

/**
 *
 * @author pakdad
 */
public class AuthController {

    private final LoginFrame loginFrame;

    public AuthController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        loginFrame.getLoginButton().addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String username = loginFrame.getUniEmail();
        String password = loginFrame.getPassword();

        if (username.equals("Jack") && password.equals("secret")) {
            loginFrame.dispose();
            new MainFrame().setVisible(true);
        }
    }
}
