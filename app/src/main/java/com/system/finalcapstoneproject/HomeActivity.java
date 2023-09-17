package com.system.finalcapstoneproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.system.finalcapstoneproject.Adaptor.CategoryAdaptor;
import com.system.finalcapstoneproject.CreateTutorial.CreateTutorialActivity;
import com.system.finalcapstoneproject.CreateTutorial.TutorialActivity;
import com.system.finalcapstoneproject.Domain.CategoryDomain;
import com.system.finalcapstoneproject.reportingsystem.ReportActivity;

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
import java.util.ArrayList;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 300;
    private static final String CHANNEL_ID = "PlasticsInfoChannel";
    private static final String[] PLASTIC_TITLES = {"Did you know?", "Did you know?", "Did you know?", "Did you know?", "Did you know?", "Did you know?", "Did you know?", "Did you know?"};
    private static final String[] PLASTIC_NAMES = {"Polyethylene Terephthalate", "High Density Polyethylene", "Polyvinyl Chloride Plastic Type", "Low density polyethylene", "Polypropolene", "Polystyrene", "Burning of plastic", "Other plastics"};
    private static final String[] PLASTIC_TEXTS = {"PET is a plastic material used for making water bottles, soda bottles and food containers.", "HDPE is a plastic material used for making milk jugs, detergent bottles and plastic bags.", "PVC is a plastic material used for making pipes, flooring and window frames.", "LDPE is a plastic material used for making squeezable bottles, bread bags and shopping bags.", "PP is a plastic material used for making yogurt containers, margarine tubs and microwaveable food containers.", "PS is a plastic material used for making foam cups, meat trays and takeout containers.", "Burning plastic can harm your health, especially your lungs.", "Other types of plastics include polycarbonate, acrylic, nylon, and polystyrene."};
    private static final String[] PLASTIC_DESCRIPTIONS = {"It is a lightweight, strong, and durable material commonly used for packaging various products, particularly beverages and food items", "It is a strong and durable thermoplastic material that is widely used in the packaging industry for its excellent resistance to chemicals, impact, and moisture.", "It is a type of plastic used in various applications, including the production of bottles. PVC plastic bottles are made from a material composed of vinyl chloride monomers.", "They are a type of plastic containers commonly used for packaging liquids, such as beverages, cleaning products, and personal care items. LDPE is a lightweight and flexible polymer, known for its low density and high durability.", "They have a wide range of applications in different industries. They are often used for packaging liquids, such as beverages, household cleaners, and personal care products. The bottles are lightweight, transparent or translucent, and have good barrier properties, meaning they can effectively protect the contents from moisture, air, and other external factors.", "It is a versatile and lightweight material that is commonly used in the packaging industry. It is popular for its durability, transparency, and ability to insulate.", "In the Philippines, the law regarding the burning of plastic is primarily governed by the Republic Act No. 9003, also known as the Ecological Solid Waste Management Act of 2000. This law promotes proper waste management practices to protect public health and the environment.", "There are several types of plastic bottles commonly used for packaging various products. Here are a few examples:\n" + "\n" + "Polyethylene Terephthalate (PET) Bottles: PET bottles are lightweight, transparent, and durable. They are widely used for water, soda, and juice packaging. PET is a recyclable plastic and can be repurposed into new bottles or other products.\n" + "\n" + "High-Density Polyethylene (HDPE) Bottles: HDPE bottles are known for their sturdiness and resistance to chemicals. They are commonly used for packaging household cleaners, shampoos, and other personal care products. HDPE is also recyclable.\n" + "\n" + "Polypropylene (PP) Bottles: PP bottles are lightweight and have good heat resistance. They are often used for packaging food products, such as sauces, condiments, and pharmaceuticals. PP is recyclable but not as widely accepted for recycling as PET and HDPE.\n" + "\n" + "Low-Density Polyethylene (LDPE) Bottles: LDPE bottles are flexible and squeezeable. They are commonly used for packaging items like lotions, creams, and liquid soaps. LDPE is less commonly recycled, but some recycling programs accept it.\n" + "\n" + "Polycarbonate (PC) Bottles: PC bottles are known for their clarity and impact resistance. They are commonly used for baby bottles and reusable water bottles. PC can release harmful chemicals, so it's important to follow specific guidelines for their safe use and avoid heating them."};
    public static final int[] PLASTIC_IMAGES = {R.drawable.popup_pet, R.drawable.popup_hdpe, R.drawable.popup_pvc, R.drawable.popup_ldpe, R.drawable.popup_pp, R.drawable.popup_ps, R.drawable.popup_burning_plastic, R.drawable.popup_other_plastic};
    public TextView user_firstname;
    private CategoryAdaptor adapter;
    private RecyclerView recyclerViewCategoryList;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private LinearLayout reportBtn;
    private FloatingActionButton scannerBtn;
    private LinearLayout profileBtn;
    private LinearLayout homeBtn;
    private LinearLayout popup_button;
    private Button galleryBtn;
    private Button cameraBtn;
    private Button createTutorialBtn;
    private LinearLayout parent;
    private Button logoutButton;
    private static final String USER_SESSION = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        reportBtn = findViewById(R.id.report_button);
        scannerBtn = findViewById(R.id.camera_button);
        profileBtn = findViewById(R.id.profile_button);
        homeBtn = findViewById(R.id.home_button);
        galleryBtn = findViewById(R.id.gallery);
        popup_button = findViewById(R.id.popup_button);
        cameraBtn = findViewById(R.id.camera);
        createTutorialBtn = findViewById(R.id.createTutorialButton);
        parent = findViewById(R.id.mainLinearLayout);
        user_firstname = findViewById(R.id.firstname);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            user_firstname.setText(acct.getGivenName());
            Log.e("HomeActivity", "Name: " + acct.getDisplayName());
            Log.e("HomeActivity", "Email: " + acct.getEmail());
            Log.e("HomeActivity", "Given Name: " + acct.getGivenName());
            Log.e("HomeActivity", "Photo: " + acct.getPhotoUrl());
            Log.e("HomeActivity", "Photo: " + acct.getId());
        }

        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryBtn.setVisibility(View.VISIBLE);
                cameraBtn.setVisibility(View.VISIBLE);
                createTutorialBtn.setVisibility(View.VISIBLE);
                scannerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (galleryBtn.getVisibility() == View.VISIBLE && cameraBtn.getVisibility() == View.VISIBLE) {
                            galleryBtn.setVisibility(View.GONE);
                            cameraBtn.setVisibility(View.GONE);
                            createTutorialBtn.setVisibility(View.GONE);
                        } else {
                            galleryBtn.setVisibility(View.VISIBLE);
                            cameraBtn.setVisibility(View.VISIBLE);
                            createTutorialBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        galleryBtn.setVisibility(View.GONE);
                        cameraBtn.setVisibility(View.GONE);
                        createTutorialBtn.setVisibility(View.GONE);
                    }
                });

                galleryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, GALLERY_REQUEST_CODE);
                        }
                    }
                });

                cameraBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        } else {
                            Intent cameraIntent = new Intent(HomeActivity.this, CameraActivity.class);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        }
                    }
                });
            }
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        createTutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read the "notification_enabled" preference value from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                boolean notificationEnabled = sharedPreferences.getBoolean("notification_preference", false);
                Log.e("HomeActivity", "popup_button - notification_preference state: " + notificationEnabled);
                // Start the PopUpSettingsActivity and pass the notificationEnabled value
                Intent intent = new Intent(HomeActivity.this, PopUpSettingsActivity.class);
                startActivity(intent);
            }
        });

