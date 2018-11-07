package com.example.proyectotiti.models;

import java.util.Map;

/**
 * Created by katieschermerhorn on 3/1/18.
 */

public class Conservation {

    public boolean committed;
    public boolean compliant;
    public String area;
    public String agree_area;

    public Conservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Conservation.class)
    }

    public Conservation(boolean committed, boolean compliant, String area, String agree_area) {
        this.committed = committed;
        this.compliant = compliant;
        this.area = area;
        this.agree_area = agree_area;
    }
}
