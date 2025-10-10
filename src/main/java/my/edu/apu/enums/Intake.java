package my.edu.apu.enums;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

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

    public static Intake fromDisplayName(String displayName) {
        for (Intake i : Intake.values()) {
            if (i.displayName.equalsIgnoreCase(displayName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
