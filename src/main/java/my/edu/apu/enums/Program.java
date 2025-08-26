/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package my.edu.apu.enums;

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

    @Override
    public String toString() {
        return displayName;
    }
}

