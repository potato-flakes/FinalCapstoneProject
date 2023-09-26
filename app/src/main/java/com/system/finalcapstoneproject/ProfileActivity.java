package com.system.finalcapstoneproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    private RelativeLayout changePasswordLayout;
    private TextInputLayout textInputLayoutFirstName, textInputLayoutLastname, textInputLayoutPhone;
    private TextInputEditText first_name, last_name, email_address, phone_number;
    private TextView changeEmailText;
    private RadioGroup genderRadioGroup;
    private CardView uploadImageButton;
    private Button saveDetailsButton;
    private ImageView backButton;
    private Handler handler = new Handler(Looper.getMainLooper());
    private android.app.AlertDialog alertDialog;
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_DURATION = 60000; // 60 seconds (adjust as needed)
    private static final int PICK_IMAGE_REQUEST = 1; // Arbitrary request code
    private long lastResendClickTime = 0;
    private String checkEmailExistence = UrlConstants.CHECKEMAIL_URL;
    private String LOGIN_URL = UrlConstants.LOGIN_URL;
    private String user_id;
    public boolean isFirstNameValid;
    public boolean isLastNameValid;
    public boolean isPhoneValid;
    private boolean isSexValid;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    private boolean isEmailAlreadyExisting;
    // Declare the dialog variable at the class level
    private AlertDialog dialog;
    private TextInputLayout textInputLayoutChangeEmail;
    private TextInputEditText newEmailEditText;
    private String lastFirstName;
    private String lastLastName;
    private String lastPhone;
    private String lastSex;
    private RadioButton selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        first_name = findViewById(R.id.firstName);
        last_name = findViewById(R.id.lastName);
        email_address = findViewById(R.id.email);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        phone_number = findViewById(R.id.phone);
        uploadImageButton = findViewById(R.id.upload_image_button);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutLastname = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        changeEmailText = findViewById(R.id.changeEmailText);
        saveDetailsButton = findViewById(R.id.save_details_button);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String passed_user_id = sharedPreferences.getString("user_id", "");
        user_id = passed_user_id;
        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        enableSaveChangesButton();
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                genderRadioGroup.requestFocus();
                if (!hasChanges()) {
                    Log.e("ProfileActivity", "onTextChanged - genderRadioGroup hasChanges: " + hasChanges());
                    isSexValid = true;
                    saveDetailsButton.setEnabled(false);
                    saveDetailsButton.setBackgroundResource(R.drawable.save_details_disabled_background);
                } else {
                    isSexValid = true;
                    saveDetailsButton.setEnabled(true);
                    saveDetailsButton.setBackgroundResource(R.drawable.edit_profile_button_rounded_background);
                }
                enableSaveChangesButton();
            }
        });

        first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String firstName = charSequence.toString();
                if (firstName.isEmpty()) {
                    textInputLayoutFirstName.setError(getString(R.string.error_enter_firstname));
                    isFirstNameValid = false;
                } else if (!firstName.matches("^[a-zA-ZñÑ ]*$")) {
                    textInputLayoutFirstName.setError(getString(R.string.error_invalid_fullname));
                    isFirstNameValid = false;
                } else if (!hasChanges()) {
                    Log.e("ProfileActivity", "onTextChanged - first_name hasChanges: " + hasChanges());
                    isFirstNameValid = false;
                    textInputLayoutFirstName.setError(null);
                    textInputLayoutFirstName.setErrorEnabled(false);
                } else {
                    textInputLayoutFirstName.setError(null);
                    textInputLayoutFirstName.setErrorEnabled(false);
                    isFirstNameValid = true;
                }
                enableSaveChangesButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String lastName = charSequence.toString();
                if (lastName.isEmpty()) {
                    textInputLayoutLastname.setError(getString(R.string.error_enter_lastname));
                    isLastNameValid = false;
                } else if (!lastName.matches("^[a-zA-ZñÑ ]*$")) {
                    textInputLayoutLastname.setError(getString(R.string.error_invalid_fullname));
                    isLastNameValid = false;
                } else if (!hasChanges()) {
                    Log.e("ProfileActivity", "onTextChanged - last_name hasChanges: " + hasChanges());
                    isLastNameValid = false;
                    textInputLayoutLastname.setError(null);
                    textInputLayoutLastname.setErrorEnabled(false);
                } else {
                    textInputLayoutLastname.setError(null);
                    textInputLayoutLastname.setErrorEnabled(false);
                    isLastNameValid = true;
                }
                enableSaveChangesButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone_number = charSequence.toString();
                if (phone_number.isEmpty()) {
                    textInputLayoutPhone.setError(getString(R.string.error_enter_phone_number));
                    isPhoneValid = false;
                } else if (!phone_number.matches("^09\\d{9}$")) {
                    textInputLayoutPhone.setError(getString(R.string.error_invalid_phone_number));
                    isPhoneValid = false;
                } else if (!hasChanges()) {
                    Log.e("ProfileActivity", "onTextChanged - phone_number hasChanges: " + hasChanges());
                    textInputLayoutPhone.setError(null);
                    isPhoneValid = false;
                    textInputLayoutPhone.setErrorEnabled(false);
                } else {
                    textInputLayoutPhone.setError(null);
                    textInputLayoutPhone.setErrorEnabled(false);
                    isPhoneValid = true;
                }
                enableSaveChangesButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        changeEmailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog view
                View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_change_email, null);
                textInputLayoutChangeEmail = dialogView.findViewById(R.id.textInputLayoutChangeEmail);
                newEmailEditText = dialogView.findViewById(R.id.newEmailEditText);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this).setView(dialogView)
                        // Inside the "Confirm" button click listener
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle the email change confirmation
                                String newEmail = newEmailEditText.getText().toString();

                                // Update the email address
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Email updated successfully, now send a verification email
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Verification email sent successfully
                                                            Toast.makeText(ProfileActivity.this, "Verification email sent to " + newEmail, Toast.LENGTH_SHORT).show();
                                                            showVerificationDialog(newEmail);
                                                        } else {
                                                            // Failed to send verification email
                                                            Toast.makeText(ProfileActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Failed to update email
                                                Toast.makeText(ProfileActivity.this, "Failed to update email address.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                // Initialize the class-level dialog variable
                dialog = builder.create();

                // Initially, disable the "Confirm" button
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                });

                newEmailEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // Do nothing
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String email = charSequence.toString();

                        if (email.isEmpty()) {
                            textInputLayoutChangeEmail.setError(getString(R.string.error_enter_email));
                        } else if (!isValidEmail(email)) {
                            textInputLayoutChangeEmail.setError(getString(R.string.error_enter_valid_email));
                            textInputLayoutChangeEmail.requestFocus();
                            isEmailValid = false;
                        } else {
                            // Clear any existing error messages
                            textInputLayoutChangeEmail.setError(null);
                            textInputLayoutChangeEmail.setErrorEnabled(false);
                            isEmailValid = true;

                            // Check email existence asynchronously
                            checkEmailExists(email);
                        }

                        // Enable/disable the confirm button based on the results
                        enableConfirmButton(isEmailValid, isEmailAlreadyExisting);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // You can handle any additional logic here if needed
                    }
                });

                dialog.show();
            }
        });
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog view
                View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_change_password, null);
                TextInputLayout textInputLayoutChangePassword = dialogView.findViewById(R.id.textInputLayoutChangePassword);
                TextInputEditText changePassword = dialogView.findViewById(R.id.changePassword);
                String emailVerify = Objects.requireNonNull(email_address.getText()).toString().trim();

                textInputLayoutChangePassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                textInputLayoutChangePassword.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selection = textInputLayoutChangePassword.getEditText().getSelectionEnd();
                        textInputLayoutChangePassword.getEditText().setTransformationMethod(textInputLayoutChangePassword.getEditText().getTransformationMethod() == null ? PasswordTransformationMethod.getInstance()  // Show password
                                : null  // Hide password
                        );
                        textInputLayoutChangePassword.getEditText().setSelection(selection);
                    }
                });


                changePassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String password = charSequence.toString();
                        if (password.isEmpty()) {
                            textInputLayoutChangePassword.setError(getString(R.string.error_enter_password));
                            textInputLayoutChangePassword.setHelperText(null);
                            isPasswordValid = false;
                        } else if (!isValidPassword(password)) {
                            textInputLayoutChangePassword.setHelperText(getString(R.string.error_password_length));
                            textInputLayoutChangePassword.requestFocus();
                            isPasswordValid = false;
                        } else {
                            textInputLayoutChangePassword.setError(null);
                            textInputLayoutChangePassword.setHelperText(null);
                            textInputLayoutChangePassword.setErrorEnabled(false);
                            isPasswordValid = true;
                        }
                        // Enable/disable "Confirm" button based on the validation status
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(isPasswordValid);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this).setView(dialogView)
                        // Inside the "Confirm" button click listener
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle the email change confirmation
                                String passwordVerify = changePassword.getText().toString();
                                Log.e("ProfileActivity", "changePasswordLayout - Password: " + passwordVerify);
                                if (!passwordVerify.isEmpty()) {
                                    RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, response -> {
                                        Log.e("ProfileActivity", "changePasswordLayout - response: " + response);
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            String loginStatus = jsonResponse.getString("login");
                                            if (loginStatus.equals("Login Success")) {
                                                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                                                intent.putExtra("passwordVerifyKey", passwordVerify); // Provide a key for your data
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                                        }
                                    }, error -> {
                                        Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("email", emailVerify);
                                            params.put("password", passwordVerify);
                                            return params;
                                        }
                                    };

                                    requestQueue.add(stringRequest);
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Please enter the password verification", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        // Initially, disable the "Confirm" button
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                });
                dialog.show();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to pick an image from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Set the MIME type to images
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        backButton = findViewById(R.id.back_toggle);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to save the updated user details to the database
                saveUserDetails();
            }
        });

        retrieveUserDetails();
    }

    private void enableSaveChangesButton() {
        boolean firstNameIsValid = isFirstNameValid;
        boolean lastNameIsValid = isLastNameValid;
        boolean phoneIsValid = isPhoneValid;
        boolean sexIsValid = isSexValid;

        Log.e("ProfileActivity", "enableSaveChangesButton - State: " + firstNameIsValid + lastNameIsValid + phoneIsValid + sexIsValid);

        if (hasChanges() && firstNameIsValid && lastNameIsValid && phoneIsValid && sexIsValid) {
            saveDetailsButton.setEnabled(true);
            saveDetailsButton.setBackgroundResource(R.drawable.edit_profile_button_rounded_background);
        } else {
            saveDetailsButton.setEnabled(false);
            saveDetailsButton.setBackgroundResource(R.drawable.save_details_disabled_background);
        }
    }

    private boolean hasChanges() {
        String currentFirstName = first_name.getText().toString();
        String currentLastName = last_name.getText().toString();
        String currentPhone = phone_number.getText().toString();
        // Initialize a variable to store the selected gender
        String selectedGender = "";
        // Find the selected RadioButton
        int selectedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        // Check which RadioButton is selected
        if (selectedRadioButtonId == R.id.radioButtonMale) {
            selectedGender = "Male";
        } else if (selectedRadioButtonId == R.id.radioButtonFemale) {
            selectedGender = "Female";
        } else if (selectedRadioButtonId == R.id.radioButtonOther) {
            selectedGender = "Other";
        }
        Log.e("ProfileActivity", "hasChanges - Current State: " + currentFirstName + currentLastName + currentPhone + selectedGender);
        Log.e("ProfileActivity", "hasChanges - Last State: " + !currentFirstName.equals(lastFirstName) + !currentLastName.equals(lastLastName) + !currentPhone.equals(lastPhone) + !selectedGender.equals(lastSex));
        // Compare current values with original values
        return !currentFirstName.equals(lastFirstName)
                || !currentLastName.equals(lastLastName)
                || !currentPhone.equals(lastPhone)
                || (selectedGender != null && !selectedGender.equals(lastSex));


    }

    private void enableConfirmButton(boolean emailIsValid, boolean isEmailAlreadyExisting) {
        boolean enable = emailIsValid && !isEmailAlreadyExisting;
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enable);
    }

    private void checkEmailExists(String email) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, checkEmailExistence, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("exists")) {
                    textInputLayoutChangeEmail.setError(getString(R.string.error_email_exists));
                    textInputLayoutChangeEmail.requestFocus();
                    isEmailAlreadyExisting = true;
                } else {
                    textInputLayoutChangeEmail.setError(null);
                    textInputLayoutChangeEmail.setErrorEnabled(false);
                    isEmailAlreadyExisting = false;
                }

                // Enable/disable the confirm button based on the results
                enableConfirmButton(isEmailValid, isEmailAlreadyExisting);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error checking email existence: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        queue.add(request);
    }

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }


    private void showVerificationDialog(String newEmail) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verification, null);
        builder.setView(dialogView);

        // Initialize dialog elements
        TextView emailText = dialogView.findViewById(R.id.emailText);
        Button continueButton = dialogView.findViewById(R.id.continueButton);
        TextView countdownText = dialogView.findViewById(R.id.countdownText); // Add this TextView

        Button resendEmailButton = dialogView.findViewById(R.id.resendEmailButton);
        resendEmailButton.setEnabled(false);

        countDownTimer = new CountDownTimer(COUNTDOWN_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                countdownText.setText(getString(R.string.resend_countdown, secondsRemaining));
            }

            @Override
            public void onFinish() {
                countdownText.setVisibility(View.GONE);
                resendEmailButton.setEnabled(true); // Enable the resend button when the countdown is finished
            }
        }.start();

        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastResendClickTime >= COUNTDOWN_DURATION) {
                    // The cooldown period has passed, allow resending the email
                    lastResendClickTime = currentTime;

                    // Call the function to resend the email
                    resendVerificationEmail(newEmail);

                    // Disable the resend button and start the countdown timer again
                    resendEmailButton.setEnabled(false);
                    countDownTimer.start();
                } else {
                    // Display a message to inform the user about the cooldown
                    Toast.makeText(ProfileActivity.this, "Please wait before resending the email.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        emailText.setText(getString(R.string.email_verification_message, newEmail));

        // Set up a click listener for the verify email button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void resendVerificationEmail(String email) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Check if a user with this email address already exists
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(signInMethodsTask -> {
            if (signInMethodsTask.isSuccessful()) {
                if (signInMethodsTask.getResult() != null && signInMethodsTask.getResult().getSignInMethods() != null && signInMethodsTask.getResult().getSignInMethods().size() > 0) {
                    // User with this email address already exists
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(emailVerificationTask -> {
                        if (emailVerificationTask.isSuccessful()) {
                            // Email verification sent
                            Toast.makeText(ProfileActivity.this, "Email verification link is sent to your email address. Please verify your email address.", Toast.LENGTH_SHORT).show();
                            showVerificationDialog(email);
                        } else {
                            // Email verification sending failed
                            Toast.makeText(ProfileActivity.this, "Email verification failed. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // Error checking if user exists
                Toast.makeText(ProfileActivity.this, "Error checking email existence.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetails() {
        try {
            updateUserDataOnServer();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUserDataOnServer() throws ParseException {
        // Get the updated user details from the input fields
        String newFirstName = Objects.requireNonNull(first_name.getText()).toString().trim();
        String newLastName = Objects.requireNonNull(last_name.getText()).toString().trim();
        String newEmail = Objects.requireNonNull(email_address.getText()).toString().trim();
        String newPhone = Objects.requireNonNull(phone_number.getText()).toString().trim();
        // Initialize a variable to store the selected gender
        String selectedGender = "";
        // Find the selected RadioButton
        int selectedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        // Check which RadioButton is selected
        if (selectedRadioButtonId == R.id.radioButtonMale) {
            selectedGender = "Male";
        } else if (selectedRadioButtonId == R.id.radioButtonFemale) {
            selectedGender = "Female";
        } else if (selectedRadioButtonId == R.id.radioButtonOther) {
            selectedGender = "Other";
        }

        Log.e("Step3Fragment", "sendReport - newFirstName: " + newFirstName);
        Log.e("Step3Fragment", "sendReport - newLastName: " + newLastName);
        Log.e("Step3Fragment", "sendReport - newEmail: " + newEmail);
        Log.e("Step3Fragment", "sendReport - newPhone: " + newPhone);
        Log.e("Step3Fragment", "sendReport - newGender: " + selectedGender);
        String finalSelectedGender = selectedGender;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Declare response variable outside the try-catch block
                String response = "";
                try {
                    // Create JSON object with crime data
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_id", user_id);
                    jsonObject.put("newFirstName", newFirstName);
                    jsonObject.put("newLastName", newLastName);
                    jsonObject.put("newEmail", newEmail);
                    jsonObject.put("newPhone", newPhone);
                    jsonObject.put("selectedGender", finalSelectedGender);

                    Log.d("ProfileActivity", "reportCrime - Data to be passed: " + jsonObject);

                    // Send the data to the PHP API and get the response
                    URL url = new URL(UrlConstants.UPDATE_PASSWORD_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(jsonObject.toString());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response from the API
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        reader.close();

                        // Assign the response to the declared variable
                        response = responseBuilder.toString();

                        // Parse the response as JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        Log.d("ProfileActivity", "reportCrime - JSON Response - Data upload: " + jsonResponse);
                        // Extract the report ID from the response
                        String message = jsonResponse.getString("message");
                        Log.d("ProfileActivity", "reportCrime - Retrieved Message from server: " + message);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProfileActivity.this, "Details saved successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Display an error message
                                Toast.makeText(ProfileActivity.this, "Details saved failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    Log.e("ProfileActivity", "reportCrime - Response String: " + response); // Use the declared response variable
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProfileActivity.this, "Please check your inputs!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    private void retrieveUserDetails() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(UrlConstants.PROFILE_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    Log.e("ProfileActivity", "retrieveUserDetails - doInBackground - Retrieved Data From SharedPreferences: " + user_id);
                    String postData = "user_id=" + URLEncoder.encode(user_id, "UTF-8");

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postData.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    inputStream.close();
                    // Log the response from the server
                    Log.e("ProfileActivity", "retrieveUserDetails - doInBackground - Server Response:" + stringBuilder);
                    return stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String response) {

                if (response != null) {
                    try {
                        Log.e(TAG, response);
                        JSONObject jsonObject = new JSONObject(response);
                        String firstname = jsonObject.getString("firstname");
                        String lastname = jsonObject.getString("lastname");
                        String user_sex = jsonObject.getString("gender");
                        String email = jsonObject.getString("email");
                        String phone = jsonObject.getString("phone");
                        String joined = jsonObject.getString("joined");
                        // Inside doInBackground
                        String profileImageUrl = jsonObject.getString("tmp");

                        String imageUrl = UrlConstants.GET_USER_PROFILE_IMAGES + profileImageUrl + ".jpeg";

                        ImageView profileImage = findViewById(R.id.profile_image);
                        Picasso.get().load(imageUrl).into(profileImage);

                        Log.e("ProfileActivity", "onPostExecute - Retrieved Data:" + jsonObject);
                        Log.e("ProfileActivity", "onPostExecute - Retrieved firstname:" + firstname);
                        Log.e("ProfileActivity", "onPostExecute - Retrieved firstname:" + lastname);
                        Log.e("ProfileActivity", "onPostExecute - Retrieved firstname:" + email);

                        lastFirstName = firstname;
                        lastLastName = lastname;
                        lastPhone = phone;
                        lastSex = user_sex;

                        first_name.setText(firstname);
                        last_name.setText(lastname);
                        email_address.setText(email);
                        phone_number.setText(phone);
                        // Select the appropriate radio button based on user_sex
                        selectedRadioButton = null;

                        switch (user_sex) {
                            case "Male":
                                selectedRadioButton = findViewById(R.id.radioButtonMale);
                                break;
                            case "Female":
                                selectedRadioButton = findViewById(R.id.radioButtonFemale);
                                break;
                            case "Other":
                                selectedRadioButton = findViewById(R.id.radioButtonOther);
                                break;
                        }

                        if (selectedRadioButton != null) {
                            selectedRadioButton.setChecked(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ProfileActivity", "onPostExecute - Error Retrieving Data:" + response);
                    }
                }
            }
        };

        task.execute();
    }

    private boolean isValidEmail(String email) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the selected image's URI
            Uri imageUri = data.getData();

            // Load the selected image into the ImageView using Picasso
            ImageView profileImage = findViewById(R.id.profile_image);
            Picasso.get().load(imageUri).into(profileImage);
        }
    }

}
