package com.example.elderease.exercises;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<ExerciseModel> favoriteExercises;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private static final String PREFS_NAME = "ExercisePrefs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recycler_view_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        favoriteExercises = new ArrayList<>();

        loadFavorites();
    }

    private void loadFavorites() {
        String userFavoritesKey = getUserFavoritesKey();
        Set<String> favoriteSet = sharedPreferences.getStringSet(userFavoritesKey, new HashSet<>());

        if (favoriteSet.isEmpty()) {
            Toast.makeText(this, "No favorite exercises yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch all exercises from Firestore and filter only the ones marked as favorites
        firestore.collection("Exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteExercises.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            ExerciseModel exercise = doc.toObject(ExerciseModel.class);
                            if (favoriteSet.contains(exercise.getName())) {
                                favoriteExercises.add(exercise);
                            }
                        }
                        if (favoriteExercises.isEmpty()) {
                            Toast.makeText(this, "No favorite exercises found!", Toast.LENGTH_SHORT).show();
                        }
                        exerciseAdapter = new ExerciseAdapter(this, favoriteExercises);
                        recyclerView.setAdapter(exerciseAdapter);
                    } else {
                        Toast.makeText(this, "Failed to load exercises!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserFavoritesKey() {
        return currentUser != null ? "favorite_exercises_" + currentUser.getUid() : "favorite_exercises_guest";
    }
}
