package com.system.finalcapstoneproject.CreateTutorial;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.system.finalcapstoneproject.R;
import com.system.finalcapstoneproject.reportingsystem.Step1Fragment;
import com.system.finalcapstoneproject.reportingsystem.Step2Fragment;
import com.system.finalcapstoneproject.reportingsystem.Step3Fragment;
import com.system.finalcapstoneproject.reportingsystem.UserData;

public class CreateTutorialActivity extends AppCompatActivity{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);

        userProgressBar = findViewById(R.id.userProgressBar);
        textViewProgress = findViewById(R.id.text_view_progress);
        progressLabel = findViewById(R.id.progressLabel);
        typeLabel = findViewById(R.id.typeLabel);
        containerLayout = findViewById(R.id.progress_container);
        loadingView = getLayoutInflater().inflate(R.layout.reporting_loading_screen, containerLayout, false);
        userData = new UserData();

        // Start the form by showing the first fragment
        navigateToNextFragment(new TutorialStep1Fragment());
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
        if (fragment instanceof TutorialStep1Fragment) {
            ((TutorialStep1Fragment) fragment).setUserData(userData);
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
        if (fragment instanceof TutorialStep1Fragment) {
            ((TutorialStep1Fragment) fragment).setUserData(userData);
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
                progressLabel.setText("Next: Tutorial Details");
                typeLabel.setText("Tutorial Title");
                break;
            case 2:
                progressLabel.setText("Next: User Details");
                typeLabel.setText("Tutorial Details");
                break;
            case 3:
                progressLabel.setText("Next: Summary Details");
                typeLabel.setText("User Details");
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
