package com.example.optimization;

import android.view.Choreographer;
import android.widget.TextView;

public class FpsMonitor implements Choreographer.FrameCallback {
    private final TextView fpsView;
    private long lastFrameTimeNanos = 0;
    private int frameCount = 0;

    // Cập nhật text mỗi 500ms để tránh nháy màn hình liên tục
    private long lastUpdateTimestamp = 0;
    private static final long UPDATE_INTERVAL_MS = 500;

    public FpsMonitor(TextView fpsView) {
        this.fpsView = fpsView;
    }

    public void start() {
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void stop() {
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (lastFrameTimeNanos > 0) {
            // Tính thời gian giữa 2 frame (ms)
            long diffMs = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000;

            frameCount++;
            long timeSinceLastUpdate = (System.currentTimeMillis() - lastUpdateTimestamp);

            if (timeSinceLastUpdate > UPDATE_INTERVAL_MS) {
                // Tính FPS trung bình trong khoảng thời gian qua
                // FPS chuẩn là 60. Nếu thấp hơn nghĩa là JANK.
                double fps = frameCount * 1000.0 / timeSinceLastUpdate;

                // Render time ước tính (càng thấp càng tốt, lý tưởng < 16ms)
                long renderTime = (timeSinceLastUpdate / frameCount);

                String color = (fps >= 55) ? "#00AA00" : (fps >= 30 ? "#FFA500" : "#FF0000"); // Xanh - Cam - Đỏ

                fpsView.setText(String.format("FPS: %.1f | Render: %dms", fps, renderTime));
                fpsView.setTextColor(android.graphics.Color.parseColor(color));

                frameCount = 0;
                lastUpdateTimestamp = System.currentTimeMillis();
            }
        }

        lastFrameTimeNanos = frameTimeNanos;
        // Đăng ký callback cho frame tiếp theo
        Choreographer.getInstance().postFrameCallback(this);
    }
}