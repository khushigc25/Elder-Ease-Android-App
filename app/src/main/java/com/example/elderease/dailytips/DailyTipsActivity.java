package com.example.elderease.dailytips;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import java.util.Random;
import com.example.elderease.R;

public class DailyTipsActivity extends AppCompatActivity {
    private String[] healthTips = {
            "Drink at least 8 glasses of water daily to stay hydrated. 💧",
            "Stretch gently in the morning to improve flexibility. 🏋️‍♂️",
            "Eat fiber-rich foods like fruits and whole grains for better digestion. 🍏🥦",
            "Take short walks to keep your joints healthy and strong. 🚶‍♂️",
            "Get at least 7 hours of sleep for good memory and health. 😴",
            "Use handrails when climbing stairs to prevent falls. ⚠️",
            "Check your blood pressure regularly to stay healthy. 🩺",
            "Wear comfortable, well-fitted shoes to prevent foot pain. 👟",
            "Spend time in sunlight to get enough Vitamin D. 🌞",
            "Listen to relaxing music or practice deep breathing to reduce stress. 🎵",
            "Keep your home well-lit to avoid tripping hazards. 💡",
            "Maintain a healthy weight to reduce joint pain. ⚖️",
            "Engage in social activities to keep your mind active. 🧠",
            "Eat protein-rich foods like eggs, fish, and nuts to maintain strength. 🍳🐟",
            "Stay in touch with family and friends for emotional support. 👨‍👩‍👧‍👦",
            "Read books or do puzzles to keep your brain sharp. 📖🧩",
            "Use a walking aid if needed for extra balance support. 🦯",
            "Avoid too much sugar and salt for better heart health. ❤️",
            "Get your eyesight and hearing checked regularly. 👓👂",
            "Take deep breaths and relax to improve mental well-being. 🌿",
            "Wash your hands often to prevent infections. 🧼",
            "Take your medicines on time—set a reminder if needed. ⏰💊",
            "Use pillows or cushions to support your back while sitting. 🪑",
            "Stay safe—don’t rush when walking on slippery floors. 🚶‍♂️⚠️",
            "Include Omega-3 foods like walnuts and salmon for brain health. 🧠🥜",
            "Keep emergency contacts handy at all times. 📞",
            "Drink warm herbal tea to relax and aid digestion. 🍵",
            "Get a health check-up every 6 months to stay ahead of issues. 🏥",
            "Laugh more—it’s good for your heart and mood! 😆❤️",
            "Practice good posture to avoid back and neck pain. 🏋️‍♂️",
            "Limit screen time and rest your eyes often. 👀",
            "Use assistive devices if necessary for mobility and hearing. 🦻",
            "Eat colorful vegetables to get a variety of nutrients. 🥗",
            "Keep a journal to track your health and mood. 📖",
            "Avoid excessive caffeine for better sleep. ☕🚫",
            "Take part in light strength exercises to maintain muscle mass. 💪",
            "Be cautious with slippery rugs and clutter at home. 🚧",
            "Stay updated on vaccinations for flu and other illnesses. 💉",
            "Use nightlights to safely navigate at night. 🌙",
            "Practice gratitude daily for a positive mindset. 😊",
            "Stay mentally active with hobbies you enjoy. 🎨",
            "Eat dark chocolate in moderation for heart benefits. 🍫",
            "Avoid smoking and limit alcohol consumption. 🚭🍷",
            "Try yoga or tai chi for balance and flexibility. 🧘‍♂️",
            "Use sunscreen when outdoors to protect your skin. ☀️",
            "Declutter your home to prevent accidents. 🏠",
            "Use a pedometer to track daily steps and stay active. 📊",
            "Practice mindful eating to prevent overeating. 🍽️",
            "Get regular dental check-ups for oral health. 🦷",
            "Eat probiotic-rich foods like yogurt for gut health. 🥄",
            "Keep a regular sleep schedule for better rest. 🌙",
            "Stay hydrated with soups and herbal infusions. 🥣",
            "Use relaxation techniques like meditation. 🧘",
            "Take short breaks when using the computer for long periods. 💻",
            "Have a balanced diet with healthy fats. 🥑",
            "Use assistive technology if needed. 📱",
            "Reduce noise pollution in your living space. 🔇",
            "Strengthen your grip with hand exercises. ✊",
            "Enjoy gardening for relaxation and exercise. 🌱",
            "Eat small meals throughout the day to maintain energy. 🍽️",
            "Use a calendar to track medical appointments. 📅",
            "Avoid processed foods to reduce health risks. 🚫🥫",
            "Get fresh air daily, even if just on the balcony. 🌬️",
            "Practice patience and kindness to yourself. 💕"
    };

    private TextView tipText;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tips);

        tipText = findViewById(R.id.tipText);
        Button nextTipButton = findViewById(R.id.nextTipButton);

        random = new Random();
        showRandomTip();

        nextTipButton.setOnClickListener(v -> showRandomTip());
    }

    private void showRandomTip() {
        int index = random.nextInt(healthTips.length);
        tipText.setText(healthTips[index]);
    }
}
