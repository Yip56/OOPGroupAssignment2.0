/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package my.edu.apu.enums;

/**
 *
 * @author pakdad
 */
public enum AppointmentStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");
    
    private final String displayName;
    
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
