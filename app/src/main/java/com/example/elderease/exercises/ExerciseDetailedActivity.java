package com.example.elderease.exercises;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExerciseDetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView name, description, benefits;
    WebView webView;
    ExerciseModel exerciseModel = null;

    FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detailed);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");

        if (obj instanceof ExerciseModel) {
            exerciseModel = (ExerciseModel) obj;
        }

        // Initialize the views
        detailedImg = findViewById(R.id.exercise_image);
        name = findViewById(R.id.exercise_name);  // Initialize the 'name' TextView
        description = findViewById(R.id.exercise_description);
        benefits = findViewById(R.id.exercise_benefits);
        webView = findViewById(R.id.exercise_link); // Initialize the WebView

        if (exerciseModel != null) {
            Glide.with(getApplicationContext()).load(exerciseModel.getImg()).into(detailedImg);
            name.setText(exerciseModel.getName());
            description.setText(exerciseModel.getDesc());
            benefits.setText(exerciseModel.getBenefit());

            // Get the iframe link from the database and load it into the WebView
            String iframeLink = exerciseModel.getLink();
            if (iframeLink != null && !iframeLink.isEmpty()) {
                // Set up the WebView to load the iframe content
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true); // Enable JavaScript
                webSettings.setDomStorageEnabled(true); // Enable DOM storage for iframes
                webSettings.setMediaPlaybackRequiresUserGesture(false); // Allow autoplay

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Inject custom CSS to ensure the iframe fits the WebView
                        String css = "iframe { width: 100% !important; height: 100% !important; }";
                        String js = "javascript:(function() { " +
                                "var style = document.createElement('style'); " +
                                "style.type = 'text/css'; " +
                                "style.innerHTML = '" + css + "'; " +
                                "document.head.appendChild(style);" +
                                "})()";
                        view.loadUrl(js);
                    }
                });

                // Load the iframe link using loadDataWithBaseURL
                String htmlContent = "<html><body>" + iframeLink + "</body></html>";
                webView.loadDataWithBaseURL("https://www.youtube.com", htmlContent, "text/html", "UTF-8", null);
            }
        }
    }
}