/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import java.time.LocalDate;
import my.edu.apu.enums.Intake;
import my.edu.apu.enums.Program;

/**
 *
 * @author pakdad
 */
interface IStudent extends IUser {

    // Getters
    LocalDate getDob();

    String getSupervisorId();

    Intake getIntake();

    Program getProgram();

    // Setters
    void setDob(LocalDate dob);

    void setSupervisorId(String supervisorId);

    void setIntake(Intake intake);

    void setProgram(Program program);

}
