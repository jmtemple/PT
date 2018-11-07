package com.example.proyectotiti.models;

/** Date_Class class
 * This class contains the date for each visit.
 */

public class Date_Class {

    public String month;
    public String day;
    public String year;

    public Date_Class() {
        // Default constructor required for calls to DataSnapshot.getValue(Date_Class.class)
    }

    public Date_Class(String month, String day, String year) {
        this.month = month;
        this.day = day;
        this.year = year;

    }
}
