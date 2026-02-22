package com.example.moneymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.MainActivity;
import com.example.moneymanager.R;
import com.example.moneymanager.utils.AuthHelper;

public class SplashActivity extends AppCompatActivity {

    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authHelper = new AuthHelper(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkAuthenticationAndNavigate();
        }, 3000); // 3 seconds delay
    }

    private void checkAuthenticationAndNavigate() {
        Intent intent;
        if (authHelper.isLoggedIn()) {
            // User is logged in, go to MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User is not logged in, go to LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}