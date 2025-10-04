/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;

import my.edu.apu.views.panels.StudentView;
import my.edu.apu.repositories.*;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingUtilities;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author pakdad
 */
public class StudentViewController {

    private final String studentId;
    private final StudentView studentView;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;

    public StudentViewController(StudentView studentView, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, String studentId) {
        this.studentId = studentId;
        this.studentView = studentView;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;

        intializeStudentView();
        initializeTimers();
    }

    private void intializeStudentView() {
        // Find the student from the student repository
        Student student = studentRepo.findById(studentId).get();
        String name = student.getName();

        // Set the student's username in the studentView
        studentView.getTxtUsername().setText(name);

        // Find the student's supervisor
        Supervisor supervisor = supervisorRepo.findById(student.getSupervisorId()).get();
        studentView.getFieldSupervisor().setText(supervisor.getName());
    }

    private void initializeTimers() {
        // Set the date once (doesn't need live updating)
        studentView.getTxtDate().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, yyyy")));

        // Set the time initially
        studentView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));

        // Update the time every second
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    studentView.getTxtTime().setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
                });
            }
        }, 0, 1000);

    }
}
