package com.example.optimization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadDemoActivity - Demo xử lý Background Thread
 *
 * Bài toán thực tế: Áp dụng filter lên ảnh
 * - SAI: Chạy trên UI Thread → App đơ, ANR
 * - ĐÚNG: Dùng ExecutorService → UI vẫn mượt
 *
 * Các filter demo:
 * 1. Grayscale (đen trắng)
 * 2. Blur (làm mờ)
 * 3. Brightness (tăng sáng)
 * 4. Sepia (tone nâu cổ điển)
 */
public class ThreadDemoActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvStatus;
    private TextView tvInfo;
    private ProgressBar progressBar;
    private Button btnUIThread;
    private Button btnBackgroundThread;
    private Button btnReset;

    private Bitmap originalBitmap;
    private ExecutorService executorService;
    private Handler mainHandler;

    // Đếm thời gian
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_demo);

        initViews();
        setupBitmap();
        setupButtons();

        // Tạo ExecutorService với thread pool
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());

        showIntroInfo();
    }

    private void initViews() {
        imageView = findViewById(R.id.imageView);
        tvStatus = findViewById(R.id.tvStatus);
        tvInfo = findViewById(R.id.tvInfo);
        progressBar = findViewById(R.id.progressBar);
        btnUIThread = findViewById(R.id.btnUIThread);
        btnBackgroundThread = findViewById(R.id.btnBackgroundThread);
        btnReset = findViewById(R.id.btnReset);
    }

    private void setupBitmap() {
        // Tạo bitmap lớn (1024x1024) với gradient để demo
        originalBitmap = createLargeBitmap(1024, 1024);
        imageView.setImageBitmap(originalBitmap);
    }

    /**
     * Tạo bitmap lớn với pattern đẹp để demo
     */
    private Bitmap createLargeBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Vẽ gradient background
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (x * 255) / width;
                int g = (y * 255) / height;
                int b = 128;
                bitmap.setPixel(x, y, Color.rgb(r, g, b));
            }
        }

        // Vẽ các hình tròn
        paint.setAntiAlias(true);
        for (int i = 0; i < 10; i++) {
            int cx = (width / 11) * (i + 1);
            int cy = height / 2;
            int radius = 80;

            paint.setColor(Color.HSVToColor(new float[] { i * 36f, 0.8f, 0.9f }));
            canvas.drawCircle(cx, cy, radius, paint);
        }

        // Vẽ text
        paint.setColor(Color.WHITE);
        paint.setTextSize(48);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        canvas.drawText("Original Image", width / 2f, 100, paint);
        canvas.drawText(width + "×" + height + " pixels", width / 2f, height - 50, paint);

        return bitmap;
    }

    private void setupButtons() {
        // Nút xử lý trên UI Thread (SAI CÁCH)
        btnUIThread.setOnClickListener(v -> processOnUIThread());

        // Nút xử lý trên Background Thread (ĐÚNG CÁCH)
        btnBackgroundThread.setOnClickListener(v -> processOnBackgroundThread());

        // Nút reset
        btnReset.setOnClickListener(v -> resetImage());
    }

    private void showIntroInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("🧵 UI THREAD VS BACKGROUND THREAD\n");
        sb.append("════════════════════════════════════\n\n");
        sb.append("Vấn đề: Xử lý ảnh tốn nhiều thời gian.\n");
        sb.append("Nếu chạy trên UI Thread → App đơ!\n\n");
        sb.append("📋 BÀI TOÁN:\n");
        sb.append("• Ảnh: 1024×1024 = 1,048,576 pixels\n");
        sb.append("• Áp dụng 4 filter liên tiếp\n");
        sb.append("• Mỗi pixel xử lý nhiều phép tính\n\n");
        sb.append("👆 Nhấn nút để so sánh 2 cách xử lý!");
        tvInfo.setText(sb.toString());
    }

    /**
     * XỬ LÝ TRÊN UI THREAD (SAI CÁCH!)
     * App sẽ bị đơ trong lúc xử lý
     */
    private void processOnUIThread() {
        tvStatus.setText("⚠️ Đang xử lý trên UI Thread...");
        tvStatus.setTextColor(Color.parseColor("#F44336"));
        progressBar.setVisibility(View.VISIBLE);

        // Disable buttons
        setButtonsEnabled(false);

        startTime = System.currentTimeMillis();

        // ❌ SAI: Xử lý trực tiếp trên UI Thread
        // UI sẽ bị đơ trong suốt quá trình này!
        Bitmap result = applyAllFilters(originalBitmap.copy(Bitmap.Config.ARGB_8888, true));

        long elapsed = System.currentTimeMillis() - startTime;

        imageView.setImageBitmap(result);
        progressBar.setVisibility(View.GONE);
        tvStatus.setText("✅ Hoàn thành: " + elapsed + "ms");
        tvStatus.setTextColor(Color.parseColor("#4CAF50"));

        showUIThreadInfo(elapsed);
        setButtonsEnabled(true);
    }

    /**
     * XỬ LÝ TRÊN BACKGROUND THREAD (ĐÚNG CÁCH!)
     * UI vẫn mượt mà trong lúc xử lý
     */
    private void processOnBackgroundThread() {
        tvStatus.setText("🔄 Đang xử lý trên Background Thread...");
        tvStatus.setTextColor(Color.parseColor("#2196F3"));
        progressBar.setVisibility(View.VISIBLE);

        // Disable buttons
        setButtonsEnabled(false);

        startTime = System.currentTimeMillis();

        // ✅ ĐÚNG: Sử dụng ExecutorService để xử lý background
        executorService.execute(() -> {
            // Xử lý trên background thread
            Bitmap result = applyAllFilters(originalBitmap.copy(Bitmap.Config.ARGB_8888, true));

            long elapsed = System.currentTimeMillis() - startTime;

            // Cập nhật UI trên main thread
            mainHandler.post(() -> {
                imageView.setImageBitmap(result);
                progressBar.setVisibility(View.GONE);
                tvStatus.setText("✅ Hoàn thành: " + elapsed + "ms");
                tvStatus.setTextColor(Color.parseColor("#4CAF50"));

                showBackgroundThreadInfo(elapsed);
                setButtonsEnabled(true);
            });
        });

        // UI vẫn responsive! Có thể thêm animation ở đây
        showProcessingInfo();
    }

    /**
     * Áp dụng tất cả các filter
     * Đây là phép xử lý nặng trên hàng triệu pixels
     */
    private Bitmap applyAllFilters(Bitmap bitmap) {
        // Filter 1: Grayscale
        bitmap = applyGrayscale(bitmap);

        // Filter 2: Brightness (+30%)
        bitmap = applyBrightness(bitmap, 1.3f);

        // Filter 3: Simple Blur (3x3 kernel)
        bitmap = applySimpleBlur(bitmap);

        // Filter 4: Sepia tone
        bitmap = applySepia(bitmap);

        return bitmap;
    }

    /**
     * Filter 1: Chuyển ảnh sang đen trắng
     * Xử lý từng pixel: Gray = 0.299*R + 0.587*G + 0.114*B
     */
    private Bitmap applyGrayscale(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = src.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                // Công thức luminance chuẩn
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                src.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }
        return src;
    }

    /**
     * Filter 2: Tăng/giảm độ sáng
     */
    private Bitmap applyBrightness(Bitmap src, float factor) {
        int width = src.getWidth();
        int height = src.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = src.getPixel(x, y);
                int r = Math.min(255, (int) (Color.red(pixel) * factor));
                int g = Math.min(255, (int) (Color.green(pixel) * factor));
                int b = Math.min(255, (int) (Color.blue(pixel) * factor));
                src.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
        return src;
    }

    /**
     * Filter 3: Simple box blur (3x3)
     * Mỗi pixel = trung bình của 9 pixel xung quanh
     */
    private Bitmap applySimpleBlur(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumR = 0, sumG = 0, sumB = 0;

                // Lấy 9 pixel xung quanh
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int pixel = src.getPixel(x + dx, y + dy);
                        sumR += Color.red(pixel);
                        sumG += Color.green(pixel);
                        sumB += Color.blue(pixel);
                    }
                }

                // Trung bình
                result.setPixel(x, y, Color.rgb(sumR / 9, sumG / 9, sumB / 9));
            }
        }
        return result;
    }

    /**
     * Filter 4: Sepia tone (màu nâu cổ điển)
     */
    private Bitmap applySepia(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = src.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                // Công thức Sepia
                int newR = Math.min(255, (int) (0.393 * r + 0.769 * g + 0.189 * b));
                int newG = Math.min(255, (int) (0.349 * r + 0.686 * g + 0.168 * b));
                int newB = Math.min(255, (int) (0.272 * r + 0.534 * g + 0.131 * b));

                src.setPixel(x, y, Color.rgb(newR, newG, newB));
            }
        }
        return src;
    }

    private void resetImage() {
        imageView.setImageBitmap(originalBitmap);
        tvStatus.setText("🖼️ Ảnh gốc");
        tvStatus.setTextColor(Color.parseColor("#666666"));
        showIntroInfo();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnUIThread.setEnabled(enabled);
        btnBackgroundThread.setEnabled(enabled);
        btnReset.setEnabled(enabled);
    }

    private void showProcessingInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("🔄 ĐANG XỬ LÝ...\n");
        sb.append("════════════════════════════════════\n\n");
        sb.append("Background thread đang chạy!\n\n");
        sb.append("👆 Thử scroll hoặc nhấn nút khác\n");
        sb.append("   để thấy UI vẫn responsive!\n\n");
        sb.append("📋 Các filter đang áp dụng:\n");
        sb.append("1. Grayscale (đen trắng)\n");
        sb.append("2. Brightness +30%\n");
        sb.append("3. Box Blur 3×3\n");
        sb.append("4. Sepia tone");
        tvInfo.setText(sb.toString());
    }

    private void showUIThreadInfo(long elapsed) {
        StringBuilder sb = new StringBuilder();
        sb.append("❌ XỬ LÝ TRÊN UI THREAD\n");
        sb.append("════════════════════════════════════\n\n");
        sb.append("⏱️ Thời gian: " + elapsed + "ms\n\n");
        sb.append("⚠️ VẤN ĐỀ:\n");
        sb.append("• UI bị ĐỨNG trong " + elapsed + "ms\n");
        sb.append("• Không thể scroll, nhấn nút\n");
        sb.append("• Nếu > 5 giây → ANR (App Not Responding)\n");
        sb.append("• Trải nghiệm người dùng RẤT TỆ!\n\n");
        sb.append("📝 CODE SAI:\n");
        sb.append("─────────────────────────\n");
        sb.append("// Chạy trực tiếp trên UI Thread\n");
        sb.append("Bitmap result = applyFilter(bitmap);\n");
        sb.append("imageView.setImageBitmap(result);\n");
        sb.append("─────────────────────────");
        tvInfo.setText(sb.toString());
    }

    private void showBackgroundThreadInfo(long elapsed) {
        StringBuilder sb = new StringBuilder();
        sb.append("✅ XỬ LÝ TRÊN BACKGROUND THREAD\n");
        sb.append("════════════════════════════════════\n\n");
        sb.append("⏱️ Thời gian: " + elapsed + "ms\n\n");
        sb.append("✅ ƯU ĐIỂM:\n");
        sb.append("• UI vẫn MƯỢT MÀ suốt quá trình\n");
        sb.append("• Có thể hiển thị progress\n");
        sb.append("• Không lo ANR\n");
        sb.append("• Trải nghiệm người dùng TỐT!\n\n");
        sb.append("📝 CODE ĐÚNG:\n");
        sb.append("─────────────────────────\n");
        sb.append("ExecutorService executor = \n");
        sb.append("  Executors.newFixedThreadPool(2);\n\n");
        sb.append("executor.execute(() -> {\n");
        sb.append("  // Xử lý background\n");
        sb.append("  Bitmap result = applyFilter(bitmap);\n");
        sb.append("  \n");
        sb.append("  // Cập nhật UI trên main thread\n");
        sb.append("  mainHandler.post(() -> {\n");
        sb.append("    imageView.setImageBitmap(result);\n");
        sb.append("  });\n");
        sb.append("});\n");
        sb.append("─────────────────────────");
        tvInfo.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
        }
    }
}