package com.system.finalcapstoneproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPassword, textInputLayoutConfirmPassword, textInputLayoutFirstName, textInputLayoutLastname, textInputLayoutEmail;
    private TextInputEditText textInputEditTextFirstName, textInputEditTextLastname, textInputEditTextPassword, textInputEditTextConfirmPassword, textInputEditTextEmail;
    private Button buttonSignUp;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    public boolean isFirstNameValid;
    public boolean isLastNameValid;
    public boolean isEmailValid;
    public boolean isPasswordValid;
    public boolean isConfirmPasswordValid;
    private String url = UrlConstants.SIGNUP_URL;
    private String checkEmailExistence = UrlConstants.CHECKEMAIL_URL;
    // Declare alertDialog variable here
    private AlertDialog alertDialog;
    // Add a variable to keep track of the cooldown time
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_DURATION = 60000; // 60 seconds (adjust as needed)
    private long lastResendClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextFirstName = findViewById(R.id.firstName);
        textInputEditTextLastname = findViewById(R.id.lastName);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextConfirmPassword = findViewById(R.id.confirmPassword);
        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutLastname = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);
        buttonSignUp.setEnabled(false);

        TextInputLayout[] layouts = {textInputLayoutPassword, textInputLayoutConfirmPassword};

        for (TextInputLayout layout : layouts) {
            layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            layout.setEndIconOnClickListener(v -> {
                int selection = layout.getEditText().getSelectionEnd();
                layout.getEditText().setTransformationMethod(layout.getEditText().getTransformationMethod() == null ? new PasswordTransformationMethod() : null);
                layout.getEditText().setSelection(selection);
            });
        }

        textInputEditTextFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
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
                } else {
                    textInputLayoutFirstName.setError(null);
                    textInputLayoutFirstName.setErrorEnabled(false);
                    isFirstNameValid = true;
                }
                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textInputEditTextLastname.addTextChangedListener(new TextWatcher() {
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
                } else {
                    textInputLayoutLastname.setError(null);
                    textInputLayoutLastname.setErrorEnabled(false);
                    isLastNameValid = true;
                }
                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textInputEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = charSequence.toString();
                if (email.isEmpty()) {
                    textInputLayoutEmail.setError(getString(R.string.error_enter_email));
                    isEmailValid = false;
                } else if (!isValidEmail(email)) {
                    textInputLayoutEmail.setError(getString(R.string.error_enter_valid_email));
                    textInputLayoutEmail.requestFocus();
                    isEmailValid = false;
                    enableSignUpButton();
                    return;
                } else {

                    checkEmailExists(email);
                }
                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textInputEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
                if (password.isEmpty()) {
                    textInputLayoutPassword.setError(getString(R.string.error_enter_password));
                    textInputLayoutPassword.setHelperText(null);
                    isPasswordValid = false;
                } else if (!isValidPassword(password)) {
                    textInputLayoutPassword.setHelperText(getString(R.string.error_password_length));
                    textInputLayoutPassword.requestFocus();
                    isPasswordValid = false;
                } else {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setHelperText(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                    isPasswordValid = true;
                }


                validateConfirmPassword();

                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textInputEditTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String confirmPassword = charSequence.toString();
                if (confirmPassword.isEmpty()) {
                    textInputLayoutConfirmPassword.setError(getString(R.string.error_confirm_password));
                    isConfirmPasswordValid = false;
                } else {
                    String password = textInputEditTextPassword.getText().toString();
                    if (!confirmPassword.equals(password)) {
                        textInputLayoutConfirmPassword.setError(getString(R.string.error_password_mismatch));
                        textInputLayoutConfirmPassword.requestFocus();
                        isConfirmPasswordValid = false;
                    } else {
                        textInputLayoutConfirmPassword.setError(null);
                        textInputLayoutConfirmPassword.setErrorEnabled(false);
                        isConfirmPasswordValid = true;
                    }
                }

                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Find the RadioGroup
        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);

        // Find the RadioButton for "Male"
        RadioButton radioButtonMale = findViewById(R.id.radioButtonMale);

        // Set "Male" as the default gender
        radioButtonMale.setChecked(true);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstname, lastname, password, email;
                firstname = String.valueOf(textInputEditTextFirstName.getText());
                lastname = String.valueOf(textInputEditTextLastname.getText());
                email = String.valueOf(textInputEditTextEmail.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
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
                progressBar.setVisibility(View.VISIBLE);

                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                int random = new Random().nextInt(9999999) + 1000000; // Generates a random 7-digit number
                int user_id = random;

                // Check if a user with this email address already exists
                String finalSelectedGender = selectedGender;
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(signInMethodsTask -> {
                    if (signInMethodsTask.isSuccessful()) {
                        if (signInMethodsTask.getResult() != null && signInMethodsTask.getResult().getSignInMethods() != null && signInMethodsTask.getResult().getSignInMethods().size() > 0) {
                            // User with this email address already exists
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(emailVerificationTask -> {
                                        if (emailVerificationTask.isSuccessful()) {
                                            // Email verification sent
                                            Toast.makeText(SignUpActivity.this, "Email verification link is already sent to your email address. Please verify your email address.", Toast.LENGTH_SHORT).show();
                                            showVerificationDialog();
                                        } else {
                                            // Email verification sending failed
                                            Toast.makeText(SignUpActivity.this, "Email verification failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            progressBar.setVisibility(View.GONE); // Hide the progress bar
                        } else {
                            // User with this email address does not exist, create a new account
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                                        if (task.isSuccessful()) {
                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(emailVerificationTask -> {
                                                        if (emailVerificationTask.isSuccessful()) {
                                                            // Create a new JSONObject
                                                            JSONObject postData = new JSONObject();
                                                            try {
                                                                postData.put("user_id", user_id); // Set the unique user ID
                                                                postData.put("firstname", firstname);
                                                                postData.put("lastname", lastname);
                                                                postData.put("email", email);
                                                                postData.put("password", password);
                                                                postData.put("sex", finalSelectedGender);

                                                                Log.d("SignUpActivity", "buttonSignUp - Data to be passed: " + postData);
                                                            } catch (JSONException e) {
                                                                throw new RuntimeException(e);
                                                            }

                                                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData, response -> {
                                                                progressBar.setVisibility(View.GONE);

                                                                String result = null;
                                                                try {
                                                                    result = response.getString("result");
                                                                    Log.d("SignUpActivity", "JSON Response" + response.toString()); // Log the JSON response
                                                                } catch (JSONException e) {
                                                                    Log.e("SignUpActivity", "JSON Error - Error parsing JSON response: " + e.getMessage()); // Log JSON parsing error
                                                                    throw new RuntimeException(e);
                                                                }

                                                                if (result.equals("Sign Up Success")) {
                                                                    showVerificationDialog();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }, error -> {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                                                Log.e("Volley Error", "Error in Volley request: " + error.toString()); // Log Volley error

                                                                // Log the response that caused the error (if available)
                                                                if (error.networkResponse != null && error.networkResponse.data != null) {
                                                                    String errorResponse = new String(error.networkResponse.data);
                                                                    Log.e("Volley Error Response", errorResponse); // Log the entire error response (HTML error)
                                                                }

                                                                // Handle the error appropriately
                                                                if (error instanceof ParseError) {
                                                                    Log.e("Volley Error", "Error in Volley request: " + error.toString()); // Log Volley error
                                                                } else {
                                                                    // Handle other types of errors
                                                                }
                                                            });

                                                            Volley.newRequestQueue(SignUpActivity.this).add(request);

                                                        } else {
                                                            // Email verification sending failed
                                                            Toast.makeText(SignUpActivity.this, "Email verification failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // User registration failed
                                            Toast.makeText(SignUpActivity.this, "User registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Error checking if user exists
                        Toast.makeText(SignUpActivity.this, "Error checking email existence.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                    }
                });
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Function to resend the verification email
    private void resendVerificationEmail(String email) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Check if a user with this email address already exists
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(signInMethodsTask -> {
            if (signInMethodsTask.isSuccessful()) {
                if (signInMethodsTask.getResult() != null && signInMethodsTask.getResult().getSignInMethods() != null && signInMethodsTask.getResult().getSignInMethods().size() > 0) {
                    // User with this email address already exists
                    firebaseAuth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(emailVerificationTask -> {
                                if (emailVerificationTask.isSuccessful()) {
                                    // Email verification sent
                                    Toast.makeText(SignUpActivity.this, "Email verification link is sent to your email address. Please verify your email address.", Toast.LENGTH_SHORT).show();
                                    showVerificationDialog();
                                } else {
                                    // Email verification sending failed
                                    Toast.makeText(SignUpActivity.this, "Email verification failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                }
            } else {
                // Error checking if user exists
                Toast.makeText(SignUpActivity.this, "Error checking email existence.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Hide the progress bar
            }
        });
    }

    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verification, null);
        builder.setView(dialogView);

        // Initialize dialog elements
        TextView emailText = dialogView.findViewById(R.id.emailText);
        Button continueButton = dialogView.findViewById(R.id.continueButton);
        TextView countdownText = dialogView.findViewById(R.id.countdownText); // Add this TextView

        Button resendEmailButton = dialogView.findViewById(R.id.resendEmailButton);
        resendEmailButton.setEnabled(false);

        // Set the email address in the dialog
        String email = textInputEditTextEmail.getText().toString();

        // Start the countdown timer
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
                    resendVerificationEmail(email);

                    // Disable the resend button and start the countdown timer again
                    resendEmailButton.setEnabled(false);
                    countDownTimer.start();
                } else {
                    // Display a message to inform the user about the cooldown
                    Toast.makeText(SignUpActivity.this, "Please wait before resending the email.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        emailText.setText(getString(R.string.email_verification_message, email));

        // Set up a click listener for the verify email button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }


    private void validateConfirmPassword() {
        String password = textInputEditTextPassword.getText().toString();
        String confirmPassword = textInputEditTextConfirmPassword.getText().toString();

        if (!confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            textInputLayoutConfirmPassword.setError(getString(R.string.error_password_mismatch));
            isConfirmPasswordValid = false;
        } else {
            textInputLayoutConfirmPassword.setError(null);
            textInputLayoutConfirmPassword.setErrorEnabled(false);
            isConfirmPasswordValid = true;
        }
    }

    private void checkEmailExists(String email) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, checkEmailExistence, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("exists")) {
                    textInputLayoutEmail.setError(getString(R.string.error_email_exists));
                    textInputLayoutEmail.requestFocus();
                    isEmailValid = false;
                } else {
                    textInputLayoutEmail.setError(null);
                    textInputLayoutEmail.setErrorEnabled(false);
                    isEmailValid = true;
                }
                enableSignUpButton();
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

    private boolean isValidEmail(String email) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void enableSignUpButton() {
        boolean firstNameIsValid = isFirstNameValid;
        boolean lastNameIsValid = isLastNameValid;
        boolean emailIsValid = isEmailValid;
        boolean passwordIsValid = isPasswordValid;
        boolean confirmPasswordIsValid = isConfirmPasswordValid;

        if (firstNameIsValid && lastNameIsValid && emailIsValid && passwordIsValid && confirmPasswordIsValid) {
            buttonSignUp.setEnabled(true);
        } else {
            buttonSignUp.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}