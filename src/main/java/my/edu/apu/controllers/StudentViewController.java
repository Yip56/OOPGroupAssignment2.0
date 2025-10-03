/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.controllers;
import my.edu.apu.views.panels.StudentView;
import my.edu.apu.repositories.StudentRepository;
import my.edu.apu.repositories.SupervisorRepository;
import my.edu.apu.models.Student;
import my.edu.apu.models.Supervisor;

/**
 *
 * @author pakdad
 */
public class StudentViewController {
    private final String studentId;
    private final StudentView studentView;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    
    public StudentViewController(StudentView studentView, StudentRepository studentRepo, SupervisorRepository supervisorRepo, String studentId) {
        this.studentId = studentId;
        this.studentView = studentView;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        
        intializeStudentView();
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
}