// The rest of your code remains the same.


        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean notificationPreferenceSet = sharedPreferences.getBoolean("notification_preference_set", false);

        if (!notificationPreferenceSet) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustomStyle);
            builder.setTitle("Notification Preference")
                    .setMessage("Do you want to receive notification popups?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Save the user's choice as "true" in SharedPreferences
                            saveNotificationPreference(true);
                            // Mark the preference as set
                            markNotificationPreferenceSet();
                            // Show the notification popup
                            showNotification();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Save the user's choice as "false" in SharedPreferences
                            saveNotificationPreference(false);
                            // Mark the preference as set
                            markNotificationPreferenceSet();
                        }
                    });
            builder.show();


        } else {
            // The preference has been set before, so you can directly check and show/dismiss notifications accordingly.
            boolean notificationPreference = sharedPreferences.getBoolean("notification_preference", false);
            if (notificationPreference) {
                // Show the notification popup
                // You can implement your own notification logic here
                showNotification();
            } else {

            }
        }

        retrieveUserDetails();
        showTipOfTheDay();
        recyclerViewCategoryList();
    }

    // Helper method to save notification preference
    private void saveNotificationPreference(boolean preference) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_preference", preference);
        editor.apply();
    }

    // Helper method to mark notification preference as set
    private void markNotificationPreferenceSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_preference_set", true);
        editor.apply();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUser();
                clearUserSession();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();


        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(getResources().getColor(R.color.logoutButtonTextColor));

        negativeButton.setTextColor(getResources().getColor(R.color.cancelButtonTextColor));
    }

    private void clearUserSession() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        // Cancel existing notifications (to prevent outdated notifications)
        cancelExistingNotifications();
        // User has turned off notifications, cancel the last notification
        cancelLastNotification();
    }

    // Example logout process
    private void logoutUser() {
        // Clear the session when the user logs out
        setLoggedIn(this, false);
    }

    // Function to set the user as logged in or out
    private void setLoggedIn(Context context, boolean isLoggedIn) {
        SharedPreferences prefs = context.getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    private void showTipOfTheDay() {
        Random random = new Random();
        int index = random.nextInt(PLASTIC_TITLES.length);

        TextView tipName = findViewById(R.id.tipName);
        TextView tipMessage = findViewById(R.id.tipMessage);
        TextView tipDescription = findViewById(R.id.tipDescription);
        ImageView tipImage = findViewById(R.id.tipImage);

        tipName.setText(PLASTIC_NAMES[index]);
        tipMessage.setText(PLASTIC_TEXTS[index]);
        tipImage.setImageResource(PLASTIC_IMAGES[index]);
        tipDescription.setText(PLASTIC_DESCRIPTIONS[index]);

        CardView cardView = findViewById(R.id.cardView);
        cardView.setRadius(getResources().getDimensionPixelSize(R.dimen.image_corner_radius));
    }


    private void recyclerViewCategoryList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryList = findViewById(R.id.recyclerView);
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryDomain> category = new ArrayList<>();
        category.add(new CategoryDomain("Scan", "scan"));
        category.add(new CategoryDomain("Report", "report"));
        category.add(new CategoryDomain("Popup", "popup"));

        adapter = new CategoryAdaptor(category);
        recyclerViewCategoryList.setAdapter(adapter);

        adapter.setOnItemClickListener(new CategoryAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CategoryDomain clickedItem = category.get(position);
                String categoryName = clickedItem.getTitle();
                int tutorialLayoutId = 0; // Resource ID for the tutorial layout

                // Determine the resource ID for the tutorial layout based on the selected category
                if ("Scan".equals(categoryName)) {
                    tutorialLayoutId = R.layout.tutorial_scan;
                } else if ("Report".equals(categoryName)) {
                    tutorialLayoutId = R.layout.tutorial_report;
                } else if ("Popup".equals(categoryName)) {
                    tutorialLayoutId = R.layout.tutorial_popup;
                }

                if (tutorialLayoutId != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    View dialogView = getLayoutInflater().inflate(tutorialLayoutId, null);

                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Set "Go to Feature" button click listener
                    Button buttonRedirect = dialogView.findViewById(R.id.buttonRedirect);
                    buttonRedirect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the redirection to the specific feature here
                            // For example, start a new activity or fragment that corresponds to the selected feature.
                            if ("Scan".equals(categoryName)) {
                                // Redirect to the Scan feature
                                startActivity(new Intent(HomeActivity.this, CameraActivity.class));
                            } else if ("Report".equals(categoryName)) {
                                // Redirect to the Report feature
                                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
                            } else if ("Popup".equals(categoryName)) {
                                // Redirect to the Popup feature
                                startActivity(new Intent(HomeActivity.this, PopUpSettingsActivity.class));
                            }

                            // Close the tutorial dialog
                            dialog.dismiss();
                        }
                    });
                } else {
                    // Handle the case where no tutorial layout is found for the selected category
                    Toast.makeText(getApplicationContext(), "No tutorial available for " + categoryName, Toast.LENGTH_SHORT).show();
                }
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
                    Log.e("HomeActivity", "retrieveUserDetails - User ID:" + user_id);
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
                        Log.e("HomeActivity", "retrieveUserDetails - Response:" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        String is_admin = jsonObject.getString("is_admin");
                        String firstname = jsonObject.getString("firstname");
                        String lastname = jsonObject.getString("lastname");
                        String email = jsonObject.getString("email");
                        String joined = jsonObject.getString("joined");
                        String sex = jsonObject.getString("gender");
                        String phone = jsonObject.getString("phone");
                        int profile_pic = jsonObject.getInt("tmp");
                        int ban_duration = jsonObject.getInt("ban_duration");
                        String ban_reason = jsonObject.getString("ban_reason");

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("is_admin", is_admin);
                        editor.putString("firstname", firstname);
                        editor.putString("lastname", lastname);
                        editor.putString("email", email);
                        editor.putString("joined", joined);
                        editor.putString("sex", sex);
                        editor.putString("phone", phone);
                        editor.putString("sex", sex);
                        editor.putInt("profile_pic", profile_pic);
                        editor.putInt("ban_duration", ban_duration);
                        editor.putString("ban_reason", ban_reason);
                        editor.apply();

                        user_firstname.setText(firstname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        task.execute();
    }

    int newInterval;

    private void showNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        newInterval = sharedPreferences.getInt("notification_interval", 1); // Default to 1 if not found

        // Cancel any existing pending intents for notifications
        cancelExistingNotifications();

        // Schedule the notification to repeat at the new interval
        scheduleNotification(newInterval);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("HomeActivity", "onResume - was called: ");
        // Check if the notification interval has been updated in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int storedInterval = sharedPreferences.getInt("notification_interval", 1); // Default to 1 if not found
        Log.e("HomeActivity", "onResume - SharedPreferences Time Interval: " + storedInterval);
        // Check if the stored interval is different from the current interval (e.g., from the settings)
        if (newInterval != storedInterval) {
            // Update the current interval (if needed)
            newInterval = storedInterval;

            // Cancel existing notifications (to prevent outdated notifications)
            cancelExistingNotifications();

            // Schedule notifications with the updated interval
            scheduleNotification(newInterval);
            Log.e("HomeActivity", "onResume - SharedPreferences New Time Interval: " + newInterval);
        }

        boolean notificationEnabled = sharedPreferences.getBoolean("notification_preference", false);
        Log.e("PopUpSettingsActivity", "onCreatePreferences - notification_enabled state: " + notificationEnabled);
        // Check if the stored interval is different from the current interval (e.g., from the settings)
        if (!notificationEnabled) {
            // Cancel existing notifications (to prevent outdated notifications)
            cancelExistingNotifications();
            // User has turned off notifications, cancel the last notification
            cancelLastNotification();
        } else {
            showNotification();
        }

    }


    private Handler handler = new Handler();
    private Runnable notificationRunnable;
    private int notificationId = 0; // Initialize a unique notification ID
    private void scheduleNotification(int intervalMinutes) {
        Log.e("HomeActivity", "scheduleNotification - Minutes: " + intervalMinutes);
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int index = random.nextInt(PLASTIC_TITLES.length);

                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                intent.putExtra("title", PLASTIC_TITLES[index]);
                intent.putExtra("text", PLASTIC_TEXTS[index]);
                intent.putExtra("image", PLASTIC_IMAGES[index]);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_logo_round)
                        .setContentTitle(PLASTIC_TITLES[index])
                        .setContentText(PLASTIC_TEXTS[index])
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), PLASTIC_IMAGES[index]))
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), PLASTIC_IMAGES[index])).bigLargeIcon(null));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "PlasticsInfo";
                    String description = "PlasticsInfo Notification";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                notificationManager.notify(notificationId, builder.build());

                handler.postDelayed(this, intervalMinutes * 60 * 1000); // Convert minutes to milliseconds
            }
        };

        // Schedule the first notification
        handler.postDelayed(notificationRunnable, intervalMinutes * 60 * 1000); // Convert minutes to milliseconds

        // Increment the notification ID to ensure uniqueness
        notificationId++;
    }

    private void cancelLastNotification() {
        // Use NotificationManager to cancel the last notification
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Assuming that you keep track of the last notificationId
        int lastNotificationId = notificationId;

        if (lastNotificationId != -1) {
            notificationManager.cancel(lastNotificationId);
        }
    }

    private void cancelExistingNotifications() {
        Log.e("HomeActivity", "cancelExistingNotifications - Notification was changed, Outdated popup settings are removed");

        // To cancel a notification, you need to provide its unique ID
        // Here, you can cancel notifications with IDs from 0 to (notificationId - 1)
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        for (int i = 0; i < notificationId; i++) {
            notificationManager.cancel(i);
        }
        // Cancel the existing notification timer (if it exists)
        cancelExistingNotificationTimer();
    }
    private void cancelExistingNotificationTimer() {
        if (handler != null && notificationRunnable != null) {
            handler.removeCallbacks(notificationRunnable);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    Log.d("Image:", "Image from gallery not empty");
                } else Log.d("Image:", "Image from gallery empty");

                Intent intent = new Intent(HomeActivity.this, ImageProcessingActivity.class);
                intent.putExtra("imageUri", selectedImageUri.toString());
                startActivity(intent);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                String imagePath = data.getStringExtra("image_path");
                if (imagePath != null) {
                    Log.d("Image:", "Image from camera not empty");
                } else Log.d("Image:", "Image from camera empty");

                Intent intent = new Intent(HomeActivity.this, ImageProcessingActivity.class);
                intent.putExtra("image_path", imagePath);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission denied, check if it is READ_EXTERNAL_STORAGE and request the permission again
                if (permissions.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }

            }
        }
    }

}
