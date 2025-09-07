/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import my.edu.apu.enums.Role;
import my.edu.apu.interfaces.IUser;

/**
 *
 * @author pakdad
 */
import java.util.UUID;

public abstract class AbstractUser implements IUser {

    private final String id;      // immutable, generated once
    private String name;
    private String password;
    private Role role;

    public AbstractUser(String name, String password, Role role) {
        this.id = UUID.randomUUID().toString(); // auto-generate unique ID
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getId() {
        return id;
        
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }
}
