package com.example.synthse.ui.chatbot.models;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private String content;
    private Sender sender;

    public MessageModel() {}
    public MessageModel(String message, Sender sender) {
        this.content = message;
        this.sender = sender;
    }

    public String getContent() { return content; }
    public void setContent(String message) { this.content = message; }
    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }
}
