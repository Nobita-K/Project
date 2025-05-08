package com.example.alfareed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<FeedbackData> feedbackList;

    public FeedbackAdapter(List<FeedbackData> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackData feedback = feedbackList.get(position);
        holder.userNameTextView.setText(feedback.getUserName());
        holder.feedbackTextView.setText(feedback.getFeedback());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public void setFeedbackList(List<FeedbackData> feedbackList) {
        this.feedbackList = feedbackList;
        notifyDataSetChanged();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, feedbackTextView;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            feedbackTextView = itemView.findViewById(R.id.feedbackTextView);
        }
    }
}