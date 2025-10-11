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
    private String uniEmail;
    private String password;
    private Role role;

    public AbstractUser(String name, String uniEmail, String password, Role role) {
        this.id = UUID.randomUUID().toString(); // auto-generate unique ID
        this.name = name;
        this.uniEmail = uniEmail;
        this.password = password;
        this.role = role;
    }

    public AbstractUser(String name, String uniEmail, String password, Role role, String id) {
        this.id = id; // Allow ids to be set for restoration from file
        this.name = name;
        this.uniEmail = uniEmail; // also unique, used for identification during login
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
    public String getUniEmail() {
        return this.uniEmail;
    }

    @Override
    public void setUniEmail(String uniEmail) {
        this.uniEmail = uniEmail;
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
