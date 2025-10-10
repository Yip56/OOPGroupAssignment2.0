package my.edu.apu.enums;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

/**
 *
 * @author pakdad
 */
public enum Program {
    COMPUTER_SCIENCE("Computer Science"),
    DATA_SCIENCE("Data Science"),
    SOFTWARE_ENGINEERING("Software Engineering"),
    INFORMATION_TECHNOLOGY("Information Technology");

    private final String displayName;

    Program(String displayName) {
        this.displayName = displayName;
    }

    public static Program fromDisplayName(String displayName) {
        for (Program p : Program.values()) {
            if (p.displayName.equalsIgnoreCase(displayName)) {
                return p;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
