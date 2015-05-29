package com.jiyouliang.datastore.model;

import java.io.Serializable;
/**
 * @author YouLiang.Ji
 */
public class Student implements Serializable{
    private String achool;
    private String professional;

    public String getAchool() {
        return achool;
    }

    public void setAchool(String achool) {
        this.achool = achool;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    @Override public String toString() {
        return "Student{" +
                "achool='" + achool + '\'' +
                ", professional='" + professional + '\'' +
                '}';
    }
}
