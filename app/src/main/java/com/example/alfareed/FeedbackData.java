package com.example.alfareed;

public class FeedbackData {
    private String userName;
    private String feedback;

    public FeedbackData() {
        // Default constructor required for calls to DataSnapshot.getValue(FeedbackData.class)
    }

    public FeedbackData(String userName, String feedback) {
        this.userName = userName;
        this.feedback = feedback;
    }

    public String getUserName() {
        return userName;
    }

    public String getFeedback() {
        return feedback;
    }
}