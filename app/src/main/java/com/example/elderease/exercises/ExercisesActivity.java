package com.example.elderease.exercises;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ExerciseAdapter exerciseAdapter;
    List<ExerciseModel> exerciseList;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.exercise_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String category = getIntent().getStringExtra("type");
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(this, exerciseList);
        recyclerView.setAdapter(exerciseAdapter);

        firestore.collection("Exercises")
                .whereEqualTo("type", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            ExerciseModel exercise = doc.toObject(ExerciseModel.class);
                            exerciseList.add(exercise);
                        }
                        exerciseAdapter.notifyDataSetChanged();
                    }
                });
    }
}
