package com.system.finalcapstoneproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    TextInputEditText first_name, last_name, email_address, passwordEditText;
    private Button btnSave;
    private ImageView backButton;
    private String UPDATEPROFILE_URL = UrlConstants.UPDATEPROFILE_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        first_name = findViewById(R.id.firstName);
        last_name = findViewById(R.id.lastName);
        email_address = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        btnSave = findViewById(R.id.buttonSignUp);

        backButton = findViewById(R.id.back_toggle);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish(); // Optional: finish the current activity to prevent going back to it using the back button
                }
            });
        }
        // Retrieve user details from the server
        retrieveUserDetails();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the updated user details to the server
                saveUserDetails();
                Toast.makeText(getApplicationContext(), "Details saved.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                Log.e("Login button", "is clicked");
            }
        });
    }

    private void saveUserDetails() {
        // Retrieving the userId
        SharedPreferences sharedPreferences = getSharedPreferences("userId", Context.MODE_PRIVATE);

        // Get the updated user details from the EditText fields
        String firstName = Objects.requireNonNull(first_name.getText()).toString();
        String lastName = Objects.requireNonNull(last_name.getText()).toString();
        String email = Objects.requireNonNull(email_address.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String userIds = sharedPreferences.getString("userId", "");
        Log.e("Inputs:", firstName);
        // Make a POST request to the PHP script using an AsyncTask
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    // Pass the updated user details to the PHP script
                    String postData = "firstname=" + URLEncoder.encode(params[1], "UTF-8") +
                            "&lastname=" + URLEncoder.encode(params[2], "UTF-8") +
                            "&email=" + URLEncoder.encode(params[3], "UTF-8") +
                            "&password=" + URLEncoder.encode(params[4], "UTF-8") +
                            "&userId=" + URLEncoder.encode(params[5], "UTF-8");
                    Log.e("PostData:", postData);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postData.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    // Read the response from the PHP script
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    return stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String response) {
                // Handle the response from the PHP script
                if (response != null) {
                    Log.e("Response",response);
                }
            }
        };
        task.execute(UPDATEPROFILE_URL, firstName, lastName, email, password, userIds);
    }

    private void retrieveUserDetails() {
        // Retrieve user details from the server and populate the EditText fields
        // You can reuse the same `retrieveUserDetails()` method from the `ProfileActivity` or modify it as needed
        // Make a POST request to the PHP script using an AsyncTask
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(UrlConstants.PROFILE_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    // Retrieving the userId
                    SharedPreferences sharedPreferences = getSharedPreferences("userId", Context.MODE_PRIVATE);
                    String userIds = sharedPreferences.getString("userId", "");

                    String postData = "email=" + URLEncoder.encode(userIds, "UTF-8");
                    Log.e("Post Data:",postData);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postData.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    // Read the response from the PHP script
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    return stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String response) {
                // Handle the response and update the UI
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String firstname = jsonObject.getString("firstname");
                        String lastname = jsonObject.getString("lastname");
                        String email = jsonObject.getString("email");

                        first_name.setText(firstname);
                        last_name.setText(lastname);
                        email_address.setText(email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        task.execute(); // Execute the AsyncTask
    }
}
