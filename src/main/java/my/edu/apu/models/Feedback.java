/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import java.time.LocalDate;
import java.util.UUID;
import my.edu.apu.interfaces.IFeedback;

/**
 *
 * @author pakdad
 */
public class Feedback implements IFeedback {

    private String feedbackId;
    private String studentId;
    private String supervisorId;
    private String feedback;
    private LocalDate createdAt;

    public Feedback(String studentId, String supervisorId, String feedback) {
        this.feedbackId = UUID.randomUUID().toString(); // auto-generate unique ID
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.feedback = feedback;
        this.createdAt = LocalDate.now(); // Set the time at which the class is instantiated
    }

    public Feedback(String feedbackId, String studentId, String supervisorId, String feedback) {
        this.feedbackId = feedbackId; // Allow custom restoration of id from file
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.feedback = feedback;
        this.createdAt = LocalDate.now();
    }

    @Override
    public String getId() {
        return this.feedbackId;
    }

    @Override
    public String getStudentId() {
        return this.studentId;
    }

    @Override
    public String getSupervisorId() {
        return this.supervisorId;
    }

    @Override
    public String getFeedback() {
        return this.feedback;
    }

    @Override
    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

}
