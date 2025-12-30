package com.example.optimization;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class OpenGLSelectorActivity extends Activity {

    // Hằng số định danh kịch bản
    public static final int SCENARIO_CPU_BATCHING = 1;
    public static final int SCENARIO_GPU_SHADER = 2;
    public static final int SCENARIO_TEXTURE_COMPRESSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl_selector);

        Button btnCpu = findViewById(R.id.btnScenarioCpu);
        Button btnGpu = findViewById(R.id.btnScenarioGpu);
        Button btnTexture = findViewById(R.id.btnScenarioTexture);

        // Kịch bản 1: CPU Batching (Dẫn sang OpenGLDemoActivity)
        btnCpu.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenGLDemoActivity.class);
            intent.putExtra("DEMO_TYPE", SCENARIO_CPU_BATCHING);
            startActivity(intent);
        });

        // Kịch bản 2: GPU Shader (Dẫn sang OpenGLDemoActivity)
        btnGpu.setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenGLDemoActivity.class);
            intent.putExtra("DEMO_TYPE", SCENARIO_GPU_SHADER);
            startActivity(intent);
        });

        // Kịch bản 3: Texture Compression (Dẫn sang TextureCompressionActivity)
        btnTexture.setOnClickListener(v -> {
            Intent intent = new Intent(this, TextureCompressionActivity.class);
            startActivity(intent);
        });
    }
}