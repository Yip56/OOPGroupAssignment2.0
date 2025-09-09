/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.edu.apu.repositories;

import java.util.List;
import java.util.Optional;
import my.edu.apu.interfaces.Repository;
import my.edu.apu.models.User;
import my.edu.apu.enums.Role;

/**
 *
 * @author pakdad
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserRepository implements Repository<User> {

    private final List<User> users = new ArrayList<>();
    private final Path filePath;

    public UserRepository(String fileName) {
        this.filePath = Paths.get(fileName); // Create a path object
        load(); // load users from file at startup
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users); // return a copy to prevent outside modification
    }

    @Override
    public Optional<User> findById(String id) {
        // loop through users
        for (User user : users) {
            // return an optional with a user in it if a match is found
            if (user.getId().equals(id)) {
                return Optional.of(user);
            }
        }
        // return empty optional if no match is found
        return Optional.empty();
    }

    public Optional<User> findByName(String name) {
        // Repeat process in findById method, but using the user's name
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(User user) {
        users.add(user); // Add a user to the list
        save(); // Save the list to the file
    }

    @Override
    public void remove(String id) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getId().equals(id)) {
                users.remove(i);
                break; // stop after removing one match
            }
        }
        save(); // persist changes to file
    }

    @Override
    public void save() {

        // Create a bufferedWriter
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            System.out.println("xxxx");
            // Loop through user list
            for (User user : users) {
                System.out.println(user.getId() + "|"
                        + user.getName() + "|"
                        + user.getUniEmail() + "|"
                        + user.getPassword() + "|"
                        + user.getRole().name());
                // Write in "id|name|password|role" format
                writer.write(user.getId() + "|"
                        + user.getName() + "|"
                        + user.getUniEmail() + "|"
                        + user.getPassword() + "|"
                        + user.getRole().name());
                writer.newLine(); // go to next line
            }
        } catch (IOException e) {
            // handle errors
            throw new RuntimeException("Failed to save users", e);
        }
    }

    private void load() {
        // ensure the file path exists
        if (!Files.exists(filePath)) {
            return;
        }

        // create a bufferedReader
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            // Clear old user list
            users.clear();
            String line;

            // Get each line from file
            while ((line = reader.readLine()) != null) {
                // Split lines based on the separator
                String[] parts = line.split("\\|");

                // Get properties from parts
                String id = parts[0];
                String name = parts[1];
                String uniEmail = parts[2];
                String password = parts[3];
                Role role = Role.valueOf(parts[4]);

                // restore user with given ID and update list
                User user = new User(name, uniEmail, password, role, id);
                users.add(user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users", e);
        }
    }
}
