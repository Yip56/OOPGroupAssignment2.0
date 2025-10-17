/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import my.edu.apu.models.FailedLoginAttempt;
import my.edu.apu.repositories.FailedLoginAttemptRepository;

/**
 *
 * @author pakdad
 */
public class FailedLoginService {

    public static List<String> getDistinctEmails(FailedLoginAttemptRepository loginAttemptRepo) {
        // Load attempts and initialize variables
        List<FailedLoginAttempt> attempts = loginAttemptRepo.findAll();
        Set<String> emails = new HashSet<>();

        // Add emails to set
        for (FailedLoginAttempt attempt : attempts) {
            emails.add(attempt.getUniEmail());
        }

        // Return
        return new ArrayList<>(emails);
    }

    public static int getTotalLoginFailsByEmail(FailedLoginAttemptRepository loginAttemptRepo, String email) {
        // Get attempts by email
        List<FailedLoginAttempt> attempts = loginAttemptRepo.findByEmail(email);
        return attempts.size();
    }

    public static LocalDateTime getLastLoginAttemptDate(FailedLoginAttemptRepository loginAttemptRepo, String email) {
        // Load attempts and initialize variables
        List<FailedLoginAttempt> attempts = loginAttemptRepo.findByEmail(email);
        LocalDateTime latestTimestamp = LocalDateTime.MIN;

        // Loop through attempts
        for (FailedLoginAttempt attempt : attempts) {
            // Update latest timestamp if needed
            LocalDateTime currentTimestamp = attempt.getTimestamp();
            if (currentTimestamp.isAfter(latestTimestamp)) {
                latestTimestamp = currentTimestamp;
            }
        }

        // Return
        return latestTimestamp;
    }
}
