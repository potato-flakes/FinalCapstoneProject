package com.system.finalcapstoneproject.reportingsystem;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.system.finalcapstoneproject.R;

public class createReport_activity extends AppCompatActivity{

    private int currentPage = 0; // Start from the first page
    private ProgressBar userProgressBar;
    private TextView textViewProgress;
    private TextView progressLabel;
    private TextView typeLabel;
    private ViewGroup containerLayout;
    private View loadingView;
    private AlertDialog dialog;
    private Handler handler = new Handler();
    private UserData userData;
    private CheckBox consentCheckbox;
    private Button agreeButton;
    private Button disagreeButton;
    private AlertDialog consentDialog;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ConsentPreferences";
    private static final String PREF_CONSENT_KEY = "UserConsent";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_layout);

        userProgressBar = findViewById(R.id.userProgressBar);
        textViewProgress = findViewById(R.id.text_view_progress);
        progressLabel = findViewById(R.id.progressLabel);
        typeLabel = findViewById(R.id.typeLabel);
        containerLayout = findViewById(R.id.progress_container);
        consentCheckbox = findViewById(R.id.consentCheckbox);
        agreeButton = findViewById(R.id.agreeButton);
        disagreeButton = findViewById(R.id.disagreeButton);
        loadingView = getLayoutInflater().inflate(R.layout.reporting_loading_screen, containerLayout, false);

        userData = new UserData();
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        Log.e("createReport_activity", "retrieveUserDetails - User ID:" + user_id);
        userData.setUserId(user_id);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Check if the user's consent choice has been remembered
        boolean userConsent = sharedPreferences.getBoolean(PREF_CONSENT_KEY, false);

        // If consent is already remembered, proceed directly
        if (userConsent) {
            navigateToNextFragment(new Step1Fragment());
        } else {
            // Show the consent dialog
            showConsentDialog();
        }
    }

    private void showConsentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View consentDialogView = getLayoutInflater().inflate(R.layout.consent_dialog, null);
        builder.setView(consentDialogView);

        consentCheckbox = consentDialogView.findViewById(R.id.consentCheckbox);
        agreeButton = consentDialogView.findViewById(R.id.agreeButton);
        disagreeButton = consentDialogView.findViewById(R.id.disagreeButton);

        // Checkbox is unchecked, disable the "Continue" button
        agreeButton.setEnabled(false);
        agreeButton.setBackgroundResource(R.drawable.reporting_disagree_consent_background); // Change to disabled button background
        agreeButton.setTextColor(getResources().getColor(R.color.white)); // Change to disabled button text color

        consentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Checkbox is checked, enable the "Continue" button
                    agreeButton.setEnabled(true);
                    agreeButton.setBackgroundResource(R.drawable.reporting_button_rounded_background); // Change to enabled button background
                    agreeButton.setTextColor(getResources().getColor(R.color.white)); // Change to enabled button text color
                } else {
                    // Checkbox is unchecked, disable the "Continue" button
                    agreeButton.setEnabled(false);
                    agreeButton.setBackgroundResource(R.drawable.reporting_disagree_consent_background); // Change to disabled button background
                    agreeButton.setTextColor(getResources().getColor(R.color.white)); // Change to disabled button text color
                }
            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (consentCheckbox.isChecked()) {
                    // User agrees, handle the consent
                    // For example, proceed with report creation
                    rememberUserConsent();
                    consentDialog.dismiss();
                    navigateToNextFragment(new Step1Fragment());
                } else {
                    // User did not check the consent checkbox, inform the user
                    // You can display an error message or customize the behavior
                    // For example, show a toast message
                    Toast.makeText(createReport_activity.this, "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User disagrees, you can handle this case (e.g., go back or exit the app)
                consentDialog.dismiss();
                // Handle disagreement action here, e.g., go back or close the application
                finish();
            }
        });

        // Find the TextViews for Privacy Policy and Terms and Conditions
        TextView privacyPolicyText = consentDialogView.findViewById(R.id.privacyPolicyText);
        TextView termsConditionsText = consentDialogView.findViewById(R.id.termsConditionsText);

        // Set click listeners for Privacy Policy
        privacyPolicyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the Privacy Policy page or content
                openPrivacyPolicy();
            }
        });

        // Set click listeners for Terms and Conditions
        termsConditionsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the Terms and Conditions page or content
                openTermsAndConditions();
            }
        });
        
        builder.setCancelable(false);
        consentDialog = builder.create();
        consentDialog.setCanceledOnTouchOutside(false);
        consentDialog.show();
    }

    // Function to open the Privacy Policy
    private void openPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://recyclearn.online/privacypolicy.php"));
        startActivity(intent);
    }

    // Function to open the Terms and Conditions
    private void openTermsAndConditions() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://recyclearn.online/termsandconditions.php"));
        startActivity(intent);
    }
    private void rememberUserConsent() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSENT_KEY, true);
        editor.apply();
    }

    public void navigateToNextFragment(Fragment fragment) {
        Log.e("createReportActivity", "navigateToNextFragment before: " + currentPage);
        currentPage++;
        updateProgress(currentPage * 33); // Update progress with animation
        replaceFragment(fragment, currentPage + "/3");
        updateLabels(); // Update labels here
        Log.e("createReportActivity", "navigateToNextFragment after: " + currentPage);
        switch (currentPage) {
            case 1:
                showLoadingScreen();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                getSupportFragmentManager().executePendingTransactions();
                hideLoadingScreen();
                break;
            case 2:
            case 3:
                showLoadingScreen();
                handler.postDelayed(() -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    hideLoadingScreen();
                }, 1100);
                break;
            default:
                // Form completed
                showFormComplete();
                break;
        }
        // Pass the userData to the next fragment
        if (fragment instanceof Step1Fragment) {
            ((Step1Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof Step2Fragment) {
            ((Step2Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof Step3Fragment) {
            ((Step3Fragment) fragment).setUserData(userData);
        }
    }

    public void navigateToPreviousFragment(Fragment fragment) {
        // Decrement currentPage here
        Log.e("createReportActivity", "navigateToPreviousFragment before: " + currentPage);
        currentPage--;
        Log.e("createReportActivity", "navigateToPreviousFragment after: " + currentPage);
        // Pass the userData to the previous fragment
        if (fragment instanceof Step1Fragment) {
            ((Step1Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof Step2Fragment) {
            ((Step2Fragment) fragment).setUserData(userData);
        } else if (fragment instanceof Step3Fragment) {
            ((Step3Fragment) fragment).setUserData(userData);
        }

        // Update progress, labels, and replace the fragment
        int progress = currentPage * 33;
        replaceFragment(fragment, currentPage + "/3");
        updateProgress(progress);
        updateLabels(); // Update labels here

        // Replace the fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        hideLoadingScreen();
    }

    private void updateLabels() {
        // Update progressLabel and typeLabel based on currentPage
        switch (currentPage) {
            case 1:
                progressLabel.setText("Next: Crime Details");
                typeLabel.setText("Crime Type");
                break;
            case 2:
                progressLabel.setText("Next: Personal Details");
                typeLabel.setText("Crime Details");
                break;
            case 3:
                progressLabel.setText("Next: Summary Details");
                typeLabel.setText("Personal Details");
                break;
            default:
                break;
        }
    }

    private void showLoadingScreen() {
        containerLayout.addView(loadingView);
    }

    private void hideLoadingScreen() {
        containerLayout.removeView(loadingView);
    }

    // Method to replace the current fragment in the container and update progress
    void replaceFragment(Fragment fragment, String progressText) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
        updateProgressText(progressText);
    }

    // Method to update the progress text
    private void updateProgressText(String progressText) {
        textViewProgress.setText(progressText);
    }

    // Method to update the progress bar
    private void updateProgress(int progress) {
        // Get the current progress value
        int currentProgress = userProgressBar.getProgress();

        // Create an ObjectAnimator to animate the progress change
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(userProgressBar, "progress", currentProgress, progress);
        progressAnimator.setDuration(1000); // Set the animation duration in milliseconds (adjust as needed)
        // Start the progress animation
        progressAnimator.start();
    }

    // Method to handle form completion
    void showFormComplete() {
        // For example, show a success message
        Toast.makeText(this, "Form completed successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Create a custom dialog with the custom theme
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.reporting_back_warning, null);
        builder.setView(dialogView);

        // Get references to the buttons in the custom dialog layout
        Button btnGoBack = dialogView.findViewById(R.id.btnGoBack);
        Button btnCancelReport = dialogView.findViewById(R.id.btnCancelReport);

        // Set click listeners for the buttons
        btnGoBack.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss the dialog when "Go Back" button is clicked
        });

        btnCancelReport.setOnClickListener(v -> {
            // Dismiss the dialog when "Cancel Report" button is clicked
            // Call the superclass method to handle "Go Back"
            super.onBackPressed();
            dialog.dismiss();
        });

        // Create and show the dialog
        dialog = builder.create();
        dialog.show();
    }

}
