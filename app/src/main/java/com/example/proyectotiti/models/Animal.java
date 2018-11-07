package com.example.proyectotiti.models;

import java.util.Map;

/** Animal class
 * This class contains two different objects "wild" and "domestic" that contain maps so each animal
 * has an ID (String) and a description (AnimalDesc).
 * committed (Boolean): reflects whether the family has committed to this field.
 * compliant (Boolean): reflects whether the family is compliant in this field.
 */

public class Animal {

    public Map<String, AnimalDesc> wild;
    public Map<String, DomesticAnimalDesc> domestic;
    public boolean committed;
    public boolean compliant;


    public Animal() {
        // Default constructor required for calls to DataSnapshot.getValue(Animal.class)
    }

    public Animal(Map<String, AnimalDesc> wild, Map<String, DomesticAnimalDesc> domestic, boolean committed, boolean compliant) {
        this.wild = wild;
        this.domestic = domestic;
        this.committed = committed;
        this.compliant = compliant;
    }
}
