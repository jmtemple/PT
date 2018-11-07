package com.example.proyectotiti.models;

import java.util.Map;

/** Basic Data Class
 * This class contains the basic data recorded for each family.
 * Name (String): name of the family
 * Community (String): name of the community for the famiy
 * Address (String): family's address
 * Phone_number (String): family phone number
 */

public class BasicData {

    public String name;
    public String community;
    public String address;
    public String phone_number;
    public Map<String, String> images;
    public String gps_coords;

    public BasicData() {
        // Default constructor required for calls to DataSnapshot.getValue(BasicData.class)
    }

    public BasicData(String name, String community, String address, String phone_number, Map<String, String> images, String gps_coords) {
        this.name = name;
        this.community = community;
        this.phone_number = phone_number;
        this.address = address;
        this.images = images;
        this.gps_coords = gps_coords;
    }

}
