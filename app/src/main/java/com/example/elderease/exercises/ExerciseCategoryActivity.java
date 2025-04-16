package com.example.elderease.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExerciseCategoryActivity extends AppCompatActivity {
    RecyclerView categoryRecycler;
    ExerciseCategoryAdapter categoryAdapter;
    List<ExerciseCategoryModel> categoryList;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_category);

        firestore = FirebaseFirestore.getInstance();
        categoryRecycler = findViewById(R.id.category_recycler);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        categoryAdapter = new ExerciseCategoryAdapter(this, categoryList);
        categoryRecycler.setAdapter(categoryAdapter);

        // Find the LinearLayout by ID
        LinearLayout favoritesSection = findViewById(R.id.favorites_section);

        // Set an onClickListener for the whole layout
        favoritesSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open FavouritesActivity
                Intent intent = new Intent(ExerciseCategoryActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        firestore.collection("ExerciseCategory")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            ExerciseCategoryModel category = doc.toObject(ExerciseCategoryModel.class);
                            categoryList.add(category);
                            categoryAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
