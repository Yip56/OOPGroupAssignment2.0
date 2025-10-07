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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import my.edu.apu.models.Appointment;
import my.edu.apu.models.Feedback;
import my.edu.apu.utils.AppNavigator;
import my.edu.apu.views.panels.SupervisorView;


/**
 *
 * @author CS Yip
 */
public class SupervisorViewController {
    private final String studentId;
    private final String supervisorId;
    private final SupervisorView supervisorView;
    private final AppNavigator navigator;
    private final StudentRepository studentRepo;
    private final SupervisorRepository supervisorRepo;
    private final AppointmentRepository appointmentRepo;
    private final FeedbackRepository feedbackRepo;
    private DefaultTableModel appointmentModel;
    private DefaultTableModel timeslotModel;
    private DefaultTableModel feedbackModel;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    private int selectedAppointmentRow = -1;
    private int selectedTimeslotRow = -1;
    
    public SupervisorViewController(SupervisorView supervisorView, AppNavigator navigator, StudentRepository studentRepo, SupervisorRepository supervisorRepo, AppointmentRepository appointmentRepo, FeedbackRepository feedbackRepo, String studentId) {
        this.studentId = studentId;
        this.supervisorView = supervisorView;
        this.navigator = navigator;
        this.studentRepo = studentRepo;
        this.supervisorRepo = supervisorRepo;
        this.appointmentRepo = appointmentRepo;
        this.feedbackRepo = feedbackRepo;
        this.supervisorId = studentRepo.findById(studentId).get().getSupervisorId();

        intializeSupervisorView();
    }

    private void intializeSupervisorView() {
        loadAppointments();
    }
    
    private void loadAppointments(){
        String[] appointmentWidgetColumns = {"Student", "Date"};
        DefaultTableModel appointmentWidgetModel = new DefaultTableModel(appointmentWidgetColumns, 0);
        
        String[] appointmentColumns = {"ID", "Student Name", "Date", "Time", "Status"};
        this.appointmentModel = new DefaultTableModel(appointmentColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        List<Appointment> appointments = appointmentRepo.findBySupervisorId(supervisorId);
        
        appointmentWidgetModel.setRowCount(0);
        appointmentModel.setRowCount(0);
        
        for (Appointment appt : appointments){
            String studentName = studentRepo.findById(appt.getStudentId())
                    .map(stu -> stu.getName())
                    .orElse("Unknown Student");
            
            String date = appt.getTimeslot().format(dateFormatter);
            String time = appt.getTimeslot().format(timeFormatter);
            String status = appt.getStatus().toString();
            
            appointmentWidgetModel.addRow(new Object[]{studentName, date});
            appointmentModel.addRow(new Object[]{appt.getId(), studentName, date, time, status});
            
            JTable appointmentWidgetTable = supervisorView.getTblAppointmentsWidget();
            appointmentWidgetTable.setModel(appointmentWidgetModel);

            JTable appointmentTable = supervisorView.getTblAppointments();
            appointmentTable.setModel(appointmentModel);

        // hide the first column (ID)
            TableColumn idColumn = supervisorView.getTblAppointments().getColumnModel().getColumn(0);
            supervisorView.getTblAppointments().removeColumn(idColumn);
        }
        
        
    }
    
    
}
