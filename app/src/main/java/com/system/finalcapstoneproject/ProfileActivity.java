package com.system.finalcapstoneproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    TextInputEditText first_name, last_name, email_address, passwordEditText, sex;
    private Button btnEdit;
    private ImageView backButton;
    private String url = UrlConstants.LOGIN_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        first_name = findViewById(R.id.firstName);
        last_name = findViewById(R.id.lastName);
        email_address = findViewById(R.id.email);
        sex = findViewById(R.id.sex);
        passwordEditText = findViewById(R.id.password);
        btnEdit = findViewById(R.id.buttonSignUp);

        backButton = findViewById(R.id.back_toggle);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });

        retrieveUserDetails();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordVerificationDialog();
            }
        });
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
                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                    String user_id = sharedPreferences.getString("user_id", "");
                    Log.e("ProfileActivity","retrieveUserDetails - doInBackground - Retrieved Data From SharedPreferences: " + user_id);
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
                        String password = jsonObject.getString("password");

                        Log.e("ProfileActivity","onPostExecute - Retrieved Data:" + jsonObject);
                        Log.e("ProfileActivity","onPostExecute - Retrieved firstname:" + firstname);
                        Log.e("ProfileActivity","onPostExecute - Retrieved firstname:" + lastname);
                        Log.e("ProfileActivity","onPostExecute - Retrieved firstname:" + email);
                        Log.e("ProfileActivity","onPostExecute - Retrieved firstname:" + password);

                        first_name.setText(firstname);
                        last_name.setText(lastname);
                        email_address.setText(email);
                        sex.setText(user_sex);
                        passwordEditText.setText(password);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ProfileActivity","onPostExecute - Error Retrieving Data:" + response);
                    }
                }
            }
        };

        task.execute();
    }

    private void showPasswordVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_verification, null);
        builder.setView(dialogView);

        TextInputEditText passwordVerificationInput = dialogView.findViewById(R.id.password_verification_input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String email = Objects.requireNonNull(email_address.getText()).toString().trim();
            String passwordVerification = Objects.requireNonNull(passwordVerificationInput.getText()).toString().trim();
            Log.e("My inputs:", email);
            Log.e("My inputs:", passwordVerification);
            if (!passwordVerification.isEmpty()) {

                RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {

                            if (response.equals("Login Success")) {

                                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                                startActivity(intent);
                            } else {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    String loginStatus = jsonResponse.getString("login");
                                    if (loginStatus.equals("Login Success")) {

                                        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {

                                        Toast.makeText(getApplicationContext(), loginStatus, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {

                                    Toast.makeText(getApplicationContext(), "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        error -> {

                            Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("password", passwordVerification);
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            } else {
                Toast.makeText(ProfileActivity.this, "Please enter the password verification", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
