/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import my.edu.apu.enums.Role;

/**
 *
 * @author pakdad
 */
public interface IUser {

    // Getters
    String getId();

    String getName();
    
    String getUniEmail();

    String getPassword();

    Role getRole();

    // Setters
    void setName(String name);

    void setPassword(String password);

    void setRole(Role role);
}
