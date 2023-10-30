package com.system.finalcapstoneproject.reportingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.system.finalcapstoneproject.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    private WebSocket webSocket;
    private MessageListener messageListener; // Add this field
    private String adminFullName;
    private Context context; // Add a Context field
    String adminId = "6591457"; // Replace with the actual admin ID
    int newUserID;
    public void send(String message) {
        if (webSocket != null && webSocket.send(message)) {
            Log.d("WebSocketClient", "Message sent: " + message);
        } else {
            Log.e("WebSocketClient", "Failed to send message: " + message);
        }
    }
    // Define a message listener interface
    public interface MessageListener {
        void onMessageReceived(String adminFullName, String sender, String message, String adminProfileImageUrl);
    }

    public WebSocketClient(Context context) {
        this.context = context; // Initialize the context field
        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        Log.e("WebSocketClient", "WebSocketClient - has launched");

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        Log.e("HomeActivity", "retrieveUserDetails - User ID:" + user_id);

        newUserID = 0;
        try {
            int userIdAsInt = Integer.parseInt(user_id);
            newUserID = userIdAsInt; // Assign the parsed value to newUserID
            Log.e("HomeActivity", "User ID as int:" + newUserID);
        } catch (NumberFormatException e) {
            // Handle the case where user_id is not a valid integer
            e.printStackTrace();
        }


        Request request = new Request.Builder().url("wss://recyclearn.glitch.me?user_id=" + user_id + "&admin_id=" + adminId + "&getUsersMessages=true").build();

        // Create a WebSocket listener
        int finalNewUserID = newUserID;
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.e("WebSocketClient", "onMessage - response: " + response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject messageObject = new JSONObject(text);
                    String messageType = messageObject.optString("type", "");

                    if ("userMessages".equals(messageType)) {
                        JSONArray messagesArray = messageObject.optJSONArray("messages");
                        if (messagesArray != null) {
                            // Iterate through the array of messages
                            for (int i = 0; i < messagesArray.length(); i++) {
                                JSONObject messageData = messagesArray.getJSONObject(i);

                                // Extract message data from the messageData object
                                int senderId = messageData.getInt("sender_id");
                                int receiverId = messageData.getInt("receiver_id");
                                String message = messageData.getString("message");
                                // Extract other message fields as needed
                                Log.e("HomeActivity", "retrieveUserDetails - messageData:" + messageData);
                               handleUserMessages(messageData);
                            }
                        } else {
                            // Handle the case where "messages" field is missing or not an array
                        }
                    } else if ("otherMessageType".equals(messageType)) {
                        // Handle other message types here
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // Handle binary message received from the server (if applicable)
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // WebSocket connection is closed
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Handle connection failure or errors
            }
        };
        // Create a WebSocket connection
        webSocket = client.newWebSocket(request, listener);
    }

    private void handleUserMessages(JSONObject messageObject) throws JSONException {
        // Extract message data from the JSON object
        int senderId = messageObject.getInt("sender_id");
        int receiverId = messageObject.getInt("receiver_id");
        String message = messageObject.getString("message");
        String senderName;
        String profile_image = null;
        // Check if the message is intended for the current user
        if (receiverId == newUserID || senderId == newUserID) {
            if (senderId == newUserID) {
                senderName = "You"; // You are the sender
            } else {
                // Handle messages sent by the admin
                String adminFirstName = messageObject.getString("sender_firstname");
                String adminLastName = messageObject.getString("sender_lastname");
                profile_image = messageObject.getString("sender_profile_image");
                adminFullName = adminFirstName + " " + adminLastName;
                senderName = "Admin"; // Admin is the sender
            }

            if (messageListener != null) {
                messageListener.onMessageReceived(adminFullName, senderName, message, profile_image);
            }
        }
    }

    // Method to close the WebSocket connection
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }

    // Setter method to set the message listener
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
}