package com.example.proyectotiti.models;

import java.util.Map;

/** AnimalDesc Class
 * This class contains the different attributes given to each animal.
 * Type (String): designates the type of animal
 * Marking (String): describes the animal
 * Name (String): name that will be used to differentiate
 * Active (Boolean): true if the animal is alive/still around, false if not
 * Inactive_desc (String): description of why an animal was deactivated
 * Compliant (Boolean): true if the animal is compliant with the rules of the user
 * Compliant_desc (String): description of why an animal is/is not compliant
 * Observation_desc (String): observations for the specific animal
 */

public class AnimalDesc {

    public String classification;
    public String type;
    public String origin;
    public String function;
    public String marking;
    public String name;
    public Boolean active;
    public String inactive_desc;
    public Map<String, String> images;
    public Boolean compliant;
    public String compliant_desc;
    public String observation_desc;

    public AnimalDesc() {
        // Default constructor required for calls to DataSnapshot.getValue(AnimalDesc.class)
    }

    public AnimalDesc(String classification, String type, String origin, String function, String marking, String name, Boolean active, String inactive_desc, Map<String, String> images, Boolean compliant,String compliant_desc,String observation_desc) {
        this.classification = classification;
        this.type = type;
        this.origin = origin;
        this.function = function;
        this.marking = marking;
        this.name = name;
        this.active = active;
        this.inactive_desc = inactive_desc;
        this.images = images;
        this.compliant = compliant;
        this.compliant_desc = compliant_desc;
        this.observation_desc = observation_desc;

    }

}
