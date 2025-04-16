package com.example.elderease.chatbot;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.elderease.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<MessageModel> messages;

    public ChatAdapter(List<MessageModel> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageModel message = messages.get(position);

        // Convert Markdown-like text to HTML
        String cleanMessage = message.getMessage()
                .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>") // Convert **bold** to <b>bold</b>
                .replaceAll("\\*(.*?)\\*", "<i>$1</i>") // Convert *italic* to <i>italic</i>
                .replaceAll("## (.*?)\\n", "<h3>$1</h3>") // Convert ## Headers to <h3> Headers</h3>
                .replaceAll("# (.*?)\\n", "<h2>$1</h2>") // Convert # Headers to <h2> Headers</h2>
                .replaceAll("- (.*?)\\n", "<li>$1</li>") // Convert bullet points (-) to <li>
                .replaceAll("\n", "<br>"); // Convert line breaks to <br>

        if (message.isBot()) {
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatTextView.setText(Html.fromHtml(cleanMessage));
        } else {
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatTextView.setText(Html.fromHtml(cleanMessage));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftChatTextView, rightChatTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftChatTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextView = itemView.findViewById(R.id.right_chat_text_view);

            // Enable text selection in Java
            leftChatTextView.setTextIsSelectable(true);
            leftChatTextView.setFocusable(true);
            leftChatTextView.setFocusableInTouchMode(true);

            rightChatTextView.setTextIsSelectable(true);
            rightChatTextView.setFocusable(true);
            rightChatTextView.setFocusableInTouchMode(true);
        }
    }
}
