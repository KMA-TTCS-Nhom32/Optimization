package com.example.optimization; // Sửa lại package name cho đúng với máy bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class OverdrawActivity extends AppCompatActivity {

    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overdraw);

        container = findViewById(R.id.container);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnBad = findViewById(R.id.btnShowBad);
        Button btnGood = findViewById(R.id.btnShowGood);

        // Mặc định load Bad
        loadLayout(true);

        btnBad.setOnClickListener(v -> loadLayout(true));
        btnGood.setOnClickListener(v -> loadLayout(false));
    }

    private void loadLayout(boolean isBad) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        if (isBad) {
            inflater.inflate(R.layout.layout_overdraw_bad, container, true);
        } else {
            inflater.inflate(R.layout.layout_overdraw_good, container, true);
        }
    }
}