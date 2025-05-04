package com.example.cafefirstcup;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextAddress;
    private TextView logoutText, myCartText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPhone = findViewById(R.id.editTextemail);
        editTextAddress = findViewById(R.id.editTextAddress);
        logoutText = findViewById(R.id.logoutText);
        myCartText = findViewById(R.id.myCartText);

        // Set default values (Optional: load from SharedPreferences or DB)
        editTextName.setText("enter yout name");
        editTextPhone.setText("");
        editTextAddress.setText("your location");

        // Handle logout click
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Optional: Clear session or shared preferences
                Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                // Navigate to LoginActivity (replace with your login screen)
                Intent intent = new Intent(ProfileActivity.this, login_page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Handle cart click
        myCartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Navigate to cart page (replace with your actual cart activity)
                Toast.makeText(ProfileActivity.this, "Go to Cart", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }
}
