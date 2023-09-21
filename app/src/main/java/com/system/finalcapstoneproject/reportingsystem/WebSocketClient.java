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
    private String IP_ADDRESS = UrlConstants.IP_ADDRESS;
    private Context context; // Add a Context field

    // Define a message listener interface
    public interface MessageListener {
        void onMessageReceived(String adminFullName, String sender, String message);
    }

    public WebSocketClient(Context context) {
        this.context = context; // Initialize the context field
        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        Log.e("WebSocketClient", "WebSocketClient - has launched");

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        Log.e("HomeActivity", "retrieveUserDetails - User ID:" + user_id);

        int newUserID = 0;
        try {
            int userIdAsInt = Integer.parseInt(user_id);
            newUserID = userIdAsInt; // Assign the parsed value to newUserID
            Log.e("HomeActivity", "User ID as int:" + newUserID);
        } catch (NumberFormatException e) {
            // Handle the case where user_id is not a valid integer
            e.printStackTrace();
        }

        String adminId = "1891944"; // Replace with the actual admin ID
        Request request = new Request.Builder().url("ws://" + IP_ADDRESS + ":8081?user_id=" + user_id + "&admin_id=" + adminId + "&getUsersMessages=true").build();

        // Create a WebSocket listener
        int finalNewUserID = newUserID;
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
                        String message = messageObject.getString("message");

                        // Determine the sender's identity based on senderId and receiverId
                        String senderName = null;
                        if (senderId == finalNewUserID) {
                            senderName = "You"; // You are the sender
                        } else {
                            // Handle messages sent by the admin
                            String adminFirstName = messageObject.getString("sender_firstname");
                            String adminLastName = messageObject.getString("sender_lastname");
                            adminFullName = adminFirstName + " " + adminLastName;
                            senderName = "Admin"; // You are the sender
                        }

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
