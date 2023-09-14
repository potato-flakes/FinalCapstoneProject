package com.system.finalcapstoneproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.system.finalcapstoneproject.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessingActivity extends AppCompatActivity {

    TextView result, confidence, tutorial;
    ImageView imageView, backBtn;
    int imageSize = 224;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);

        tutorial = findViewById(R.id.tutorial);
        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        backBtn = findViewById(R.id.back_toggle);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(ImageProcessingActivity.this, HomeActivity.class);
               startActivity(intent);
               finish();
            }
        });

        requestCameraPermission();
        requestExternalStoragePermission();


        String imageUriString = getIntent().getStringExtra("imageUri");
        String imagePath = getIntent().getStringExtra("image_path");


        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (imageUriString != null){
            Uri imageUri = Uri.parse(imageUriString);
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }else if (bitmap != null){
            int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
            imageView.setImageBitmap(bitmap);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, false);

            classifyImage(resizedBitmap);
        }

    }

    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());


            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());


            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());


            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);


            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            if (maxConfidence < 0.9) {
                result.setText("Invalid object");
                confidence.setText("");
            } else {
                String[] classes = {"Wilkins 7L", "Coca Cola 1.5L", "C2 1000ml", "7Up 1.25L", "Sprite 1.5L", "Royal 1.5L", "Nature's Spring 10L", "Nature's Spring 500ml", "Datu Puti Patis 350ml", "Le Minerale 600ml", "C2 Solo 230ml", "Sting 300ml", "Mountain Dew 290ml", "Royal  250ml", "Absolute 1000ml"};
                String predictedClass = classes[maxPos];
                result.setText(predictedClass);

                String s = "";
                float predictedConfidence = maxConfidence * 100;
                String predictedText = String.format("%.1f%%", predictedConfidence, predictedClass);
                confidence.setText(predictedText);


                List<String> tutorials = recommendTutorials(predictedClass);
                if (!tutorials.isEmpty()) {

                    LinearLayout tutorialsContainer = findViewById(R.id.tutorials_container);
                    tutorialsContainer.removeAllViews();


                    for (final String tutorial : tutorials) {

                        LinearLayout tutorialContainer = new LinearLayout(this);
                        tutorialContainer.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tutorialContainer.setOrientation(LinearLayout.HORIZONTAL);

                        tutorialContainer.setBackground(getResources().getDrawable(R.drawable.border));
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tutorialContainer.getLayoutParams();
                        params.setMargins(0, 0, 0, dpToPx(16));
                        tutorialContainer.setLayoutParams(params);

                        int paddingStart = dpToPx(16);
                        int paddingTop = dpToPx(8);
                        int paddingEnd = dpToPx(16);
                        int paddingBottom = dpToPx(8);


                        tutorialContainer.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);


                        LinearLayout tutorialContentLayout = new LinearLayout(this);
                        tutorialContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tutorialContentLayout.setOrientation(LinearLayout.VERTICAL);


                        TextView tutorialTextView = new TextView(this);
                        tutorialTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tutorialTextView.setText(tutorial);
                        tutorialTextView.setTextColor(getResources().getColor(R.color.black));
                        tutorialTextView.setTextSize(18);


                        Typeface font = Typeface.createFromAsset(getAssets(), "font/poppinsmedium.ttf");
                        tutorialTextView.setTypeface(font);
                        tutorialTextView.setPadding(20, 20, 20, 0);


                        tutorialContentLayout.addView(tutorialTextView);

                        List<String> descriptions = getDescription(tutorial);

                        StringBuilder descriptionBuilder = new StringBuilder();
                        for (String description : descriptions) {
                            descriptionBuilder.append(description).append("\n");
                        }
                        String descriptionText = descriptionBuilder.toString();


                        TextView additionalTextView = new TextView(this);
                        additionalTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        additionalTextView.setText(descriptionText); // Use the 'description' variable
                        additionalTextView.setTextColor(getResources().getColor(R.color.black));
                        additionalTextView.setTextSize(10);
                        additionalTextView.setTypeface(font);
                        additionalTextView.setPadding(20, 0, 15, 20);


                        tutorialContentLayout.addView(additionalTextView);

                        int[] tutorialImages = {R.drawable.parol_image, R.drawable.pencil_case, R.drawable.placeholder_image}; // Add your image resources here


                        int desiredWidth = 200;
                        int desiredHeight = 200;


                        int tutorialImageResource = tutorialImages[tutorials.indexOf(tutorial)];


                        ImageView tutorialImageView = new ImageView(this);
                        tutorialImageView.setLayoutParams(new LinearLayout.LayoutParams(
                                desiredWidth,
                                desiredHeight));
                        tutorialImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        tutorialImageView.setAdjustViewBounds(true);
                        tutorialImageView.setImageResource(tutorialImageResource);


                        tutorialContainer.addView(tutorialImageView);


                        tutorialContainer.addView(tutorialContentLayout);
                        tutorialsContainer.addView(tutorialContainer);


                        tutorialContainer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Toast.makeText(getApplicationContext(), "Clicked on " + tutorial, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ImageProcessingActivity.this, TutorialActivity.class);
                                intent.putExtra("tutorial", tutorial);

                                startActivity(intent);
                            }
                        });
                    }


                }
                else Log.d("Predicted class:", "No predicted class");
            }


            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    private List<String> recommendTutorials(String predictedClass) {
        List<String> tutorials = new ArrayList<>();
        if (predictedClass.equals("Coca Cola 1.5L")) {
            tutorials.add("How to recycle plastic bottles");
            tutorials.add("5 creative ways to reuse plastic bottles");
        } else if (predictedClass.equals("C2 1000ml")) {
            tutorials.add("Recycling tips for plastic bottles");
            tutorials.add("DIY plastic bottle bird feeder");
        } else if (predictedClass.equals("7Up 1.25L")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Royal 250ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Sprite 1.5L")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Royal 1.5L")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Nature's Spring 10L")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Wilkins 7L")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Nature's Spring 500ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Datu Puti Patis 350ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Coca Cola Solo 190ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Royal Solo 190ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Le Minerale 600ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("C2 Solo 230ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Coca Cola ZERO 195ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Sting 300ml")) {
            tutorials.add("Recycling do's and don'ts for plastic bottles");
            tutorials.add("How to make a plastic bottle terrarium");
        } else if (predictedClass.equals("Mountain Dew 290ml")) {
            tutorials.add("Parol");
            tutorials.add("Pencil Case");
            tutorials.add("Broom");
        }

        return tutorials;
    }
    private List<String> getDescription(String tutorialType) {
        List<String> descriptions = new ArrayList<>();

        if (tutorialType.equals("Parol")) {
            descriptions.add("What is it?:\n" +
                    "- Parol is a traditional Christmas lantern usually shaped like stars or lanterns to symbolize light and hope during the Christmas season.");
        } else if (tutorialType.equals("Pencil Case")) {
            descriptions.add("What is it?:\n" +
                    "- Pencil Case is a creative way to repurpose the bottle and turn it into a container for holding pencils and other stationery items.");
        } else if (tutorialType.equals("Broom")) {
            descriptions.add("What is it?:\n" +
                    "- Pencil Case is a creative way to repurpose the bottle and turn it into a container for holding pencils and other stationery items.");
        }

        return descriptions;
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
        }
    }
    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
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

}