package com.example.optimization;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnLayout = findViewById(R.id.btnLayoutOpt);
        Button btnOverdraw = findViewById(R.id.btnOverdraw);
        Button btnJank = findViewById(R.id.btnJank);
        Button btnOpenGL = findViewById(R.id.btnOpenGL);

        // 1. Chuyển sang màn hình Tối ưu Layout
        btnLayout.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, LayoutOptActivity.class);
            startActivity(intent);
        });

        // 2. Chuyển sang màn hình Overdraw
        btnOverdraw.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, OverdrawActivity.class);
            startActivity(intent);
        });

        // 3. Chuyển sang màn hình Frame Rate (Jank)
        btnJank.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, JankActivity.class);
            startActivity(intent);
        });

        // 4. Chuyển sang màn hình OpenGL ES
        btnOpenGL.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, OpenGLSelectorActivity.class);
            startActivity(intent);
        });
    }
}