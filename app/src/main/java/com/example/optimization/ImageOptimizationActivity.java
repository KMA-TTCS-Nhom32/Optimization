package com.example.optimization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ImageOptimizationActivity extends AppCompatActivity {

    // Khai báo 3 cặp view riêng biệt
    private ImageView imgHeavy, imgRGB, imgResize;
    private TextView tvInfoHeavy, tvInfoRGB, tvInfoResize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_opt);

        // Ánh xạ View
        imgHeavy = findViewById(R.id.imgHeavy);
        tvInfoHeavy = findViewById(R.id.tvInfoHeavy);

        imgRGB = findViewById(R.id.imgRGB);
        tvInfoRGB = findViewById(R.id.tvInfoRGB);

        imgResize = findViewById(R.id.imgResize);
        tvInfoResize = findViewById(R.id.tvInfoResize);

        // 1. NÚT LOAD GỐC
        findViewById(R.id.btnHeavy).setOnClickListener(v -> {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture_heavy);
            displayInfo(bitmap, imgHeavy, tvInfoHeavy);
        });

        // 2. NÚT LOAD RGB_565 (Thay thế Hardware Bitmap cũ)
        findViewById(R.id.btnRGB).setOnClickListener(v -> {
            // Load gốc trước
            Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.texture_heavy);
            // Ép kiểu sang RGB_565 (Giảm 50% RAM)
            Bitmap rgb565 = original.copy(Bitmap.Config.RGB_565, false);
            original.recycle(); // Xóa gốc đi

            displayInfo(rgb565, imgRGB, tvInfoRGB);
        });

        // 3. NÚT LOAD RESIZED (DOWNSAMPLING)
        findViewById(R.id.btnResize).setOnClickListener(v -> {
            // Resize về 300x300
            Bitmap bitmap = decodeSampledBitmap(R.drawable.texture_heavy, 300, 300);
            displayInfo(bitmap, imgResize, tvInfoResize);
        });
    }

    // Hàm hiển thị thông tin chung cho cả 3 trường hợp
    private void displayInfo(Bitmap bitmap, ImageView imageView, TextView textView) {
        imageView.setImageBitmap(bitmap);

        int byteCount = bitmap.getAllocationByteCount();
        float mbCount = byteCount / (1024f * 1024f);

        textView.setText("RAM: " + String.format("%.2f", mbCount) + " MB | " +
                "Size: " + bitmap.getWidth() + "x" + bitmap.getHeight() + " | " +
                "Config: " + bitmap.getConfig());
    }

    // --- Các hàm hỗ trợ Resize (Giữ nguyên) ---
    private Bitmap decodeSampledBitmap(int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(), resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}