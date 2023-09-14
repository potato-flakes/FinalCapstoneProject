package com.system.finalcapstoneproject.reportingsystem;

public class UrlConstants {
    public static final String IP_ADDRESS = "192.168.1.5";
    public static final String BASE_URL = "http://" + IP_ADDRESS + "/recyclearn/report_user/";
    public static final String GET_REPORTS = BASE_URL + "get_reports.php?user_id=";
    public static final String DELETE_REPORTS = BASE_URL + "delete_report.php";
    public static final String UPLOAD_REPORT = BASE_URL + "report.php";
    public static final String UPLOAD_IMAGES_REPORT = BASE_URL + "upload.php";
    public static final String GET_USER_DETAILS = BASE_URL + "get_user_details.php";
    // editReport_activity
    public static final String GET_REPORT_DETAILS = BASE_URL + "get_report_details.php?reportId=";
    public static final String GET_REPORT_IMAGES = BASE_URL + "images/";
    public static final String UPDATE_REPORT = BASE_URL + "update_report.php";

}