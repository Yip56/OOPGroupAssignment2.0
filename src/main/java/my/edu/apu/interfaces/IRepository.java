/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.edu.apu.interfaces;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author pakdad
 * @param <T>
 */
public interface IRepository<T> {

    Optional<T> findById(String id);   // find entity by id

    List<T> findAll();                 // get all entities

    void add(T entity);                // add a new entity

    void remove(String id);            // remove entity by id

    void save();                       // persist data to file
}
