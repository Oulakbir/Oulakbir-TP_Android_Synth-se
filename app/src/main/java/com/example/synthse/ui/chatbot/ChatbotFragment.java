package com.example.synthse.ui.chatbot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synthse.databinding.FragmentChatbotBinding;
import com.example.synthse.ui.chatbot.models.MessageModel;
import com.example.synthse.ui.chatbot.models.Sender;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatbotFragment extends Fragment {

    private FragmentChatbotBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        Context context = binding.getRoot().getContext();

        List<MessageModel> messages = new ArrayList<>();
        ChatbotAdapter chatBotAdapter = new ChatbotAdapter(messages, context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setAdapter(chatBotAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        binding.sendButton.setOnClickListener(view -> {
            String msg = binding.editText.getText().toString();
            messages.add(new MessageModel(msg, Sender.USER));
            chatBotAdapter.notifyDataSetChanged();

            binding.editText.setText("");

            OpenAiService service = new OpenAiService("sk-proj-30YvkfmtgpMmUE9cNIzTT3BlbkFJ4rqocIg6osSXLpu1pHUo");
            CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(msg)
                .model("gpt-3.5-turbo-instruct")
                .echo(true)
                .build();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CompletionChoice res = service.createCompletion(completionRequest).getChoices().get(0);

                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messages.add(new MessageModel(res.getText(), Sender.BOT));
                                chatBotAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messages.size() - 1);

                                // Create firestore chat document
                                Map<String, Object> chat = new HashMap<>();
                                chat.put("user", msg);
                                chat.put("bot", res.getText());
                                chat.put("createdAt", FieldValue.serverTimestamp());

                                // Save to the database
                                db.collection("chat").add(chat);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}