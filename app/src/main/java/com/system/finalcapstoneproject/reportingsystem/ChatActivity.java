package com.system.finalcapstoneproject.reportingsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.system.finalcapstoneproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response; // This is the library for HTTP response handling

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Messages> messageList;
    private WebSocketClient webSocketClient;
    private TextView adminNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporting_activity_chat);
        String userId = "9183797"; // Replace with the actual user ID
        String adminId = "1891944"; // Replace with the actual admin ID
        // Initialize UI components
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        adminNameTextView = findViewById(R.id.adminNameTextView);

        // Initialize the WebSocket client
        webSocketClient = new WebSocketClient();

        // Initialize the message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        // Set the RecyclerView's layout manager and adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setAdapter(messageAdapter);

        // Inside the sendButton OnClickListener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                // Get the message from the input field
                String messageText = messageInput.getText().toString().trim();

                // Add the user message to the message list
                if (!messageText.isEmpty()) {
                    Messages userMessage = new Messages("You", messageText);
                    sendMessageToServer(userId, adminId, messageText);
                    messageList.add(userMessage);
                    messageAdapter.notifyDataSetChanged();

                    // Scroll to the last message
                    messageRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);


                    // Clear the input field
                    messageInput.getText().clear();
                }
            }
        });

        webSocketClient.setMessageListener(new WebSocketClient.MessageListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onMessageReceived(String adminFullName, String sender, String message) {
                // Check if the message already exists in the list
                boolean messageExists = false;
                for (Messages existingMessage : messageList) {
                    if (existingMessage.getSender().equals(sender) && existingMessage.getMessage().equals(message)) {
                        messageExists = true;
                        break;
                    }
                }

                if (!messageExists) {
                    // Create a message object for the received message
                    Messages receivedMessage = new Messages(sender, message);
                    Log.e("ChatActivity", "onMessageReceived - Received Message: " + sender + message);

                    // Add the received message to the message list
                    messageList.add(receivedMessage);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyDataSetChanged();
                            adminNameTextView.setText(adminFullName);
                        }
                    });

                    // Scroll to the last message
                    messageRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }
        });

    }

    // Function to send a message to the server for storage

    // Function to send a message to the server for storage
    private void sendMessageToServer(String senderId, String receiverId, String messageText) {
        OkHttpClient client = new OkHttpClient();

        // Replace with the actual URL of your send_message.php script
        String url = "http://192.168.158.229/recyclearn/report_user/send_message.php";

        try {
            // Create a JSON object with the message data
            JSONObject messageData = new JSONObject();
            messageData.put("sender_id", senderId);
            messageData.put("receiver_id", receiverId);
            messageData.put("message", messageText);

            // Convert the JSON object to a string
            String messageJson = messageData.toString();

            // Create a request body with the message data
            okhttp3.RequestBody body = okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    messageJson
            );

            // Create an HTTP request
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Send the HTTP request
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Handle the request failure (e.g., network error)
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Handle the error on the UI thread if needed
                            // For example, show an error message to the user
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Handle the server's response, if needed
                    if (response.isSuccessful()) {
                        // The message was sent successfully
                        // You can update your UI or perform any necessary actions here
                        Log.d("SendMessage", "Message sent successfully");
                    } else {
                        // Handle the server's error response, if needed
                        Log.e("SendMessage", "Failed to send message: " + response.message());
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
