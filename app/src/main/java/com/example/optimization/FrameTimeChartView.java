package com.example.optimization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class FrameTimeChartView extends View {
    private final Paint paint = new Paint();
    private final List<Long> frameTimes = new ArrayList<>();
    private static final int MAX_SAMPLES = 100; // Số cột hiển thị

    public FrameTimeChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addFrameTime(long frameTimeMs) {
        if (frameTimes.size() >= MAX_SAMPLES) {
            frameTimes.remove(0);
        }
        frameTimes.add(frameTimeMs);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (frameTimes.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float barWidth = width / MAX_SAMPLES;

        // Vẽ vạch chuẩn 16ms (60FPS) màu xanh lá
        float threshold16ms = height - (16f / 50f) * height;
        paint.setColor(0xFF00FF00);
        paint.setStrokeWidth(3f);
        canvas.drawLine(0, threshold16ms, width, threshold16ms, paint);

        for (int i = 0; i < frameTimes.size(); i++) {
            long time = frameTimes.get(i);
            // Cột cao quá 50ms thì cắt bớt
            float barHeight = (time / 50f) * height;
            if (barHeight > height) barHeight = height;

            // Màu đỏ nếu > 16ms, Xanh dương nếu mượt
            paint.setColor(time > 16 ? 0xFFFF0000 : 0xFF2196F3);

            float left = i * barWidth;
            float top = height - barHeight;
            canvas.drawRect(left, top, left + barWidth - 2, height, paint);
        }
    }
}