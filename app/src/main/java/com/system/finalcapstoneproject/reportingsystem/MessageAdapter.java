package com.system.finalcapstoneproject.reportingsystem;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.system.finalcapstoneproject.R;
import com.system.finalcapstoneproject.UrlConstants;

import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Messages> messages;

    public MessageAdapter(List<Messages> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reporting_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Messages message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView senderTextView;
        private TextView messageContentTextView;
        private ImageView senderProfileImageView; // Add this field

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            senderProfileImageView = itemView.findViewById(R.id.senderProfileImageView); // Initialize the I
        }

        public void bind(Messages message) {
            messageContentTextView.setText(message.getMessage());

            // Determine the message sender and set the layout gravity
            if (message.getSender().equals("You")) {
                // User message (right-aligned)
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) messageContentTextView.getLayoutParams();
                layoutParams.horizontalBias = 1.0f; // Align to the right
                messageContentTextView.setLayoutParams(layoutParams);
                // User message (set user chat bubble background)
                messageContentTextView.setBackgroundResource(R.drawable.reporting_chat_bubble_user);
                messageContentTextView.setTextColor(Color.parseColor("#fcfcfc"));
                // Hide the sender's profile image for user messages
                senderProfileImageView.setVisibility(View.GONE);
            } else {
                // Admin message (left-aligned)
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) messageContentTextView.getLayoutParams();
                layoutParams.horizontalBias = 0.0f; // Align to the left
                messageContentTextView.setLayoutParams(layoutParams);
                // Admin message (set admin chat bubble background)
                messageContentTextView.setBackgroundResource(R.drawable.reporting_chat_bubble_admin);
                messageContentTextView.setTextColor(Color.parseColor("#0d0d0d"));

                // Load and display the admin's profile image
                String imageUrl = UrlConstants.GET_USER_PROFILE_IMAGES + message.getSenderProfileImageUrl();
                Picasso.get().load(imageUrl).into(senderProfileImageView);
                senderProfileImageView.setVisibility(View.VISIBLE); // Show sender's profile image for admin messages

                layoutParams.setMarginEnd(16); // Adjust the right margin as needed
                messageContentTextView.setLayoutParams(layoutParams);
            }
        }

    }
}