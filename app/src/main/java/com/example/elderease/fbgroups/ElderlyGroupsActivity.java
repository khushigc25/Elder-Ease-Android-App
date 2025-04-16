package com.example.elderease.fbgroups;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ElderlyGroupsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<GroupModel> groupList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_groups);

        recyclerView = findViewById(R.id.elderGroupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupList);
        recyclerView.setAdapter(groupAdapter);

        db = FirebaseFirestore.getInstance();
        fetchGroupsFromFirestore();
    }

    private void fetchGroupsFromFirestore() {
        CollectionReference groupsRef = db.collection("ElderlyGroups"); // Firestore collection name

        groupsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                groupList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String description = document.getString("desc");
                    String url = document.getString("url");

                    if (name != null && description != null && url != null) {
                        groupList.add(new GroupModel(name, description, url));
                    }
                }
                groupAdapter.notifyDataSetChanged();
            } else {
                Log.e("Firestore", "Error getting documents", task.getException());
                Toast.makeText(ElderlyGroupsActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
