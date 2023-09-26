package com.system.finalcapstoneproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.system.finalcapstoneproject.AdminScannerActivity.AdminQRScanActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null){
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
                }else if (!isValidEmail(email)) {
                    textInputLayoutEmail.requestFocus();
                    isEmailValid = false;
                    return;
                }else {
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
                }else {
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
                                        // User email and password are correct
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
                                                            JSONObject jsonResponse = new JSONObject(response);
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
                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            String access_level = jsonResponse.getString("access_level");
                                                            Log.e("LoginActivity", "buttonLogin - access_level: " + access_level);
                                                            if ("reportadmin".equals(access_level)) {
                                                                // Redirect to the QR scanner activity
                                                                Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
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
        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
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
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}