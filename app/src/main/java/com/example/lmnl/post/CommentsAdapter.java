package com.example.lmnl.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmnl.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void setComments(List<Comment> newComments) {
        this.comments = newComments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvCommentUsername.setText("@" + comment.getUsername());
        holder.tvCommentContent.setText(comment.getContent());
        holder.tvCommentTimestamp.setText(formatTimestamp(comment.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    private String formatTimestamp(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return timestamp;
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUsername;
        TextView tvCommentContent;
        TextView tvCommentTimestamp;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentTimestamp = itemView.findViewById(R.id.tvCommentTimestamp);
        }
    }
}