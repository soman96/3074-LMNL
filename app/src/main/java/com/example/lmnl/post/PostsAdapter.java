package com.example.lmnl.post;

import android.content.Intent;
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

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public void setPosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvPostUsername.setText("@" + post.getUsername());
        holder.tvPostContent.setText(post.getContent());
        holder.tvPostTimestamp.setText(formatTimestamp(post.getCreatedAt()));

        // Handle click to view post details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    private String formatTimestamp(String timestamp) {
        try {
            // SQLite timestamp format: "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);

            // Format to more readable: "MMM d, yyyy"
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return timestamp; // Return original if parsing fails
        }
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView tvPostUsername;
        TextView tvPostContent;
        TextView tvPostTimestamp;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPostUsername = itemView.findViewById(R.id.tvPostUsername);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            tvPostTimestamp = itemView.findViewById(R.id.tvPostTimestamp);
        }
    }
}