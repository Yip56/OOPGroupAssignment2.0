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
        return this.studentId;
    }

    @Override
    public String getSupervisorId() {
        return this.supervisorId;
    }

    @Override
    public LocalDate getTimeslot() {
        return this.timeslot;
    }

    @Override
    public AppointmentStatus getStatus() {
        return this.status;
    }

    @Override
    public void setTimeslot(LocalDate timeslot) {
      this.timeslot = timeslot;
    }

    @Override
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

}