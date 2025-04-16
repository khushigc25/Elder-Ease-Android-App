package com.example.elderease.exercises;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private Context context;
    private List<ExerciseModel> exerciseModels;
    private SharedPreferences sharedPreferences;
    private FirebaseUser currentUser;
    private static final String PREFS_NAME = "ExercisePrefs";

    public ExerciseAdapter(Context context, List<ExerciseModel> exerciseModels) {
        this.context = context;
        this.exerciseModels = exerciseModels;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ExerciseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ViewHolder holder, int position) {
        ExerciseModel exercise = exerciseModels.get(holder.getAdapterPosition());
        holder.name.setText(exercise.getName());
        Glide.with(context).load(exercise.getImg()).into(holder.img);

        // Get user-specific favorites key
        String userFavoritesKey = getUserFavoritesKey();

        // Load favorite state for the current user
        Set<String> favoriteSet = sharedPreferences.getStringSet(userFavoritesKey, new HashSet<>());
        boolean isFavorite = favoriteSet.contains(exercise.getName());

        // Update UI based on favorite state
        holder.favoriteIcon.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // Handle favorite toggle
        holder.favoriteIcon.setOnClickListener(v -> toggleFavorite(holder, exercise, userFavoritesKey));

        // Navigate to ExerciseDetailedActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExerciseDetailedActivity.class);
            intent.putExtra("detailed", exercise);
            context.startActivity(intent);
        });
    }

    private void toggleFavorite(ViewHolder holder, ExerciseModel exercise, String userFavoritesKey) {
        Set<String> favoriteSet = new HashSet<>(sharedPreferences.getStringSet(userFavoritesKey, new HashSet<>()));

        if (favoriteSet.contains(exercise.getName())) {
            // Remove from favorites
            favoriteSet.remove(exercise.getName());
            holder.favoriteIcon.setImageResource(R.drawable.ic_heart_outline);
        } else {
            // Add to favorites
            favoriteSet.add(exercise.getName());
            holder.favoriteIcon.setImageResource(R.drawable.ic_heart_filled);
        }

        sharedPreferences.edit().putStringSet(userFavoritesKey, favoriteSet).apply();
    }

    private String getUserFavoritesKey() {
        return currentUser != null ? "favorite_exercises_" + currentUser.getUid() : "favorite_exercises_guest";
    }

    @Override
    public int getItemCount() {
        return exerciseModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView img, favoriteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exercise_name);
            img = itemView.findViewById(R.id.exercise_image);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }
    }
}
