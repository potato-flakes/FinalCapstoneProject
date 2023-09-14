package com.system.finalcapstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class PopUpSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
    public void onSaveButtonClicked(View view) {
        // Implement your save functionality here
        // For example, save preferences

        // Finish the activity to close it
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SwitchPreferenceCompat notificationEnabledPreference;
        private ListPreference notificationIntervalPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.notification_preferences, rootKey);

            // Initialize references to preferences
            notificationEnabledPreference = findPreference("notification_preference");
            notificationIntervalPreference = findPreference("notification_interval");

            // Retrieve the notification_enabled value from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            boolean notificationEnabled = sharedPreferences.getBoolean("notification_preference", false);
            Log.e("PopUpSettingsActivity", "onCreatePreferences - notification_enabled state: " + notificationEnabled);

            // Set up a listener for the "notification_enabled" preference
            if (notificationEnabledPreference != null) {
                notificationEnabledPreference.setChecked(notificationEnabled);
                notificationEnabledPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        // When the "notification_enabled" preference changes, save the new value
                        boolean enabled = (boolean) newValue;
                        saveNotificationEnabled(requireContext(), enabled);

                        // Enable or disable the "Notification Interval" preference
                        if (notificationIntervalPreference != null) {
                            notificationIntervalPreference.setEnabled(enabled);
                        }
                        return true; // Allow the preference change
                    }
                });
            }

            // Always set the initial state of the "Notification Interval" preference
            if (notificationIntervalPreference != null) {
                notificationIntervalPreference.setEnabled(notificationEnabled);
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            // Register a listener for preference changes
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener when the fragment is paused
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("notification_interval")) {
                // Handle changes to the notification interval setting
                String selectedIntervalValue = sharedPreferences.getString(key, "1"); // This will give you the selected value ("1", "5", "15", etc.)
                saveNotificationInterval(requireContext(), selectedIntervalValue); // Pass the Context
                // You can now use the selectedIntervalValue as needed
                Log.e("PopUpSettingsActivity", "showNotification - onSharedPreferenceChanged: " + selectedIntervalValue);
            }
        }

    }

    public static void saveNotificationInterval(Context context, String interval) {
        // Convert the interval String to an int
        int intervalValue = Integer.parseInt(interval);

        // Now, you have the intervalValue as an int
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notification_interval", intervalValue);
        editor.apply();
        Log.e("PopUpSettingsActivity", "showNotification - saveNotificationInterval: " + intervalValue);
    }


    public static void saveNotificationEnabled(Context context, boolean enabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification_preference", enabled);
        editor.apply();
        Log.e("PopUpSettingsActivity", "saveNotificationEnabled: " + enabled);
    }
}
