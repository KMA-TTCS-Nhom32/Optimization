package com.example.optimization;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class OpenGLSelectorActivity extends Activity {

    // Hằng số định danh kịch bản
    public static final int SCENARIO_CPU_BATCHING = 1;
    public static final int SCENARIO_GPU_SHADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl_selector);

        Button btnCpu = findViewById(R.id.btnScenarioCpu); // ID mới
        Button btnGpu = findViewById(R.id.btnScenarioGpu); // ID mới

        // Kịch bản 1: CPU Batching
        btnCpu.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenGLDemoActivity.class);
            intent.putExtra("SCENARIO_TYPE", SCENARIO_CPU_BATCHING);
            startActivity(intent);
        });

        // Kịch bản 2: GPU Shader
        btnGpu.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenGLDemoActivity.class);
            intent.putExtra("SCENARIO_TYPE", SCENARIO_GPU_SHADER);
            startActivity(intent);
        });
    }
}