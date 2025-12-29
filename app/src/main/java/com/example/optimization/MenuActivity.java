package com.example.optimization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.btnLayoutOpt).setOnClickListener(v -> {

            Intent intent = new Intent(MenuActivity.this, LayoutOptActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnOverdraw).setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, OverdrawActivity.class);
            startActivity(intent);
        });

        // Trong onCreate của MenuActivity.java

        findViewById(R.id.btnJank).setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, JankActivity.class);
            startActivity(intent);
        });

        View.OnClickListener featureDevelopingListener = v -> {
            Toast.makeText(MenuActivity.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        };

        findViewById(R.id.btnOpenGLES).setOnClickListener(featureDevelopingListener);
    }
}
