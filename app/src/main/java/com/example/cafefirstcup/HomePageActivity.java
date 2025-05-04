package com.example.cafefirstcup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    RecyclerView recyclerView, categoryRecyclerView;
    MealAdapter mealAdapter;
    CategoryAdapter categoryAdapter;
    List<Meal> mealList = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();

    // Location services
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
    private ImageView cartIcon;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.recyclerView);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        locationTextView = findViewById(R.id.tv_location);

        // Set up Bottom Navigation
        setupBottomNavigation();

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationTextView.setOnClickListener(v -> requestLocationPermission());

        // Load meal list and categories
        setupRecyclerViews();
        loadRunningMeals();
        loadCategories();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    Toast.makeText(HomePageActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.navigation_rewards) {
                    Toast.makeText(HomePageActivity.this, "Rewards Selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HomePageActivity.this, RewardsActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_cart) {
                    Toast.makeText(HomePageActivity.this, "Cart Selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HomePageActivity.this, CartActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    Toast.makeText(HomePageActivity.this, "Profile Selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setupRecyclerViews() {
        // Set up Meals RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mealAdapter = new MealAdapter(this,mealList);
        recyclerView.setAdapter(mealAdapter);

        // Set up Categories RecyclerView (Horizontal Scroll)
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this, categoryList, this::loadCategory);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void loadRunningMeals() {
        mealList.clear();
        mealList.add(new Meal("Cappuccino", "2", R.drawable.cappuccino));
        mealList.add(new Meal("Espresso", "300", R.drawable.espresso));
        mealList.add(new Meal("Americano", "155", R.drawable.americano));
        mealList.add(new Meal("Latte", "140", R.drawable.latte));
        mealList.add(new Meal("Cheese Burger", "250", R.drawable.cappuccino));
        mealList.add(new Meal("Chicken Burger", "300", R.drawable.espresso));
        mealList.add(new Meal("French Fries", "150", R.drawable.americano));
        mealList.add(new Meal("Cheese Fries", "200", R.drawable.latte));
        mealAdapter.notifyDataSetChanged();
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.add(new Category("Coffee", R.drawable.ic_coffee));
        categoryList.add(new Category("Burgers", R.drawable.ic_burgers));
        categoryList.add(new Category("Pizza", R.drawable.ic_pizza));
        categoryList.add(new Category("Fries", R.drawable.ic_fries));
        categoryList.add(new Category("Mojito", R.drawable.ic_mojito));
        categoryAdapter.notifyDataSetChanged();
    }

    private void loadCategory(String category) {
        mealList.clear();

        if (category.equals("Coffee")) {
            mealList.add(new Meal("Cappuccino", "2", R.drawable.cappuccino));
            mealList.add(new Meal("Latte", "140", R.drawable.latte));

        } else if (category.equals("Burgers")) {
            mealList.add(new Meal("Cheese Burger", "250", R.drawable.cheese_burger));
            mealList.add(new Meal("Chicken Burger", "300", R.drawable.chicken_burger));

        } else if (category.equals("Pizza")) {
            mealList.add(new Meal("Margherita", "400", R.drawable.margherita_pizza));
            mealList.add(new Meal("Pepperoni", "500", R.drawable.pepperoni_pizza));

        } else if (category.equals("Fries")) {
            mealList.add(new Meal("French Fries", "150", R.drawable.fries));
            mealList.add(new Meal("Cheese Fries", "200", R.drawable.cheese_fries));

        } else if (category.equals("Mojito")) {
            mealList.add(new Meal("Classic Mojito", "180", R.drawable.ic_mojito));
            mealList.add(new Meal("Strawberry Mojito", "200", R.drawable.strawberry_mojito));
        }

        mealAdapter.notifyDataSetChanged();
    }

    // Location Permission Request
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Uri mapUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Your Location)");
                startActivity(new Intent(Intent.ACTION_VIEW, mapUri));
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
