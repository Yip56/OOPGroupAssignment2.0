/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package my.edu.apu.enums;

/**
 *
 * @author pakdad
 */
public enum Role {
    STUDENT("Student"),
    SUPERVISOR("Supervisor"),
    FACULTY_ADMIN("Faculty Admin"),
    SYSTEM_ADMIN("System Admin");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
