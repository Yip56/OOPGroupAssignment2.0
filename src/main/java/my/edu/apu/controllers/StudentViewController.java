/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;
import my.edu.apu.views.panels.StudentView;
import my.edu.apu.repositories.StudentRepository;
import my.edu.apu.models.Student;

/**
 *
 * @author pakdad
 */
public class StudentViewController {
    private final String studentId;
    private final StudentView studentView;
    private final StudentRepository studentRepo;
    
    public StudentViewController(StudentView studentView, StudentRepository studentRepo, String studentId) {
        this.studentId = studentId;
        this.studentView = studentView;
        this.studentRepo = studentRepo;
        
        intializeStudentView();
    }
    
    private void intializeStudentView() {
        // Find the student from the student repository
        Student student = studentRepo.findById(studentId).get();
        String name = student.getName();
        
        // Set the student's username in the studentView
        studentView.getTxtUsername().setText(name);
        studentView.getFieldSupervisor().setText("Supervisor Name");
    }
}
