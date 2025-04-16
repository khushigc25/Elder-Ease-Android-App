package com.example.elderease.news;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.elderease.R;

public class WebViewsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_views);

        WebView webView = findViewById(R.id.newsWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }
}
