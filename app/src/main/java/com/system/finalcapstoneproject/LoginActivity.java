package com.system.finalcapstoneproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.system.finalcapstoneproject.AdminScannerActivity.AdminDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPassword, textInputLayoutEmail;
    private TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    private ProgressBar progressBar;
    private boolean isEmailValid, isPasswordValid;
    private String LOGIN_URL = UrlConstants.LOGIN_URL;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private String continueToGoogleEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            navigateToSecondActivity();
        }

        // Check login state on app launch
        checkLoginStateOnLaunch();

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.signUpText);
        progressBar = findViewById(R.id.progress);
        // Disable the login button at startup
        buttonLogin.setEnabled(false);

        textInputEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = charSequence.toString();
                if (email.isEmpty()) {
                    isEmailValid = false;
                } else if (!isValidEmail(email)) {
                    textInputLayoutEmail.requestFocus();
                    isEmailValid = false;
                    return;
                } else {
                    textInputLayoutEmail.setError(null);
                    textInputLayoutEmail.setErrorEnabled(false);
                    isEmailValid = true;
                }
                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textInputEditTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmail();
                }
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
                    isPasswordValid = false;
                } else {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                    isPasswordValid = true;
                }
                enableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textInputLayoutPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        textInputLayoutPassword.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selection = textInputEditTextPassword.getSelectionEnd();
                textInputEditTextPassword.setTransformationMethod(textInputEditTextPassword.getTransformationMethod() == null ? new PasswordTransformationMethod() : null);
                textInputEditTextPassword.setSelection(selection);
            }
        });

        Button btnGoogleSignIn = findViewById(R.id.btnCustomGoogleLogin);
        Drawable googleIcon = getResources().getDrawable(R.drawable.google);
        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size); // Adjust the size as needed
        googleIcon.setBounds(0, 0, iconSize, iconSize);
        btnGoogleSignIn.setCompoundDrawables(googleIcon, null, null, null);
        int iconPaddingRight = getResources().getDimensionPixelSize(R.dimen.icon_padding_right); // Adjust the padding as needed
        btnGoogleSignIn.setCompoundDrawablePadding(iconPaddingRight);

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disable the login button to prevent multiple clicks
                buttonLogin.setEnabled(false);

                final String email, password;

                email = Objects.requireNonNull(textInputEditTextEmail.getText()).toString().trim();
                password = Objects.requireNonNull(textInputEditTextPassword.getText()).toString().trim();

                if (!email.equals("") && !password.equals("")) {
                    Log.e("LoginActivity", "buttonLogin - email and password not empty"); // Log JSON parsing error
                    if (isValidEmail(email)) {
                        progressBar.setVisibility(View.VISIBLE);

                        // Make a request to your server to check email and password
                        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                LOGIN_URL,
                                response -> {
                                    progressBar.setVisibility(View.GONE);
                                    Log.e("LoginActivity", "buttonLogin - response: " + response); // Log JSON parsing error
                                    if (response.contains("Login Success")) {
                                        if (response.contains("email_verified_override")) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                String email_verified_override = jsonResponse.getString("email_verified_override");
                                                if (email_verified_override.equals("1")) {
                                                    if (response.contains("user_id")) {
                                                        try {
                                                            String user_id = jsonResponse.getString("user_id");
                                                            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("user_id", user_id);
                                                            editor.apply();
                                                            // On successful login, set the user as logged in
                                                            setLoggedIn(LoginActivity.this, true);
                                                            Log.e("LoginActivity", "btnLogin - User ID:" + user_id);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            // Handle JSON parsing error here
                                                        }
                                                    }
                                                    // Check user role
                                                    if (response.contains("access_level")) {
                                                        try {
                                                            String access_level = jsonResponse.getString("access_level");
                                                            Log.e("LoginActivity", "buttonLogin - access_level: " + access_level);
                                                            if ("reportadmin".equals(access_level)) {
                                                                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                                // Redirect to the QR scanner activity
                                                                Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                                // User is not a report admin, redirect to the home activity
                                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("access_level", access_level);
                                                            editor.apply();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            // Handle JSON parsing error here
                                                        }
                                                    }
                                                } else {
                                                    // Now, check if the email is verified
                                                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                                        // Re-enable the login button

                                                        if (task.isSuccessful()) {
                                                            Log.e("LoginActivity", "buttonLogin - Email verification link sent");
                                                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                                                Log.e("LoginActivity", "buttonLogin - Email is verified");
                                                                if (response.contains("user_id")) {
                                                                    try {
                                                                        String user_id = jsonResponse.getString("user_id");
                                                                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                        editor.putString("user_id", user_id);
                                                                        editor.apply();
                                                                        // On successful login, set the user as logged in
                                                                        setLoggedIn(LoginActivity.this, true);
                                                                        Log.e("LoginActivity", "btnLogin - User ID:" + user_id);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        // Handle JSON parsing error here
                                                                    }
                                                                }
                                                                // Check user role
                                                                if (response.contains("access_level")) {
                                                                    try {
                                                                        String access_level = jsonResponse.getString("access_level");
                                                                        Log.e("LoginActivity", "buttonLogin - access_level: " + access_level);
                                                                        if ("reportadmin".equals(access_level)) {
                                                                            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                                            // Redirect to the QR scanner activity
                                                                            Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                                            // User is not a report admin, redirect to the home activity
                                                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                        editor.putString("access_level", access_level);
                                                                        editor.apply();
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        // Handle JSON parsing error here
                                                                    }
                                                                }
                                                            } else {
                                                                // Email address is not verified
                                                                Toast.makeText(getApplicationContext(), "Email address is not verified. Please verify your email address", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            // Error occurred while checking email verification status
                                                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                // Handle JSON parsing error here
                                            }
                                        }

                                    } else {
                                        // Incorrect email or password
                                        // Re-enable the login button
                                        buttonLogin.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> {
                                    progressBar.setVisibility(View.GONE);
                                    // Re-enable the login button
                                    buttonLogin.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("email", email);
                                params.put("password", password);
                                return params;
                            }
                        };

                        requestQueue.add(stringRequest);
                    } else {
                        // Re-enable the login button
                        buttonLogin.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Re-enable the login button
                    buttonLogin.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });


        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    // Modify your signIn() method
    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    // Function to set the user as logged in
    private void setLoggedIn(Context context, boolean isLoggedIn) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Function to check if the user is logged in
    private boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Check login state on app launch
    private void checkLoginStateOnLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String access_level = sharedPreferences.getString("access_level", "");
        if (isLoggedIn(this)) {
            if ("reportadmin".equals(access_level)) {
                // User is logged in as a report admin, navigate to the Admin Dashboard
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish(); // Finish the login activity to prevent going back
            } else {
                // User is logged in as a regular user, navigate to the HomeActivity
                startActivity(new Intent(this, HomeActivity.class));
                finish(); // Finish the login activity to prevent going back
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Check if the user is registered by their email
                    String userEmail = account.getEmail();
                    continueToGoogleEmail = userEmail;
                    Log.e("LoginActivity", "buttonLogin - userEmailAddress: " + userEmail);

                    // Use the AsyncTask to check email existence
                    new CheckEmailExistenceTask(this).execute(userEmail);
                }
                Log.e("LoginActivity", "buttonLogin - account: " + account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class CheckEmailExistenceTask extends AsyncTask<String, Void, Boolean> {
        private WeakReference<LoginActivity> activityReference;

        CheckEmailExistenceTask(LoginActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            LoginActivity activity = activityReference.get();
            if (activity == null) {
                return false;
            }

            String email = params[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            try {
                // Replace "YOUR_BACKEND_API_URL" with your actual API endpoint for checking email existence
                URL url = new URL(UrlConstants.CHECK_GOOGLE_EMAIL + email);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // If the response code is HTTP_OK (200), it means the email exists in the database
                    return true;
                } else {
                    // If the response code is not HTTP_OK, it means the email does not exist in the database
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle any network or API request errors here
                return false;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean isRegistered) {
            LoginActivity activity = activityReference.get();
            if (activity != null) {
                if (isRegistered) {
                    // User is registered, proceed to homepage using MySQL data
                    activity.navigateToHomePage();
                } else {
                    // User is not registered, redirect to the signup activity
                    activity.navigateToSignUpActivity();
                }
            }
        }
    }

    // Helper method to navigate to the signup activity with autofilled email
    private void navigateToSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {
            String displayName = acct.getDisplayName();
            String[] nameParts = displayName.split(" "); // Split the full name by space
            String firstName = "";
            String lastName = "";

            if (nameParts.length > 0) {
                lastName = nameParts[0]; // First part is the first name
                if (nameParts.length > 1) {
                    firstName = nameParts[nameParts.length - 2]; // Last part is the last name
                }
            }

            Log.e("HomeActivity", "First Name: " + firstName);
            Log.e("HomeActivity", "Last Name: " + lastName);
            Log.e("HomeActivity", "Email: " + acct.getEmail());
            Log.e("HomeActivity", "Name: " + acct.getDisplayName());
            Log.e("HomeActivity", "Given Name: " + acct.getGivenName());
            Log.e("HomeActivity", "Photo: " + acct.getPhotoUrl());
            Log.e("HomeActivity", "Photo: " + acct.getId());

            intent.putExtra("autofill_email", acct.getEmail());
            intent.putExtra("autofill_first_name", firstName);
            intent.putExtra("autofill_last_name", lastName);

            // Pass the profile photo URL
            if (acct.getPhotoUrl() != null) {
                intent.putExtra("autofill_profile_photo", acct.getPhotoUrl().toString());
            }
        }

        startActivity(intent);
    }


    // Helper method to navigate to the homepage using MySQL data
    private void navigateToHomePage() {
        // Implement the logic to fetch user data from MySQL and navigate to the homepage
        String email = continueToGoogleEmail;
        Log.e("LoginActivity", "navigateToHomePage - email: " + email);
        // Use Volley to make an HTTP request to your PHP script
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiUrl = UrlConstants.GET_EMAIL_DETAILS; // Replace with the actual URL of your PHP script

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the JSON response to extract user data
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.e("LoginActivity", "navigateToHomePage - jsonResponse: " + jsonResponse);
                            String user_id = jsonResponse.getString("user_id");
                            Log.e("LoginActivity", "navigateToHomePage - user_id: " + user_id);
                            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                            // Start the homepage activity and pass the user data
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_id", user_id);
                            editor.apply();
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error here
                        }
                        Log.e("LoginActivity", "navigateToHomePage - response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors (e.g., network issues, server errors) here
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Send the user's email as a parameter to your PHP script
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void validateEmail() {
        String email = textInputEditTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            textInputLayoutEmail.setError(getString(R.string.error_enter_email));
            isEmailValid = false;
        } else if (!isValidEmail(email)) {
            textInputLayoutEmail.setError(getString(R.string.error_enter_valid_email));
            isEmailValid = false;
        } else {
            textInputLayoutEmail.setError(null);
            textInputLayoutEmail.setErrorEnabled(false);
            isEmailValid = true;
        }

        enableSignUpButton();
    }

    private void enableSignUpButton() {
        boolean emailIsValid = isEmailValid;

        if (emailIsValid && isPasswordValid) {
            buttonLogin.setEnabled(true);
        } else {
            buttonLogin.setEnabled(false);
        }
    }

    private boolean isValidEmail(String email) {
        // Define a regular expression pattern that checks for either domain
        String emailPattern = "[a-zA-Z0-9._-]+@(gmail\\.com|dhvsu\\.edu\\.ph)";

        // Use the matches() method to check if the email matches the pattern
        return email.matches(emailPattern);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}