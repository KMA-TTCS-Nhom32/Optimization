package com.example.optimization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class FlattenLayoutActivity extends AppCompatActivity {

    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flatten); // Kết nối với layout khung sườn

        container = findViewById(R.id.container);

        findViewById(R.id.btnShowBad).setOnClickListener(v -> loadLayout(true));
        findViewById(R.id.btnShowGood).setOnClickListener(v -> loadLayout(false));

        // Mặc định load Bad
        loadLayout(true);
    }

    private void loadLayout(boolean isBad) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        if (isBad) {
            inflater.inflate(R.layout.layout_flatten_bad, container, true);
        } else {
            inflater.inflate(R.layout.layout_flatten_good, container, true);
        }
    }
}