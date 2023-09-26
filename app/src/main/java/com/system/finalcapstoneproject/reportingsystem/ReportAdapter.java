package com.system.finalcapstoneproject.reportingsystem;
// ReportAdapter.java

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.system.finalcapstoneproject.BuildConfig;
import com.system.finalcapstoneproject.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private OnItemClickListener itemClickListener;
    private OnEditClickListener editClickListener;
    private Context context; // Add a context variable

    // Define an interface for delete button click listener
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private OnDeleteClickListener onDeleteClickListener;

    // Setter for delete button click listener
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    // Modify the constructor to accept a Context parameter
    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context; // Store the context
        this.reportList = reportList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reporting_report_item, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.descriptionTextView.setText(report.getCrime_type());
        holder.locationTextView.setText(report.getCrime_location());
        holder.dateTextView.setText(report.getCrime_date());
        holder.timeTextView.setText(report.getCrime_time());
        holder.reportStatusTextView.setText(report.getReportStatus());

        // Check if the report status is "Approved" to show/hide buttons
        if (report.getReportStatus().equals("Approved")) {
            holder.claimRewardButton.setVisibility(View.VISIBLE);
            holder.menuButton.setVisibility(View.GONE); // Hide the menuButton for approved reports
            // Check if the report status is "Approved" to show/hide buttons
            if (report.getReward_claimed().equals("1")) {
                holder.claimRewardButton.setEnabled(false);
                holder.claimRewardButton.setText("Reward Claimed");
            } else if (report.getReward_claimed().equals("2")){
                holder.claimRewardButton.setEnabled(false);
                holder.claimRewardButton.setText("Reward Aborted");
            } else {
                holder.claimRewardButton.setEnabled(true);
                holder.claimRewardButton.setText("Claim Reward");
            }
        } else {
            holder.claimRewardButton.setVisibility(View.GONE);
            holder.menuButton.setVisibility(View.VISIBLE); // Show the menuButton for non-approved reports
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public interface OnMenuClickListener {
        void onMenuClick(int position);
    }

    private OnMenuClickListener onMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.onMenuClickListener = listener;
    }


    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportIdTextView;
        TextView descriptionTextView;
        TextView locationTextView;
        TextView dateTextView;
        TextView timeTextView;
        Button editButton;
        ImageView menuButton;
        Button claimRewardButton;
        TextView reportStatusTextView;

        public ReportViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.TutorialTitleTextView);
            locationTextView = itemView.findViewById(R.id.TutorialDescriptionTextView);
            dateTextView = itemView.findViewById(R.id.TutorialCategoryTextView);
            timeTextView = itemView.findViewById(R.id.DifficultyLevelTextView);
            reportStatusTextView = itemView.findViewById(R.id.reportStatusTextView);
            claimRewardButton = itemView.findViewById(R.id.claimRewardButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(position);
                        }
                    }
                }
            });

            menuButton = itemView.findViewById(R.id.menuButton);

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Show the popup menu
                            showPopupMenu(menuButton, position);
                        }
                    }
                }
            });
            claimRewardButton = itemView.findViewById(R.id.claimRewardButton);
            claimRewardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Report report = reportList.get(position);
                        // Create QR code data with report details, user information, and reward money
                        String qrCodeData = createQRCodeData(report);

                        // Generate QR code bitmap
                        Bitmap qrCodeBitmap = generateQRCode(qrCodeData);

                        // Display the QR code to the user (you can customize this part)
                        showQRCodeDialog(qrCodeBitmap);
                    }
                }
            });
        }

        private void showPopupMenu(View view, final int position) {
            // Create a PopupMenu and set its style
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.END);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupMenu = new PopupMenu(view.getContext(), view, Gravity.END, 0, R.style.PopupMenuStyle);
            } else {
                popupMenu = new PopupMenu(view.getContext(), view);
            }

            // Inflate the menu XML resource
            popupMenu.getMenuInflater().inflate(R.menu.reporting_option_popup_menu, popupMenu.getMenu());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true);
            }

            // Set click listeners for menu items
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            if (editClickListener != null) {
                                editClickListener.onEditClick(position);
                            }
                            return true;
                        case R.id.menu_follow_up:
                            // Handle the "Follow-up" menu item click
                            return true;
                        case R.id.menu_delete:
                            if (onDeleteClickListener != null) {
                                onDeleteClickListener.onDeleteClick(position);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            });

            // Show the PopupMenu
            popupMenu.show();
        }

        // Create a method to format the data for the QR code
        private String createQRCodeData(Report report) {
            // You can format the data as needed, e.g., report details, user info, reward money
            // Format the time without colons
            String formattedTime = report.getCrime_time().replace(":", "");
            // Remove colons from the "Short Description" field
            String shortDescription = report.getCrime_description().replace(":", " ");

            // Here's a sample format (customize as per your requirements):
            return "Report ID: " + report.getReportId() + "\n" +
                    "Crime Type: " + report.getCrime_type() + "\n" +
                    "Person of Interest: " + report.getCrime_person() + "\n" +
                    "When it happened: " + report.getCrime_date() + "\n" +
                    "What time it happened: " + formattedTime + "\n" +
                    "Is the user used current location: " + report.getIsUseCurrentLocation() + "\n" +
                    "Where it happened: " + report.getCrime_location() + "\n" +
                    "Short Description: " + shortDescription + "\n" +
                    "User Details" + "\n" +
                    "User Name: " + report.getCrime_user_name() + "\n" +
                    "User Sex: " + report.getCrime_user_sex() + "\n" +
                    "User Email: " + report.getCrime_user_email() + "\n" +
                    "User Phone: " + report.getCrime_user_phone() + "\n" +
                    "Report Date: " + report.getReport_date() + "\n" +
                    "Status: " + report.getStatus() + "\n" +
                    "Reward: " + calculateReward(report);
        }

        // Calculate the reward based on report details
        private double calculateReward(Report report) {
            // Add your logic to calculate the reward amount
            // For example, you can calculate based on the type of report, severity, etc.
            // Return the calculated reward amount.
            return 100;
        }

        // Show the QR code to the user (customize this part)
        private void showQRCodeDialog(Bitmap qrCodeBitmap) {
            // Implement a dialog or activity to display the QR code to the user
            // You can create a custom dialog with an ImageView to show the QR code bitmap.
            // Alternatively, you can start a new activity to display the QR code.
            Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
            // Create an AlertDialog.Builder using the context
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.qr_code_dialog, null);
            ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);

            // Find your MapView by its ID
            MapView mapView = dialogView.findViewById(R.id.mainMapView);

// Set the center position and zoom level
            GeoPoint centerPosition = new GeoPoint(14.9263325, 120.5892146); // Replace with your desired center coordinates
            int zoomLevel = 16; // Adjust this value for your desired zoom level

// Set the center position and zoom level for the MapView
            mapView.getController().setCenter(centerPosition);
            mapView.getController().setZoom(zoomLevel);

// Create a Marker with the marker's position and title
            Marker marker = new Marker(mapView);
            marker.setPosition(centerPosition);
            marker.setTitle("Municipality of Lubao"); // Replace with your desired title

// Add the marker to the MapView's overlays
            mapView.getOverlays().add(marker);

// Refresh the MapView to update the display
            mapView.invalidate();

            builder.setView(dialogView)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Close the dialog
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private Bitmap generateQRCode(String data) {
            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                return bmp;
            } catch (WriterException e) {
                e.printStackTrace();
                return null;
            }
        }


    }
}
