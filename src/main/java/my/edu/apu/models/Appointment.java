/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import java.time.LocalDate;
import my.edu.apu.enums.AppointmentStatus;
import my.edu.apu.interfaces.IAppointment;

/**
 *
 * @author pakdad
 */
public class Appointment implements IAppointment {

    private String studentId;
    private String supervisorId;
    private LocalDate timeslot;
    private AppointmentStatus status;

    public Appointment(String studentId, String supervisorId, LocalDate timeslot) {
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.timeslot = timeslot;
        this.status = AppointmentStatus.PENDING; // Initially pending when appointment is created
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
    public LocalDate getTimeslot() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AppointmentStatus getStatus() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setTimeslot(LocalDate timeslot) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setStatus(AppointmentStatus status) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
