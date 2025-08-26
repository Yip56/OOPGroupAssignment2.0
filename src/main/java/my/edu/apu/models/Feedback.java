/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import java.time.LocalDate;
import my.edu.apu.interfaces.IFeedback;

/**
 *
 * @author pakdad
 */
public class Feedback implements IFeedback {

    private String studentId;
    private String supervisorId;
    private String feedback;
    private LocalDate createdAt;

    public Feedback(String studentId, String supervisorId, String feedback) {
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.feedback = feedback;
        this.createdAt = LocalDate.now(); // Set the time at which the class is instantiated
    }

    @Override
    public String getStudentId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getSupervisorId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getFeedback() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LocalDate getCreatedAt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setFeedback(String feedback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
