package com.system.finalcapstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPassword, textInputLayoutConfirmPassword;
    private TextInputEditText textInputEditTextPassword, textInputEditTextConfirmPassword;
    private Button btnSave;
    private String UPDATEPROFILE_URL = UrlConstants.UPDATE_PASSWORD_URL;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String user_id;
    private String receivedPassword;
    public boolean isPasswordValid;
    public boolean isConfirmPasswordValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextConfirmPassword = findViewById(R.id.confirmPassword);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        btnSave = findViewById(R.id.save_details_button);
        btnSave.setEnabled(false);
        btnSave.setBackgroundResource(R.drawable.save_details_disabled_background);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String passed_user_id = sharedPreferences.getString("user_id", "");
        user_id = passed_user_id;
        // Inside ChangePasswordActivity
        receivedPassword = getIntent().getStringExtra("passwordVerifyKey");

        TextInputLayout[] layouts = {textInputLayoutPassword, textInputLayoutConfirmPassword};

        for (TextInputLayout layout : layouts) {
            layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            layout.setEndIconOnClickListener(v -> {
                int selection = layout.getEditText().getSelectionEnd();
                layout.getEditText().setTransformationMethod(layout.getEditText().getTransformationMethod() == null ? new PasswordTransformationMethod() : null);
                layout.getEditText().setSelection(selection);
            });
        }

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
                } else if (receivedPassword.equals(password)) {
                    textInputLayoutPassword.setHelperText(getString(R.string.error_password_cannot_old_password));
                    textInputLayoutPassword.requestFocus();
                    isPasswordValid = false;
                } else {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setHelperText(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                    isPasswordValid = true;
                }


                validateConfirmPassword();

                enableUpdatePasswordButton();
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

                enableUpdatePasswordButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the updated user details to the server
                try {
                    saveUserDetails();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void enableUpdatePasswordButton() {
        boolean passwordIsValid = isPasswordValid;
        boolean confirmPasswordIsValid = isConfirmPasswordValid;
        String password = textInputEditTextPassword.getText().toString();
        String confirmPassword = textInputEditTextConfirmPassword.getText().toString();

        if (passwordIsValid && confirmPasswordIsValid && !password.isEmpty() && !confirmPassword.isEmpty()) {
            btnSave.setEnabled(true);
            btnSave.setBackgroundResource(R.drawable.edit_profile_button_rounded_background);
        } else {
            btnSave.setEnabled(false);
            btnSave.setBackgroundResource(R.drawable.save_details_disabled_background);
        }
    }

    private void saveUserDetails() throws ParseException {
        String newPassword = Objects.requireNonNull(textInputEditTextPassword.getText()).toString().trim();
        Log.e("Step3Fragment", "sendReport - newFirstName: " + newPassword);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Declare response variable outside the try-catch block
                String response = "";
                try {
                    // Create JSON object with crime data
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_id", user_id);
                    jsonObject.put("newPassword", newPassword);

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
                                Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Display an error message
                                Toast.makeText(ChangePasswordActivity.this, "Changing password failed. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    Log.e("ProfileActivity", "reportCrime - Response String: " + response); // Use the declared response variable
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this, "Please check your inputs", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
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
}
