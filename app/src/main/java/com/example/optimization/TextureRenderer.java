package com.example.optimization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * TextureRenderer - Renderer với hỗ trợ Texture Compression thật
 *
 * Modes:
 * 1. RGBA8888 - Không nén (4 bytes/pixel)
 * 2. RGB565 - Giảm 50% (2 bytes/pixel)
 * 3. ETC1 - Nén thật bằng Android ETC1 API
 * 4. ETC2 - Nén thật (cần OpenGL ES 3.0)
 */
public class TextureRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private TexturedQuad texturedQuad;
    private Bitmap sourceBitmap;

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int currentMode = TexturedQuad.MODE_RGBA8888;
    private boolean needsTextureUpdate = false;

    // Listener for UI updates
    public interface OnInfoUpdateListener {
        void onMemoryInfoUpdate(int memoryBytes, String ratio, String bpp);

        void onTextureLoaded(int width, int height, long loadTimeMs);

        void onGPUInfoReady(String gpuInfo);
    }

    private OnInfoUpdateListener listener;
    private Handler mainHandler;

    public TextureRenderer(Context context) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setOnInfoUpdateListener(OnInfoUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Create quad
        texturedQuad = new TexturedQuad();

        // Create source bitmap
        sourceBitmap = createDemoBitmap(512, 512);

        // Load initial texture
        texturedQuad.loadTextureRGBA8888(sourceBitmap);

        // Notify GPU info
        notifyGPUInfo();
        notifyMemoryInfo();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Update texture if mode changed
        if (needsTextureUpdate) {
            updateTexture();
            needsTextureUpdate = false;
        }

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        texturedQuad.draw(mvpMatrix);
    }

    /**
     * Set compression mode
     */
    public void setCompressionMode(int mode) {
        currentMode = mode;
        needsTextureUpdate = true;
    }

    /**
     * Update texture based on current mode
     */
    private void updateTexture() {
        if (sourceBitmap == null || texturedQuad == null)
            return;

        switch (currentMode) {
            case TexturedQuad.MODE_RGBA8888:
                texturedQuad.loadTextureRGBA8888(sourceBitmap);
                break;
            case TexturedQuad.MODE_RGB565:
                texturedQuad.loadTextureRGB565(sourceBitmap);
                break;
            case TexturedQuad.MODE_ETC1:
                texturedQuad.loadTextureETC1(sourceBitmap);
                break;
            case TexturedQuad.MODE_ETC2:
                texturedQuad.loadTextureETC2(sourceBitmap);
                break;
        }

        notifyMemoryInfo();
        notifyTextureLoaded();
    }

    /**
     * Create demo bitmap with pattern
     */
    private Bitmap createDemoBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Gradient background
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (x * 255) / width;
                int g = (y * 255) / height;
                int b = 128;
                bitmap.setPixel(x, y, Color.rgb(r, g, b));
            }
        }

        // Draw circles
        paint.setAntiAlias(true);
        for (int i = 0; i < 8; i++) {
            int cx = (width / 9) * (i + 1);
            int cy = height / 2;
            int radius = width / 15;
            paint.setColor(Color.HSVToColor(new float[] { i * 45f, 0.8f, 0.9f }));
            canvas.drawCircle(cx, cy, radius, paint);
        }

        // Draw text
        paint.setColor(Color.WHITE);
        paint.setTextSize(width / 12f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        canvas.drawText("Texture " + width + "×" + height, width / 2f, height / 4f, paint);
        canvas.drawText("Compression Demo", width / 2f, height * 3 / 4f, paint);

        return bitmap;
    }

    private void notifyMemoryInfo() {
        if (listener == null || texturedQuad == null)
            return;

        mainHandler.post(() -> {
            listener.onMemoryInfoUpdate(
                    texturedQuad.getActualMemoryUsage(),
                    texturedQuad.getCompressionRatio(),
                    texturedQuad.getBitsPerPixel());
        });
    }

    private void notifyTextureLoaded() {
        if (listener == null || texturedQuad == null)
            return;

        mainHandler.post(() -> {
            listener.onTextureLoaded(
                    texturedQuad.getTextureWidth(),
                    texturedQuad.getTextureHeight(),
                    texturedQuad.getLoadTimeMs());
        });
    }

    private void notifyGPUInfo() {
        if (listener == null || texturedQuad == null)
            return;

        mainHandler.post(() -> {
            listener.onGPUInfoReady(texturedQuad.getGPUInfo());
        });
    }

    // Getters
    public String getModeName() {
        return texturedQuad != null ? texturedQuad.getModeName() : "";
    }

    public int getMemoryUsage() {
        return texturedQuad != null ? texturedQuad.getActualMemoryUsage() : 0;
    }

    public String getModeDetails() {
        if (texturedQuad == null)
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append("📊 CHI TIẾT MODE: ").append(texturedQuad.getModeName()).append("\n");
        sb.append("════════════════════════════════════\n\n");

        sb.append("📐 Texture: ").append(texturedQuad.getTextureWidth());
        sb.append("×").append(texturedQuad.getTextureHeight()).append("\n");
        sb.append("🎯 ").append(texturedQuad.getBitsPerPixel()).append("\n");
        sb.append("📦 Memory THẬT: ").append(formatBytes(texturedQuad.getActualMemoryUsage())).append("\n");
        sb.append("⚡ Load time: ").append(texturedQuad.getLoadTimeMs()).append("ms\n\n");

        sb.append("GPU INFO:\n");
        sb.append(texturedQuad.getGPUInfo()).append("\n\n");

        switch (texturedQuad.getCurrentMode()) {
            case TexturedQuad.MODE_RGBA8888:
                sb.append("⚠️ Không nén - tốn bộ nhớ nhất\n");
                sb.append("Mỗi pixel = 4 bytes (RGBA)");
                break;
            case TexturedQuad.MODE_RGB565:
                sb.append("✅ Giảm 50% bộ nhớ\n");
                sb.append("Mỗi pixel = 2 bytes (RGB)\n");
                sb.append("Không có Alpha channel");
                break;
            case TexturedQuad.MODE_ETC1:
                sb.append("✅ NÉN THẬT bằng Android ETC1\n");
                sb.append("Block compression 4×4\n");
                sb.append("GPU giải nén trực tiếp\n");
                sb.append("Không có Alpha channel");
                break;
            case TexturedQuad.MODE_ETC2:
                sb.append("✅ ETC2 - OpenGL ES 3.0\n");
                sb.append("Hỗ trợ Alpha\n");
                sb.append("Chất lượng cao hơn ETC1");
                break;
        }

        return sb.toString();
    }

    private String formatBytes(int bytes) {
        if (bytes >= 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else if (bytes >= 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return bytes + " B";
    }
}