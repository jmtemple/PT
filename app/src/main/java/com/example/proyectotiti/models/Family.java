package com.example.proyectotiti.models;

import java.util.Map;

/** Family class
 * Holds the information for the family.
 * id (String): the unique number given to the family
 * visits (Map<String, Visit>): String is the visit number corresponding to a specific visit
 * name (String): the name of the family
 */

public class Family {

    public Map<String, Visit> visits;
    public String name;
    public String id;

    public Family() {
        // Default constructor required for calls to DataSnapshot.getValue(Family.class)
    }

    public Family(Map<String, Visit> visits, String name, String id) {
        this.visits = visits;
        this.name = name;
        this.id = id;
    }

}
