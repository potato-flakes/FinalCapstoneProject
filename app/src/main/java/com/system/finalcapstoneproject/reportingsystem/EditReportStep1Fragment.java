package com.system.finalcapstoneproject.reportingsystem;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.system.finalcapstoneproject.R;

public class EditReportStep1Fragment extends Fragment {

    private RadioGroup crimeRadioGroup;
    private String selectedCrimeType;
    private TextInputLayout crimeRadioGroupLayout;
    private View customErrorMessageView;
    private Handler errorHandler = new Handler();
    private boolean isErrorVisible = false;
    private UserData userData;
    private Button backButton;

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public EditReportStep1Fragment() {
        // Required empty public constructor
    }

    @Nullable
    // Inside onCreateView method of EditReportStep1Fragment.java

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reporting_edit_report_fragment_step1, container, false);

        Log.e("EditReportStep1Fragment", "You are in EditReportStep1Fragment");

        crimeRadioGroup = view.findViewById(R.id.crimeRadioGroup);
        crimeRadioGroupLayout = view.findViewById(R.id.crimeRadioGroupLayout);
        customErrorMessageView = view.findViewById(R.id.customErrorMessage);
        backButton = view.findViewById(R.id.backButton);
        customErrorMessageView.setVisibility(View.GONE); // Hide it initially

        boolean switchStatus = userData.isLocationEnabled();
        Log.d("EditReportStep1Fragment", "onCreateView - Switch status: " + switchStatus);

        // Populate the UI elements with data from the userData object (if available)
        if (userData != null) {
            String crimeType = userData.getCrimeType();
            Log.e("EditReportStep1Fragment", "onCreateView - crimeType: " + crimeType);
            if (crimeType != null) {
                for (int i = 0; i < crimeRadioGroup.getChildCount(); i++) {
                    View radioView = crimeRadioGroup.getChildAt(i);
                    if (radioView instanceof RadioButton) {
                        RadioButton radioButton = (RadioButton) radioView;
                        if (radioButton.getText().toString().equals(crimeType)) {
                            radioButton.setChecked(true);
                            selectedCrimeType = radioButton.getText().toString();
                            break;
                        }
                    }
                }
            } else {
                Log.e("EditReportStep1Fragment", "onCreateView - crimeType is null");
            }
        } else {
            Log.e("EditReportStep1Fragment", "onCreateView - userData is null");
        }
        crimeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected RadioButton's text
                RadioButton selectedRadioButton = view.findViewById(checkedId);
                selectedCrimeType = selectedRadioButton.getText().toString();

                // Save the selected crime type to the userData object
                userData.setCrimeType(selectedCrimeType);

                // Hide the custom error message when a selection is made
                hideErrorMessage();
            }
        });

        // Get the value from the EditText and pass it to EditReportStep2Fragment when the "Next" button is clicked
        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a crime type is selected
                if (selectedCrimeType == null) {
                    // Show the custom error message above the RadioGroup
                    crimeRadioGroupLayout.requestFocus();

                    showErrorMessage();
                    return; // Return without navigating to the next fragment
                } else {
                    hideErrorMessage();
                }
                // Save the user's input to the userData object
                userData.setCrimeType(selectedCrimeType);

                // Navigate to the next fragment (Step2Fragment)
                ((editReport_activity) requireActivity()).navigateToNextFragment(new EditReportStep2Fragment());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return view;
    }

    private void showErrorMessage() {
        if (!isErrorVisible) {
            isErrorVisible = true;
            customErrorMessageView.setVisibility(View.VISIBLE);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
            customErrorMessageView.startAnimation(fadeInAnimation);

            // Set a duration for the visibility of the error message
            int errorMessageDuration = 3000; // 3 seconds
            errorHandler.removeCallbacksAndMessages(null); // Cancel previous delayed runnable
            errorHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideErrorMessage();
                }
            }, errorMessageDuration);
        }
    }

    private void hideErrorMessage() {
        if (isErrorVisible) {
            isErrorVisible = false;
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out);
            customErrorMessageView.startAnimation(fadeOutAnimation);

            // Wait for the animation to complete before setting the visibility to GONE
            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    customErrorMessageView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private void cancelErrorMessage() {
        isErrorVisible = false;
        errorHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel the error message handler when the view is destroyed
        cancelErrorMessage();
    }

    public static EditReportStep1Fragment newInstance(int currentPage) {
        EditReportStep1Fragment fragment = new EditReportStep1Fragment();
        Bundle args = new Bundle();
        args.putInt("currentPage", currentPage);
        fragment.setArguments(args);
        return fragment;
    }

}
