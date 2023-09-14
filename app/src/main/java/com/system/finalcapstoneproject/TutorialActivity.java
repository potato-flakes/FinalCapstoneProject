package com.system.finalcapstoneproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TutorialActivity extends AppCompatActivity {
    LinearLayout stepsLayout;
    ImageButton back_toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity);
        stepsLayout = findViewById(R.id.steps_layout);
        back_toggle = findViewById(R.id.back_toggle);

        back_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final ScrollView scrollView = findViewById(R.id.scroll_view);


        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });

        String tutorial = getIntent().getStringExtra("tutorial");
        Toast.makeText(getApplicationContext(), "Clicked on " + tutorial, Toast.LENGTH_SHORT).show();
        if (tutorial.equals("Parol")) {

            ImageView recycledProductImageView = findViewById(R.id.recycled_product_imageview);
            recycledProductImageView.setImageResource(R.drawable.parol_image);


            TextView productTextViewName = findViewById(R.id.name_textview);
            String productName;
            productName = ("Lightning shimmering splendid");
            productTextViewName.setText(productName);

            TextView materialsTextView = findViewById(R.id.materials_textview);
            String Materials;
            Materials = ("- 10 Mountain Dew plastic bottles\n" +
                    "- Glue Gun"
            );
            materialsTextView.setText("Materials Needed: \n" + Materials);


            TextView timeTextView = findViewById(R.id.time_textview);
            String time;
            time = ("30 Minutes");
            timeTextView.setText("Estimated Time to Make: \n" + time);

            addStep("Step 1: Rinse the bottle", R.drawable.unnamed);
            addStep("Step 2: Halle", R.drawable.unnamed);
            addStep("Step 3: Halle", R.drawable.unnamed);
            addStep("Step 4: Halle", R.drawable.unnamed);


        } else if (tutorial.equals("Pencil Case")) {
            ImageView recycledProductImageView = findViewById(R.id.recycled_product_imageview);
            recycledProductImageView.setImageResource(R.drawable.pencilcase);

            TextView productTextViewName = findViewById(R.id.name_textview);
            String productName;
            productName = ("Mountain Dew Pencil Case");
            productTextViewName.setText(productName);

            TextView materialsTextView = findViewById(R.id.materials_textview);
            String Materials;
            Materials = ("- 2 Mountain Dew plastic bottles\n" +
                    "- Glue Gun\n" +
                    "- Scissors/Cutters\n" +
                    "- Zipper"
            );
            materialsTextView.setText(Materials);

            TextView timeTextView = findViewById(R.id.time_textview);
            String time;
            time = ("10 Minutes");
            timeTextView.setText(time);

            addStep("Step 1: Clean the bottle\n\n" +
                    " - Remove the plastic around the bottle and make sure to clean and remove all the dirt.", R.drawable.pencil_case_step_1);
            addStep("Step 2: Cut the bottle in half\n\n" +
                    " - Use scissors or cutters to cut the bottle. You can use a marker to mark the bottle and use it as your guide, you can also sand it using sandpapers to smoothen the edges.", R.drawable.pencil_case_step_2);
            addStep("Step 3: Put the zipper on the edges of both bottles and glue it\n\n" +
                    " - Put some glue around the bottle's edges and slowly put the zipper around it", R.drawable.pencil_case_step_3);
            addStep("Step 4: Combine the two bottles in one\n\n" +
                    " - Get the other bottle and also put some glue onto it and then slowly combine the two bottles", R.drawable.pencil_case_step_4);

            YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
            getLifecycle().addObserver(youTubePlayerView);

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = "qnCz6Z8werI";
                    youTubePlayer.cueVideo(videoId, 0);
                }
            });
        }
    }

    private void addStep(String stepText, int gifResource) {
        LinearLayout stepLayout = new LinearLayout(this);
        stepLayout.setOrientation(LinearLayout.VERTICAL);
        stepLayout.setPadding(0, 0, 0, 0);
        stepLayout.setDividerPadding(0);
        stepLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        TextView stepTextView = new TextView(this);
        stepTextView.setText(stepText);
        stepTextView.setTextSize(14);
        stepTextView.setTextColor(Color.rgb(0, 0, 0));
        stepTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        stepTextView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        LinearLayout.LayoutParams stepTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        stepTextParams.setMargins(0, 0, 0, 0);
        stepTextView.setLayoutParams(stepTextParams);

        GifImageView stepGifImageView = new GifImageView(this);
        stepGifImageView.setImageResource(gifResource);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, -150, 0, -150);
        stepGifImageView.setLayoutParams(layoutParams);

        GifDrawable gifDrawable = (GifDrawable) stepGifImageView.getDrawable();
        gifDrawable.stop();

        stepGifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GifDrawable gifDrawable = (GifDrawable) stepGifImageView.getDrawable();
                if (gifDrawable.isPlaying()) {
                    gifDrawable.stop();
                } else {
                    gifDrawable.start();
                }
            }
        });

        stepLayout.addView(stepTextView);
        stepLayout.addView(stepGifImageView);

        stepsLayout.addView(stepLayout);
    }



}
