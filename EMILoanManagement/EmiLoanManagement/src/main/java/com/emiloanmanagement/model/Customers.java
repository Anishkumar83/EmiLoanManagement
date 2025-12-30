package com.emiloanmanagement.model;


import java.sql.Date;

public class Customers {
    private int customer_id;
    private String customer_name;
    private String email;
    private Date dob;
    private String gender;

    public Customers() {
    }

    public Customers(int customer_id, String customer_name, String email, Date dob, String gender) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
