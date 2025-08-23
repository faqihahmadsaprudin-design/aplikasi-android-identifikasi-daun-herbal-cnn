package com.example.herbidentifier;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    // Durasi tampilnya splash screen dalam milidetik (misal: 3000ms = 3 detik)
    private static final int SPLASH_SCREEN_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mengatur layout untuk Activity splash screen
        setContentView(R.layout.activity_splash_screen);

        // Menggunakan Handler untuk menunda perpindahan ke MainActivity
        // Ini akan membuat splash screen terlihat selama SPLASH_SCREEN_DURATION
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Setelah durasi splash screen berakhir, mulai MainActivity
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                // Tutup SplashScreen Activity agar tidak bisa kembali ke sini dengan tombol 'back'
                finish();
            }
        }, SPLASH_SCREEN_DURATION);
    }
}