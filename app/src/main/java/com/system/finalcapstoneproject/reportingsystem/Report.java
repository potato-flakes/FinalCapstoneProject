package com.system.finalcapstoneproject.reportingsystem;

// Report.java

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String report_id;
    private String user_id;
    private String crime_type;
    private String crime_person;
    private String crime_location;
    private String location;
    private String crime_date;
    private String crime_description;
    private String crime_time;
    private String crime_barangay;
    private String crime_user_name;
    private String crime_user_sex;
    private String crime_user_phone;
    private String crime_user_email;
    private String report_date;
    private String isUseCurrentLocation;
    private String isIdentified;
    private String status;
    private String reward_claimed;
    private List<String> imageUrls;
    private List<String> imagePaths; // Ad

    public Report(String report_id, String user_id, String crime_type, String crime_person, String crime_location, String crime_date, String crime_description, String crime_time, String crime_barangay, String crime_user_name, String crime_user_sex, String crime_user_phone, String crime_user_email, String report_date, String isUseCurrentLocation, String isIdentified, String status, String reward_claimed) {
        this.report_id = report_id;
        this.user_id = user_id;
        this.crime_type = crime_type;
        this.crime_person = crime_person;
        this.crime_location = crime_location;
        this.crime_date = crime_date;
        this.crime_description = crime_description;
        this.crime_time = crime_time;
        this.crime_barangay = crime_barangay;
        this.crime_user_name = crime_user_name;
        this.crime_user_sex = crime_user_sex;
        this.crime_user_phone = crime_user_phone;
        this.crime_user_email = crime_user_email;
        this.report_date = report_date;
        this.isUseCurrentLocation = isUseCurrentLocation;
        this.isIdentified = isIdentified;
        this.status = status;
        this.reward_claimed = reward_claimed;
        this.imageUrls = new ArrayList<>();
        imagePaths = new ArrayList<>();
    }

    public String getReportId() {
        return report_id;
    }
    public String getUserId() {
        return user_id;
    }
    public String getCrime_type() {
        return crime_type;
    }
    public String getCrime_person() {
        return crime_person;
    }
    public String getCrime_location() {
        return crime_location;
    }
    public String getCrime_date() {
        return crime_date;
    }
    public String getCrime_description() {
        return crime_description;
    }
    public String getCrime_time() {
        return crime_time;
    }
    public String getReportStatus() {
        return status;
    }
    public String getCrime_barangay() {
        return crime_barangay;
    }

    public String getCrime_user_name() {
        return crime_user_name;
    }
    public String getCrime_user_sex() {
        return crime_user_sex;
    }
    public String getCrime_user_phone() {
        return crime_user_phone;
    }
    public String getCrime_user_email() {
        return crime_user_email;
    }
    public String getReport_date() {
        return report_date;
    }
    public String getIsUseCurrentLocation() {
        return isUseCurrentLocation;
    }
    public String getIsIdentified() {
        return isIdentified;
    }
    public String getStatus() {
        return status;
    }
    public String getReward_claimed() {
        return reward_claimed;
    }
    public void addImageUrl(String imageUrl) {
        imageUrls.add(imageUrl);
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public List<String> getImagePaths() {
        // Return the list of image paths
        return imagePaths;
    }
}
