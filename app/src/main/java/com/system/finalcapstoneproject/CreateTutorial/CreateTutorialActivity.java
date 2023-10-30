package com.system.finalcapstoneproject.CreateTutorial;

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

import com.system.finalcapstoneproject.R;

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
    private TutorialData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);

        progressLabel = findViewById(R.id.progressLabel);
        typeLabel = findViewById(R.id.typeLabel);
        containerLayout = findViewById(R.id.progress_container);
        loadingView = getLayoutInflater().inflate(R.layout.reporting_loading_screen, containerLayout, false);
        userData = new TutorialData();

        // Start the form by showing the first fragment
        navigateToNextFragment(new TutorialStep1Fragment());
    }

    public void navigateToNextFragment(Fragment fragment) {
        Log.e("createReportActivity", "navigateToNextFragment before: " + currentPage);
        currentPage++;
        switch (currentPage) {
            case 1:
                showLoadingScreen();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                getSupportFragmentManager().executePendingTransactions();
                hideLoadingScreen();
                break;
            default:
                // Form completed
                showFormComplete();
                break;
        }
        // Pass the userData to the next fragment
        if (fragment instanceof TutorialStep1Fragment) {
            ((TutorialStep1Fragment) fragment).setUserData(userData);
        }
    }

    private void showLoadingScreen() {
        containerLayout.addView(loadingView);
    }

    private void hideLoadingScreen() {
        containerLayout.removeView(loadingView);
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
        View dialogView = getLayoutInflater().inflate(R.layout.tutorial_back_warning, null);
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
