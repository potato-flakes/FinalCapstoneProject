package com.system.finalcapstoneproject.reportingsystem;

// Report.java

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String report_id;
    private String user_id;
    private String firstname;
    private String lastname;
    private String description;
    private String location;
    private String date;
    private String time;
    private List<String> imageUrls;
    private List<String> imagePaths; // Ad

    public Report(String report_id, String user_id, String firstname, String lastname, String description, String location, String date, String time) {
        this.report_id = report_id;
        this.user_id = user_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
        this.imageUrls = new ArrayList<>();
        imagePaths = new ArrayList<>();
    }

    public String getReportId() {
        return report_id;
    }


    public String getUserId() {
        return user_id;
    }
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }


    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
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
