package my.edu.apu.models;

import java.time.LocalDate;
import java.util.UUID;
import my.edu.apu.interfaces.IFeedback;

public class Feedback implements IFeedback {

    private final String feedbackId;
    private final String studentId;
    private final String supervisorId;
    private String feedback;
    private final LocalDate createdAt;

    // Normal constructor when creating new feedback
    public Feedback(String studentId, String supervisorId, String feedback) {
        this.feedbackId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.feedback = feedback;
        this.createdAt = LocalDate.now();
    }

    // Constructor for restoring from file
    public Feedback(String feedbackId, String studentId, String supervisorId, String feedback, LocalDate createdAt) {
        this.feedbackId = feedbackId;
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.feedback = feedback;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return feedbackId;
    }

    @Override
    public String getStudentId() {
        return studentId;
    }

    @Override
    public String getSupervisorId() {
        return supervisorId;
    }

    @Override
    public String getFeedback() {
        return feedback;
    }

    @Override
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
