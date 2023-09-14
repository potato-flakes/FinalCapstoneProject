package com.system.finalcapstoneproject.reportingsystem;

public class UserDetails {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;

    public UserDetails(String firstName, String lastName, String email, String phone, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getGender() {
        return gender;
    }
}
