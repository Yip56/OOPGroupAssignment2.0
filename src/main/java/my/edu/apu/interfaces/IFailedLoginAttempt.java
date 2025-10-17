/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import java.time.LocalDate;

/**
 *
 * @author pakdad
 */
public interface IFailedLoginAttempt {

    // Getters
    String getId();

    String getUniEmail();

    String getReason();

    LocalDate getTimestamp();

    // Setters
    void setUniEmail(String uniEmail);

    void setReason(String reason);

    void setTimestamp(LocalDate timestamp);
}
