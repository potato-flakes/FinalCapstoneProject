package com.system.finalcapstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);

        backButton = findViewById(R.id.back_toggle);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
        }


        TextView titleTextView = findViewById(R.id.title_text_view);
        TextView textTextView = findViewById(R.id.text_text_view);
        ImageView imageImageView = findViewById(R.id.image_image_view);


        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        int imageResource = intent.getIntExtra("image", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        titleTextView.setText(title);
        textTextView.setText(text);
        imageImageView.setImageResource(imageResource);


    }
    @Override
    public void onBackPressed() {

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("from_notification")) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            super.onBackPressed();
        }
    }



}
