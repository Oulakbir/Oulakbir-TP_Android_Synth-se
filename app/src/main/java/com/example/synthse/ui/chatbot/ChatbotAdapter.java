package com.example.synthse.ui.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synthse.R;
import com.example.synthse.ui.chatbot.models.MessageModel;
import com.example.synthse.ui.chatbot.models.Sender;

import java.util.List;

public class ChatbotAdapter extends RecyclerView.Adapter {
    private List<MessageModel> messages;
    private Context context;

    public ChatbotAdapter(List<MessageModel> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType) {
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.user_msg, parent, false);
                return new UserViewHolder(view);

            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.bot_msg, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Sender sender = messages.get(position).getSender();
        switch (sender) {
            case USER: return 0;
            case BOT: return 1;
            default: return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        switch (message.getSender()) {
            case USER:
                ((UserViewHolder) holder).userMsg.setText(message.getContent());
                break;
            case BOT:
                ((BotViewHolder) holder).botMsg.setText(message.getContent());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userMsg;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userMsg = itemView.findViewById(R.id.user_msg);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        private TextView botMsg;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botMsg = itemView.findViewById(R.id.bot_msg);
        }
    }
}
