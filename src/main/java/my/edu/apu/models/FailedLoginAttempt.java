/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import java.time.LocalDateTime;
import java.util.UUID;
import my.edu.apu.interfaces.IFailedLoginAttempt;

/**
 *
 * @author pakdad
 */
public class FailedLoginAttempt implements IFailedLoginAttempt {

    private final String id;
    private String uniEmail;
    private String reason;
    private LocalDateTime timestamp;

    // Constructor for loading failed login attempts from file
    public FailedLoginAttempt(String uniEmail, String reason, LocalDateTime timestamp, String id) {
        this.id = id;
        this.uniEmail = uniEmail;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    // Constructor to create new failed login attempts
    public FailedLoginAttempt(String uniEmail, String reason) {
        this.id = UUID.randomUUID().toString();
        this.uniEmail = uniEmail;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getUniEmail() {
        return this.uniEmail;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setUniEmail(String uniEmail) {
        this.uniEmail = uniEmail;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
