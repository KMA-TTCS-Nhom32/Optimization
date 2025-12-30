package com.example.optimization;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class OpenGLDemoActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private MyGLRenderer glRenderer;
    private TextView infoText;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int currentScenario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy thông tin kịch bản từ Activity trước (DEMO_TYPE)
        currentScenario = getIntent().getIntExtra("DEMO_TYPE", OpenGLSelectorActivity.SCENARIO_CPU_BATCHING);

        // Setup OpenGL View
        FrameLayout frameLayout = new FrameLayout(this);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glRenderer = new MyGLRenderer(this);

        // Thiết lập chế độ mặc định ban đầu
        if (currentScenario == OpenGLSelectorActivity.SCENARIO_CPU_BATCHING) {
            glRenderer.setDemoMode(MyGLRenderer.MODE_CPU_LOOP);
        } else {
            glRenderer.setDemoMode(MyGLRenderer.MODE_GPU_ANIMATION);
        }

        glSurfaceView.setRenderer(glRenderer);
        frameLayout.addView(glSurfaceView);

        // Load giao diện điều khiển (nút bấm)
        View controls = getLayoutInflater().inflate(R.layout.activity_opengl_control, frameLayout, false);
        frameLayout.addView(controls);
        setContentView(frameLayout);

        // Setup các nút bấm
        infoText = controls.findViewById(R.id.tvInfo);

        // Lưu ý: ID nút bấm phải khớp với file activity_opengl_control.xml
        // Tôi đang dùng ID từ code cũ của bạn: btnTextureBad/Good
        // Nếu bạn đã đổi ID trong XML thì hãy sửa lại ở đây cho khớp
        Button btnBad = controls.findViewById(R.id.btnBad);
        Button btnGood = controls.findViewById(R.id.btnGood);

        setupButtons(btnBad, btnGood);
        startMonitoring();
    }

    private void setupButtons(Button btnBad, Button btnGood) {
        if (currentScenario == OpenGLSelectorActivity.SCENARIO_CPU_BATCHING) {
            // --- KỊCH BẢN 1: CPU (Batching) ---
            btnBad.setText("Loop (Bad)");
            btnGood.setText("Batch (Good)");

            btnBad.setOnClickListener(v -> glRenderer.setDemoMode(MyGLRenderer.MODE_CPU_LOOP));
            btnGood.setOnClickListener(v -> glRenderer.setDemoMode(MyGLRenderer.MODE_CPU_BATCH));

        } else {
            // --- KỊCH BẢN 2: VERTEX SHADER (Animation) ---
            btnBad.setText("Animation (Bad)");
            btnGood.setText("Static (Good)");

            btnBad.setOnClickListener(v -> glRenderer.setDemoMode(MyGLRenderer.MODE_GPU_ANIMATION));
            btnGood.setOnClickListener(v -> glRenderer.setDemoMode(MyGLRenderer.MODE_GPU_NORMAL));
        }
    }

    private void startMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String modeName = (currentScenario == OpenGLSelectorActivity.SCENARIO_CPU_BATCHING) ? "CPU Demo" : "GPU Demo";
                infoText.setText(modeName + "\n" + glRenderer.getFPS());
                handler.postDelayed(this, 500);
            }
        }, 500);
    }
}