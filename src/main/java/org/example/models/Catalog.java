package org.example.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Catalog {

    private String uuid;
    private Date date;
    private String company;
    private List<Plant> plants;

    public Catalog() {
        plants = new ArrayList<Plant>();
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getDate() {
        return date;
    }

    public String getCompany() {
        return company;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
