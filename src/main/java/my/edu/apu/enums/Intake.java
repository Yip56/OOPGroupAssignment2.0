/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package my.edu.apu.enums;

/**
 *
 * @author pakdad
 */
public enum Intake {
    JANUARY("January"),
    MAY("May"),
    SEPTEMBER("September");

    private final String displayName;

    Intake(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
