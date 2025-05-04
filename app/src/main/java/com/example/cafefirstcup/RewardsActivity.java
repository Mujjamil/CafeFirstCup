package com.example.cafefirstcup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RewardsActivity extends AppCompatActivity {
    private Button btnChallenge;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        btnChallenge = findViewById(R.id.dailyChallengeButton);
        btnChallenge.setOnClickListener(v -> {
            Intent intent = new Intent(RewardsActivity.this,HomePageActivity.class);
            startActivity(intent);
            finish();

        });


    }
}
