/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.models;

import java.time.LocalDate;
import my.edu.apu.enums.Intake;
import my.edu.apu.enums.Program;
import my.edu.apu.interfaces.IStudent;
import my.edu.apu.enums.*;

/**
 *
 * @author pakdad
 */
public class Student extends AbstractUser implements IStudent {

    private LocalDate dob;
    private String supervisorId;
    private Intake intake;
    private Program program;

    public Student(String name, String uniEmail, String password) {
        super(name, uniEmail, password, Role.STUDENT);
    }

    @Override
    public LocalDate getDob() {
        return this.dob;
    }

    @Override
    public String getSupervisorId() {
        return this.supervisorId;
    }

    @Override
    public Intake getIntake() {
        return this.intake;
    }

    @Override
    public Program getProgram() {
        return this.program;
    }

    @Override
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    @Override
    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    @Override
    public void setIntake(Intake intake) {
        this.intake = intake;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

}
