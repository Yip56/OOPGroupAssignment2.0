/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import my.edu.apu.enums.Role;

/**
 *
 * @author pakdad
 */
public class User extends AbstractUser {

    public User(String name, String password, Role role) {
        super(name, password, role);
    }

}
