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
        Button btnImageOpt = findViewById(R.id.btnImageOpt); // MỚI: Ánh xạ nút mới

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

        // 5. MỚI: Chuyển sang màn hình Tối ưu hình ảnh
        btnImageOpt.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ImageOptimizationActivity.class);
            startActivity(intent);
        });

        Button btnThread = findViewById(R.id.btnThread);

        btnThread.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, ThreadDemoActivity.class));
        });

    }
}