package com.system.finalcapstoneproject;

public class UrlConstants {
    public static final String BASE_URL = "https://recyclearn.online/";
    public static final String LOGIN_URL = BASE_URL + "user/login.php";
    public static final String SIGNUP_URL = BASE_URL + "user/signup.php";
    public static final String CHECKEMAIL_URL = BASE_URL + "user/check_email_exists.php";
    public static final String CHECK_GOOGLE_EMAIL = BASE_URL + "user/check_google_email.php?email=";
    public static final String GET_EMAIL_DETAILS = BASE_URL + "user/get_email_details.php";
    public static final String PROFILE_URL = BASE_URL + "user/profile.php";
    public static final String GET_USER_PROFILE_IMAGES = BASE_URL + "user/user_profile_images/";
    public static final String UPDATE_PROFILE_URL = BASE_URL + "user/update_profile.php";
    public static final String UPDATE_PROFILE_PICTURE = BASE_URL + "user/upload_profile_image.php";
    public static final String UPDATE_PASSWORD_URL = BASE_URL + "user/update_password.php";
    public static final String UPDATE_EMAIL_URL = BASE_URL + "user/update_email.php";
    // Report_activity
    public static final String CHECK_BAN_STATUS = BASE_URL + "report_user/check.php";
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
    public static final String GET_SPECIFIC_TUTORIAL = BASE_URL + "tutorial/get_specific_tutorials.php?predictedClass=";
    public static final String GET_TUTORIAL_DETAILS = BASE_URL + "tutorial/get_tutorial_details.php?TutorialID=";
    public static final String UPLOAD_TUTORIAL = BASE_URL + "tutorial/save_tutorial_data.php";
    public static final String UPDATE_TUTORIAL = BASE_URL + "tutorial/update_tutorial_data.php";
    public static final String DELETE_TUTORIALS = BASE_URL + "tutorial/delete_tutorial.php";
    public static final String GET_TUTORIAL_IMAGES = BASE_URL + "tutorial/tutorial_images/";
    public static final String GET_TUTORIAL_HEADER_IMAGES = BASE_URL + "tutorial/tutorial_header_images/";
    public static final String UPLOAD_DATASET = BASE_URL + "tutorial/upload_dataset.php/";
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
