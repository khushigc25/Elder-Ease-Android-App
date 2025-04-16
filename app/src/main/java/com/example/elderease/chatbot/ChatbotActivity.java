package com.example.elderease.chatbot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.BuildConfig;
import com.example.elderease.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<MessageModel> messageList;
    private EditText userInput;
    private ImageButton sendButton;
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    private boolean isUserAtBottom = true; // Track if user is at the bottom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Scroll detection: track if user is at the bottom
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) chatRecyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                isUserAtBottom = (lastVisibleItemPosition == messageList.size() - 1);
            }
        });

        // Send message when button is clicked
        sendButton.setOnClickListener(v -> {
            String userMessage = userInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                messageList.add(new MessageModel(userMessage, true));
                chatAdapter.notifyDataSetChanged();
                scrollToBottom(); // Always scroll for user messages
                sendMessageToGemini(userMessage);
                userInput.setText("");
            }
        });

        // Prevent forced scrolling when chatbot replies
        userInput.setOnClickListener(v -> scrollToBottom());
    }

    private void sendMessageToGemini(String userMessage) {
        GeminiAPI api = RetrofitClient.getGeminiAPI();
        GeminiRequest request = new GeminiRequest(userMessage);

        Call<GeminiResponse> call = api.generateContent(API_KEY, request);
        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getCandidates() != null && !response.body().getCandidates().isEmpty() &&
                        response.body().getCandidates().get(0).getContent().getParts() != null &&
                        !response.body().getCandidates().get(0).getContent().getParts().isEmpty()) {

                    String botReply = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    messageList.add(new MessageModel(botReply, false));
                    chatAdapter.notifyDataSetChanged();

                    if (isUserAtBottom) {
                        // Scroll a bit instead of jumping fully to the bottom
                        chatRecyclerView.post(() -> chatRecyclerView.scrollBy(0, 200)); // Adjust 200 as needed
                    }
                } else {
                    messageList.add(new MessageModel("Error: Failed to get response", false));
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                messageList.add(new MessageModel("Error: " + t.getMessage(), false));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void scrollToBottom() {
        if (messageList.size() > 0) {
            chatRecyclerView.post(() -> chatRecyclerView.smoothScrollToPosition(messageList.size() - 1));
        }
    }
}
