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

    private List<LocalDate> timeslots;

    public Supervisor(String name, String uniEmail, String password) {
        super(name, uniEmail, password, Role.SUPERVISOR);
    }

    @Override
    public List<LocalDate> getAvailableTimeslots() {
        return this.timeslots;
    }

    @Override
    public void addTimeslot(LocalDate timeslot) {
        this.timeslots.add(timeslot);
    }

    @Override
    public void removeTimeslot(LocalDate timeslot) {
        this.timeslots.remove(timeslot);
    }

}
