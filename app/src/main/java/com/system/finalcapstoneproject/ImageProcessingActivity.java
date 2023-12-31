package com.system.finalcapstoneproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.system.finalcapstoneproject.CreateTutorial.CreateTutorialActivity;
import com.system.finalcapstoneproject.CreateTutorial.DataSetTutorialAdapter;
import com.system.finalcapstoneproject.CreateTutorial.Tutorial;
import com.system.finalcapstoneproject.CreateTutorial.TutorialActivity;
import com.system.finalcapstoneproject.CreateTutorial.TutorialAdapter;
import com.system.finalcapstoneproject.CreateTutorial.TutorialDetailActivity;
import com.system.finalcapstoneproject.ml.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageProcessingActivity extends AppCompatActivity {
    TextView result;
    ImageView imageView, backBtn;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private List<Tutorial> tutorialList;
    private DataSetTutorialAdapter dataSetTutorialAdapter;
    private RecyclerView recyclerView;
    private Bitmap passDatasetImage;
    private String dataset_target_dir;
    private LinearLayout noTutorialLayout;
    private Button createTutorialButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tutorialList = new ArrayList<>();
        dataSetTutorialAdapter = new DataSetTutorialAdapter(this, tutorialList);
        recyclerView.setAdapter(dataSetTutorialAdapter);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        backBtn = findViewById(R.id.backButton);
        noTutorialLayout = findViewById(R.id.noTutorialLayout);
        createTutorialButton = findViewById(R.id.createTutorialButton);

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the tutorial list based on the search query
                filterTutorials(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });

        createTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(ImageProcessingActivity.this, CreateTutorialActivity.class);
              startActivity(intent);
              finish();
            }
        });
        dataSetTutorialAdapter.setOnItemClickListener(new DataSetTutorialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Get the report at the clicked position
                Tutorial tutorial = tutorialList.get(position);

                // Extract the report_id from the report object
                String tutorialId = tutorial.getTutorialId();
                // Create an Intent to start the detail activity
                Intent intent = new Intent(ImageProcessingActivity.this, TutorialDetailActivity.class);
                // Pass tutorial data as extras
                intent.putExtra("tutorial_id", tutorialId);
                // Start the detail activity
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestCameraPermission();
        requestExternalStoragePermission();

        // Get data passed from the CameraFragment
        Intent intent = getIntent();
        if (intent != null) {
            byte[] byteArray = intent.getByteArrayExtra("image");
            String detectedClass = intent.getStringExtra("detectedClass");
            float confidenceScore = intent.getFloatExtra("confidenceScore", 0.0f);
            int imageRotation = intent.getIntExtra("imageRotation", 0);
            // Convert byte array back to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            // Rotate the bitmap based on the image rotation
            if (imageRotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(imageRotation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            // Display the detected object information
            displayDetectedObject(detectedClass, bitmap);
        }
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                passDatasetImage = image;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);


        }
    }

    private void filterTutorials(String query) {
        List<Tutorial> filteredTutorials = new ArrayList<>();

        for (Tutorial tutorial : tutorialList) {
            if (tutorial.getTutorialTitle().toLowerCase().contains(query.toLowerCase()) ||
                    tutorial.getDifficultyLevel().toLowerCase().contains(query.toLowerCase()) ||
                    tutorial.getTutorialStatus() .toLowerCase().contains(query.toLowerCase()) ||
                    tutorial.getTutorialCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredTutorials.add(tutorial);
            }
        }

        // Update the RecyclerView with the filtered tutorials
        dataSetTutorialAdapter.filterList(filteredTutorials);
    }


    private void sendDataSet() throws ParseException {
        String tutorialHeaderImage = null;
        ByteArrayOutputStream byteArrayOutputStream;
        byteArrayOutputStream = new ByteArrayOutputStream();
        if (passDatasetImage != null) {
            passDatasetImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            tutorialHeaderImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        }

        String finalTutorialHeaderImage = tutorialHeaderImage;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Declare response variable outside the try-catch block
                String response = "";
                try {
                    // Create JSON object with tutorial data
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tutorial_header_image", finalTutorialHeaderImage);
                    jsonObject.put("dataset_target_dir", dataset_target_dir);

                    Log.d("MainActivity", "reportCrime - Data to be passed: " + dataset_target_dir);

                    // Send the data to the PHP API and get the response
                    URL url = new URL(UrlConstants.UPLOAD_DATASET);
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
                        Log.d("MainActivity", "reportCrime - JSON Response - Data upload: " + jsonResponse);
                        // Extract the report ID from the response
                        String message = jsonResponse.getString("message");
                        Log.d("MainActivity", "reportCrime - Retrieved Message from server: " + message);

                        // Inside your Step2Fragment
                        Handler handler = new Handler(Looper.getMainLooper());

                        // To run code on the UI thread, use the handler like this:
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    } else {
                    }

                } catch (IOException | JSONException e) {
                    // Log the response string for debugging
                    Log.e("YourTag", "Response String: " + response); // Use the declared response variable
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void displayDetectedObject(String predictedClass, Bitmap datasetImage) {
        passDatasetImage = datasetImage;
        imageView.setImageBitmap(datasetImage);
        if(predictedClass.contains("Wilkins")){
            result.setText("Wilkins");
        } else if(predictedClass.contains("Coca Cola")){
            result.setText("Coca Cola");
        } else if(predictedClass.contains("C2")){
            result.setText("C2");
        } else if(predictedClass.contains("7UP")){
            result.setText("7Up");
        } else if(predictedClass.contains("Sprite")){
            result.setText("Sprite");
        } else if(predictedClass.contains("Royal")){
            result.setText("Royal");
        } else if(predictedClass.contains("Nature's Spring")){
            result.setText("Nature's Spring");
        } else if(predictedClass.contains("Le Minerale")){
            result.setText("Le Minerale");
        } else if(predictedClass.contains("Datu Puti Patis")){
            result.setText("Datu Puti Patis");
        } else if(predictedClass.contains("Sting")){
            result.setText("Sting");
        } else if(predictedClass.contains("Mountain Dew")){
            result.setText("Mountain Dew");
        } else if(predictedClass.contains("Absolute 1L")){
            result.setText("Royal");
        } else if(predictedClass.contains("Absolute 8L")){
            result.setText("Absolute");
        } else if(predictedClass.contains("Summit")){
            result.setText("Summit");
        }

        fetchTutorials(predictedClass);

        if (predictedClass.equals("Wilkins 7L")) {
            dataset_target_dir = "Wilkins/";
        } else if (predictedClass.equals("Coca Cola 1.5L")) {
            dataset_target_dir = "Cocacola/";
        } else if (predictedClass.equals("C2 1000ml")) {
            dataset_target_dir = "C2/";
        } else if (predictedClass.equals("7Up 1.25L")) {
            dataset_target_dir = "7up/";
        } else if (predictedClass.equals("Sprite 1.5L")) {
            dataset_target_dir = "Sprite/";
        } else if (predictedClass.equals("Royal 1.5L")) {
            dataset_target_dir = "Royal/";
        } else if (predictedClass.equals("Nature's Spring 10L")) {
            dataset_target_dir = "Natures Spring/";
        } else if (predictedClass.equals("Nature's Spring 500ml")) {
            dataset_target_dir = "Natures Spring/";
        } else if (predictedClass.equals("Datu Puti Patis 350ml")) {
            dataset_target_dir = "Datu Puti Patis/";
        } else if (predictedClass.equals("Le Minerale 600ml")) {
            dataset_target_dir = "Le Minerale/";
        } else if (predictedClass.equals("C2 Solo 230ml")) {
            dataset_target_dir = "C2/";
        } else if (predictedClass.equals("Sting 300ml")) {
            dataset_target_dir = "Sting/";
        } else if (predictedClass.equals("Mountain Dew 290ml")) {
            dataset_target_dir = "Mountain Dew/";
        } else if (predictedClass.equals("Royal  250ml")) {
            dataset_target_dir = "Royal/";
        } else if (predictedClass.equals("Absolute 1000ml")) {
            dataset_target_dir = "Absolute/";
        } else if (predictedClass.equals("Absolute 8L")) {
            dataset_target_dir = "Absolute/";
        }

        try {
            sendDataSet();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d("Permission:", "Permission granted");
            } else {
                Toast.makeText(this, "Accept permission to continue using Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchTutorials(final String predictedClass) {
        String newpredictedClass = null;
        if (predictedClass.equals("Wilkins 7L")) {
            newpredictedClass = "Wilkins";
        } else if (predictedClass.equals("Coca Cola 1.5L")) {
            newpredictedClass = "Coca Cola";
        } else if (predictedClass.equals("C2 1000ml")) {
            newpredictedClass = "C2";
        } else if (predictedClass.equals("7Up 1.25L")) {
            newpredictedClass = "7Up";
        } else if (predictedClass.equals("Sprite 1.5L")) {
            newpredictedClass = "Sprite";
        } else if (predictedClass.equals("Royal 1.5L")) {
            newpredictedClass = "Royal";
        } else if (predictedClass.equals("Nature's Spring 10L")) {
            newpredictedClass = "natures_spring_10L_folder/";
        } else if (predictedClass.equals("Nature's Spring 500ml")) {
            newpredictedClass = "Nature's Spring";
        } else if (predictedClass.equals("Datu Puti Patis 350ml")) {
            newpredictedClass = "Datu Puti Patis";
        } else if (predictedClass.equals("Le Minerale 600ml")) {
            newpredictedClass = "Le Minerale";
        } else if (predictedClass.equals("C2 Solo 230ml")) {
            newpredictedClass = "C2 Solo";
        } else if (predictedClass.equals("Sting 300ml")) {
            newpredictedClass = "Sting";
        } else if (predictedClass.equals("Mountain Dew 290ml")) {
            newpredictedClass = "Mountain Dew";
        } else if (predictedClass.equals("Royal  250ml")) {
            newpredictedClass = "Royal";
        } else if (predictedClass.equals("Absolute 1000ml")) {
            newpredictedClass = "Absolute";
        } else if (predictedClass.equals("Absolute 8L")) {
            newpredictedClass = "Absolute";
        }

        String finalNewpredictedClass = newpredictedClass;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = "";
                try {
                    // Append the status parameter to the URL
                    URL url = new URL(UrlConstants.GET_SPECIFIC_TUTORIAL + finalNewpredictedClass);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    Log.e("", "finalNewpredictedClass" + finalNewpredictedClass);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Success response (HTTP 200 OK)
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        reader.close();
                        response = responseBuilder.toString();
                        inputStream.close();
                    } else {
                        // Error response (e.g., HTTP 404, 500, etc.)
                        InputStream errorStream = connection.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                            StringBuilder errorResponseBuilder = new StringBuilder();
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                errorResponseBuilder.append(errorLine);
                            }
                            errorReader.close();
                            response = errorResponseBuilder.toString();
                            errorStream.close();
                        }
                    }

                    connection.disconnect();

                    final String jsonResponse = response.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseReportResponse(jsonResponse);
                        }
                    });
                } catch (IOException e) {
                    Log.e("ReportActivty", "fetchReports - Response String: " + response);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseReportResponse(String response) {
        // Clear the existing report list before parsing the new response
        tutorialList.clear();
        noTutorialLayout.setVisibility(View.GONE);
        Log.e("", "" + response);
        try {
            JSONArray jsonArray = new JSONArray(response);
            Log.e("ReportActivty", "parseReportResponse - Response String: " + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String tutorialStatus = jsonObject.getString("TutorialStatus"); // Extract the tutorial status
                if ("Approved".equals(tutorialStatus)) {
                    String tutorial_header_image = jsonObject.getString("tutorial_header_image");
                    String tutorial_id = jsonObject.getString("TutorialID"); // Correctly extract the tutorial_id
                    String user_id = jsonObject.getString("user_id"); // Use "id" instead of "reportId"
                    String TutorialTitle = jsonObject.getString("TutorialTitle");
                    String TutorialDescription = jsonObject.getString("TutorialDescription");
                    String TutorialCategory = jsonObject.getString("TutorialCategory");
                    String DifficultyLevel = jsonObject.getString("DifficultyLevel");
                    String EstimatedTime = jsonObject.getString("EstimatedTime");
                    String Timestamp = jsonObject.getString("Timestamp");
                    String user_name = jsonObject.getString("user_name");

                    String headerImageUrl = UrlConstants.GET_TUTORIAL_HEADER_IMAGES + tutorial_header_image; // Modify the URL as per your server setup

                    Tutorial tutorial = new Tutorial(headerImageUrl, tutorial_id, user_id, TutorialTitle, TutorialDescription, TutorialCategory, DifficultyLevel, EstimatedTime,
                            Timestamp, user_name);

                    tutorialList.add(tutorial);
                }
            }

            // Reverse the order of the reportList to display newest at the top
            Collections.reverse(tutorialList);
            if (tutorialList.isEmpty()){
                noTutorialLayout.setVisibility(View.VISIBLE);
            }
            dataSetTutorialAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}