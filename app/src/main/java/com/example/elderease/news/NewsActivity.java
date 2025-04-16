package com.example.elderease.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.elderease.BuildConfig;
import com.example.elderease.R;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private static final String API_KEY = BuildConfig.NEWS_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up buttons
        setupCategoryButtons();

        // Fetch General News by default
        fetchNews("general");
    }

    private void setupCategoryButtons() {
        Button[] buttons = {
                findViewById(R.id.btnGeneral),
                findViewById(R.id.btnWorld),
                findViewById(R.id.btnNation),
                findViewById(R.id.btnBusiness),
                findViewById(R.id.btnTechnology),
                findViewById(R.id.btnEntertainment),
                findViewById(R.id.btnSports),
                findViewById(R.id.btnScience),
                findViewById(R.id.btnHealth)
        };

        // Set "General" button as selected by default
        Button btnGeneral = findViewById(R.id.btnGeneral);
        btnGeneral.setSelected(true);

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                // Deselect all buttons first
                for (Button btn : buttons) {
                    btn.setSelected(false);
                }

                // Select the clicked button
                v.setSelected(true);

                // Fetch news based on selected button
                String category = v.getTag().toString(); // Ensure buttons have tag attributes in XML
                fetchNews(category);
            });
        }
    }


    private void fetchNews(String category) {
        GNewsApiService apiService = GNewsRetrofitClient.getNewsService();
        Call<NewsResponse> call = apiService.getTopHeadlines(API_KEY, "en", "in", category);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> newsList = response.body().getArticles();
                    newsAdapter = new NewsAdapter(NewsActivity.this, newsList);
                    recyclerView.setAdapter(newsAdapter);
                } else {
                    Toast.makeText(NewsActivity.this, "No news found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Toast.makeText(NewsActivity.this, "Failed to load news", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
