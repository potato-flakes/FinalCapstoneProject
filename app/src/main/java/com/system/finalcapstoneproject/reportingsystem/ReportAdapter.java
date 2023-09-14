package com.system.finalcapstoneproject.reportingsystem;
// ReportAdapter.java

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

import com.system.finalcapstoneproject.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private OnItemClickListener itemClickListener;
    private OnEditClickListener editClickListener;
    // Define an interface for delete button click listener
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    private OnDeleteClickListener onDeleteClickListener;
    // Setter for delete button click listener
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public ReportAdapter(List<Report> reportList) {
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
        holder.descriptionTextView.setText(report.getDescription());
        holder.locationTextView.setText(report.getLocation());
        holder.dateTextView.setText(report.getDate());
        holder.timeTextView.setText(report.getTime());
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
        private Button deleteButton;

        public ReportViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);

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




    }
}
