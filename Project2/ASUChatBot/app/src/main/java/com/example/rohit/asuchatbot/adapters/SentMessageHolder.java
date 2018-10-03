package com.example.rohit.asuchatbot.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.rohit.asuchatbot.R;
import com.example.rohit.asuchatbot.model.ChatMessage;

/**
 * Created by Rohit on 03-05-2018.
 */

class SentMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageText;

    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
    }

    void bind(ChatMessage message) {
        messageText.setText(message.getMessage());
    }
}