package com.example.cafefirstcup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure correct XML file

        // Find button and ensure it's not null
        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, login_page.class); // Ensure class name is correct
                startActivity(intent);
            });
        }
    }
}
