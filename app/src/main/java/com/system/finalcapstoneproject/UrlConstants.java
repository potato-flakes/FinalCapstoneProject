package com.system.finalcapstoneproject;

public class UrlConstants {

    public static final String IP_ADDRESS = "192.168.1.25";
    public static final String BASE_URL = "http://" + IP_ADDRESS + "/recyclearn/";
    public static final String LOGIN_URL = BASE_URL + "user/login.php";
    public static final String SIGNUP_URL = BASE_URL + "user/signup.php";
    public static final String CHECKEMAIL_URL = BASE_URL + "user/check_email_exists.php";
    public static final String PROFILE_URL = BASE_URL + "user/profile.php";
    public static final String GET_USER_PROFILE_IMAGES = BASE_URL + "user/user_profile_images/";
    public static final String UPDATE_PASSWORD_URL = BASE_URL + "user/update_password.php";
    // Report_activity
    public static final String GET_REPORTS = BASE_URL + "report_user/get_reports.php?user_id=";
    public static final String DELETE_REPORTS = BASE_URL + "report_user/delete_report.php";
    public static final String UPLOAD_REPORT = BASE_URL + "report_user/report.php";
    public static final String UPLOAD_IMAGES_REPORT = BASE_URL + "report_user/upload.php";
    public static final String GET_USER_DETAILS = BASE_URL + "report_user/get_user_details.php";
    // editReport_activity
    public static final String GET_REPORT_DETAILS = BASE_URL + "report_user/get_report_details.php?reportId=";
    public static final String GET_REPORT_IMAGES = BASE_URL + "report_user/images/";
    public static final String UPDATE_REPORT = BASE_URL + "report_user/update_report.php";
    // Tutorial Activity
    public static final String GET_TUTORIALS = BASE_URL + "tutorial/get_tutorials.php?user_id=";
    public static final String DELETE_TUTORIALS = BASE_URL + "tutorial/" + "delete_tutorial.php";
    public static final String UPLOAD_TUTORIALS = BASE_URL + "tutorial/report.php";
    public static final String UPLOAD_IMAGE_TUTORIALS = BASE_URL + "tutorial/upload.php";
    // ChatActivty
    public static final String SEND_MESSAGE = BASE_URL + "report_user/send_message.php";
    // RewardClaimActivity
    public static final String GET_REWARDS_CLAIMED = BASE_URL + "report_user/get_rewards_claimed.php";
    public static final String UPDATE_REWARD_STATUS = BASE_URL + "report_user/update_reward_status.php";
    public static final String GET_REWARD_STATUS = BASE_URL + "report_user/get_reward_status.php";
    public static final String FETCH_UNCLAIMED_REPORTS = BASE_URL + "report_user/fetch_unclaimed_reports.php";
    public static final String FETCH_CLAIMED_REPORTS = BASE_URL + "report_user/fetch_claimed_reports.php";
    public static final String FETCH_ABORTED_REPORTS = BASE_URL + "report_user/fetch_aborted_reports.php";
}
