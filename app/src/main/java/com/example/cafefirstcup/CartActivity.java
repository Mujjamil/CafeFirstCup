package com.example.cafefirstcup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {

    ProgressDialog progressDialog;
    private RecyclerView recyclerOrderItems;
    private Button btnCreateOrder;
    private TextView tvTotalPrice, tvCoinCount;
    private RadioGroup paymentModeGroup;
    private RadioButton rbCash, rbOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.d("CartActivity", "Layout inflated: activity_cart");

        Checkout.preload(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        recyclerOrderItems = findViewById(R.id.recyclerOrderItems);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvCoinCount = findViewById(R.id.tvCoinCount);
        ImageView btnBack = findViewById(R.id.btnBack);
        paymentModeGroup = findViewById(R.id.paymentMethodGroup);
        rbCash = findViewById(R.id.rbCash);
        rbOnline = findViewById(R.id.rbOnline);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadCartItems();
        loadUserCoins();

        btnCreateOrder.setOnClickListener(v -> {
            if (OrderManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = paymentModeGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a payment mode", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == R.id.rbCash) {
                Toast.makeText(CartActivity.this, "Cash Payment Selected", Toast.LENGTH_SHORT).show();
                simulateCashPaymentSuccess();
            } else {
                startRazorPayment();
            }
        });
    }

    private void loadCartItems() {
        List<OrderItem> orderItems = OrderManager.getCartItems();

        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            btnCreateOrder.setEnabled(false);
        } else {
            btnCreateOrder.setEnabled(true);
        }

        OrderAdapter orderAdapter = new OrderAdapter(orderItems, tvTotalPrice, this);
        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrderItems.setAdapter(orderAdapter);

        tvTotalPrice.setText("Total: ₹" + OrderManager.getTotalPrice());
    }

    private void loadUserCoins() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference coinRef = db.collection("Users").document(userId).collection("Coins").document("TotalCoins");
        coinRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists() && snapshot.contains("coins")) {
                int coins = snapshot.getLong("coins").intValue();
                tvCoinCount.setText("Coins: " + coins);
            } else {
                tvCoinCount.setText("Coins: 0");
            }
        }).addOnFailureListener(e -> {
            tvCoinCount.setText("Coins: 0");
            Log.e("CartActivity", "Failed to load coins: " + e.getMessage());
        });
    }

    private void startRazorPayment() {
        progressDialog.show();
        final Activity activity = this;
        final Checkout checkout = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "First Cup Cafe");
            options.put("description", "Order Payment");
            options.put("currency", "INR");
            options.put("amount", OrderManager.getTotalPrice() * 100);

            checkout.open(activity, options);
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("CartActivity", "Error starting Razorpay: ", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        progressDialog.dismiss();
        Toast.makeText(this, "Payment Successful! ID: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        saveOrderToDatabase("Online");
    }

    @Override
    public void onPaymentError(int code, String response) {
        progressDialog.dismiss();
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_SHORT).show();
        Log.e("CartActivity", "Payment error: Code=" + code + ", Response=" + response);
    }

    private void simulateCashPaymentSuccess() {
        Toast.makeText(this, "Payment Successful In Cash", Toast.LENGTH_SHORT).show();
        saveOrderToDatabase("Cash");
    }

    private void saveOrderToDatabase(String paymentMode) {
        int totalAmount = OrderManager.getTotalPrice();
        int earnedCoins = totalAmount / 10;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("amount", totalAmount);
        orderData.put("coinsEarned", earnedCoins);
        orderData.put("timestamp", FieldValue.serverTimestamp());
        orderData.put("paymentMode", paymentMode);

        db.collection("Users").document(userId)
                .collection("Orders").add(orderData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Order saved");

                    DocumentReference coinRef = db.collection("Users").document(userId).collection("Coins").document("TotalCoins");
                    coinRef.get().addOnSuccessListener(snapshot -> {
                        int currentCoins = 0;
                        if (snapshot.exists() && snapshot.contains("coins")) {
                            currentCoins = snapshot.getLong("coins").intValue();
                        }

                        int finalCoins = currentCoins + earnedCoins;
                        Map<String, Object> coinData = new HashMap<>();
                        coinData.put("coins", finalCoins);

                        coinRef.set(coinData)
                                .addOnSuccessListener(unused -> {
                                    tvCoinCount.setText("Coins: " + finalCoins);
                                    Toast.makeText(CartActivity.this, "Order placed & Coins updated!", Toast.LENGTH_SHORT).show();

                                    OrderManager.clearCart();
                                    loadCartItems();
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update coins: " + e.getMessage()));
                    }).addOnFailureListener(e -> Log.e("Firestore", "Failed to get coins: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving order: " + e.getMessage()));
    }
}
