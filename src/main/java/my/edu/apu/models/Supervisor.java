/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import java.time.LocalDate;
import java.util.*;
import my.edu.apu.enums.Role;
import my.edu.apu.interfaces.ISupervisor;

/**
 *
 * @author pakdad
 */
public class Supervisor extends AbstractUser implements ISupervisor {

    private List<String> studentIds;
    private List<LocalDate> timeslots;

    public Supervisor(String name, String uniEmail, String password) {
        super(name, uniEmail, password, Role.SUPERVISOR);
    }

    @Override
    public List<String> getStudentIds() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LocalDate> getAvailableTimeslots() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void addStudentId(String studentId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void removeStudentId(String studentId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void addTimeslot(LocalDate timeslot) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void removeTimeslot(LocalDate timeslot) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
