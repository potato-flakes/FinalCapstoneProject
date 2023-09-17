package com.system.finalcapstoneproject.reportingsystem;

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
    private String IP_ADDRESS = UrlConstants.IP_ADDRESS;

    // Define a message listener interface
    public interface MessageListener {
        void onMessageReceived(String adminFullName, String sender, String message);
    }

    public WebSocketClient() {
        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        Log.e("WebSocketClient", "WebSocketClient - has launched");

        String userId = "9183797"; // Replace with the actual user ID
        String adminId = "1891944"; // Replace with the actual admin ID
        Request request = new Request.Builder()
                .url("ws://" + IP_ADDRESS + ":8081?user_id=" + userId + "&admin_id=" + adminId + "&getUsersMessages=true")
                .build();

        // Create a WebSocket listener
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.e("WebSocketClient", "onMessage - response: " + response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.e("WebSocketClient", "onMessage - Message: " + text);

                try {
                    JSONArray jsonArray = new JSONArray(text); // Parse the incoming JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject messageObject = jsonArray.getJSONObject(i);

                        // Extract message data from the JSON object
                        int senderId = messageObject.getInt("sender_id");
                        int receiverId = messageObject.getInt("receiver_id");
                        String message = messageObject.getString("message");

                        // Determine the sender's identity based on senderId and receiverId
                        String senderName;
                        if (senderId == 9183797) {
                            senderName = "You";
                            String adminFirstName = messageObject.getString("receiver_firstname");
                            String adminLastName = messageObject.getString("receiver_lastname");
                            adminFullName = adminFirstName + " " + adminLastName;
                            Log.e("WebSocketClient", "onMessage - adminName: " + adminFullName);
                        } else {
                            // Handle other cases if needed
                            senderName = "Admin"; // Replace with appropriate handling
                        }


                        // Notify the message listener with the sender's identity and message
                        if (messageListener != null) {
                            messageListener.onMessageReceived(adminFullName, senderName, message);
                        }
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
