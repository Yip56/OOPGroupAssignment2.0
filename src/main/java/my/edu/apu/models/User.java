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

    public User(String name, String uniEmail, String password, Role role) {
        super(name, uniEmail, password, role);
    }

    public User(String name, String uniEmail, String password, Role role, String id) {
        super(name, uniEmail, password, role, id);
    }

}
