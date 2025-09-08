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
public interface IFeedback {

    // Getters
    String getId();
    
    String getStudentId();

    String getSupervisorId();

    String getFeedback();

    LocalDate getCreatedAt();

    // Setters
    void setFeedback(String feedback);
}
