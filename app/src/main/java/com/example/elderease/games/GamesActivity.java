package com.example.elderease.games;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elderease.R;

public class GamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        // Find the WebView in the layout
        WebView webView = findViewById(R.id.webview_games);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript for interactive websites
        webSettings.setDomStorageEnabled(true); // Enable DOM storage for modern web content

        // Ensure links open within the WebView instead of an external browser
        webView.setWebViewClient(new WebViewClient());

        // Load the website
        webView.loadUrl("https://poki.com/");
    }
}
