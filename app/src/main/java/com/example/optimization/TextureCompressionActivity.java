package com.example.optimization;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TextureCompressionActivity extends AppCompatActivity {

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_compression);

        tvInfo = findViewById(R.id.tvInfo);

        // Overview button
        Button btnOverview = findViewById(R.id.btnOverview);
        btnOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverview();
            }
        });

        // ETC2 button
        Button btnETC2 = findViewById(R.id.btnETC2);
        btnETC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showETC2Info();
            }
        });

        // ASTC button
        Button btnASTC = findViewById(R.id.btnASTC);
        btnASTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showASTCInfo();
            }
        });

        // Comparison button
        Button btnCompare = findViewById(R.id.btnCompare);
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComparison();
            }
        });

        showOverview();
    }

    private void showOverview() {
        StringBuilder info = new StringBuilder();
        info.append("📦 TEXTURE COMPRESSION\n\n");
        info.append("Tại sao cần nén texture?\n\n");
        info.append("❌ Texture KHÔNG nén:\n");
        info.append("• 256x256 RGBA8888\n");
        info.append("• = 256 KB bộ nhớ!\n\n");
        info.append("✅ Texture ĐÃ nén:\n");
        info.append("• Giảm 4-8x kích thước\n");
        info.append("• GPU giải nén trực tiếp\n");
        info.append("• Không cần CPU xử lý\n\n");
        info.append("💡 Lợi ích:\n");
        info.append("• Tiết kiệm bộ nhớ GPU\n");
        info.append("• Giảm băng thông\n");
        info.append("• Tăng hiệu suất render\n");
        info.append("• Giảm kích thước APK\n");
        info.append("• Tiết kiệm pin");
        tvInfo.setText(info.toString());
    }

    private void showETC2Info() {
        StringBuilder info = new StringBuilder();
        info.append("🔷 ETC2 (Ericsson Texture\n");
        info.append("   Compression 2)\n\n");
        info.append("📋 Đặc điểm:\n");
        info.append("• Chuẩn trong OpenGL ES 3.0\n");
        info.append("• Hỗ trợ RGB và RGBA\n");
        info.append("• Tỷ lệ nén 6:1\n");
        info.append("• Block size: 4x4 pixels\n\n");
        info.append("📊 Định dạng:\n");
        info.append("• ETC2_RGB8: 4 bits/pixel\n");
        info.append("• ETC2_RGBA8: 8 bits/pixel\n\n");
        info.append("💡 Ưu điểm:\n");
        info.append("• Hỗ trợ rộng rãi\n");
        info.append("• Chất lượng tốt\n");
        info.append("• Cân bằng size/quality\n\n");
        info.append("⚠️ Nhược điểm:\n");
        info.append("• Không hỗ trợ HDR\n");
        info.append("• Block artifacts khi zoom");
        tvInfo.setText(info.toString());
    }

    private void showASTCInfo() {
        StringBuilder info = new StringBuilder();
        info.append("🔶 ASTC (Adaptive Scalable\n");
        info.append("   Texture Compression)\n\n");
        info.append("📋 Đặc điểm:\n");
        info.append("• Định dạng hiện đại nhất\n");
        info.append("• Hỗ trợ HDR\n");
        info.append("• Block size linh hoạt:\n");
        info.append("  4x4 → 12x12 pixels\n\n");
        info.append("📊 Tỷ lệ nén:\n");
        info.append("• 4x4: 8 bits/pixel\n");
        info.append("• 6x6: 3.56 bits/pixel\n");
        info.append("• 8x8: 2 bits/pixel\n");
        info.append("• 12x12: 0.89 bits/pixel\n\n");
        info.append("💡 Ưu điểm:\n");
        info.append("• Chất lượng cao nhất\n");
        info.append("• Linh hoạt nhất\n");
        info.append("• Hỗ trợ HDR, 3D textures\n\n");
        info.append("⚠️ Yêu cầu:\n");
        info.append("• OpenGL ES 3.1+\n");
        info.append("• GPU hỗ trợ extension");
        tvInfo.setText(info.toString());
    }

    private void showComparison() {
        StringBuilder info = new StringBuilder();
        info.append("📊 SO SÁNH CÁC ĐỊNH DẠNG\n\n");
        info.append("Texture 512x512 RGBA:\n\n");
        info.append("┌─────────────────────────┐\n");
        info.append("│ Không nén: 1024 KB      │\n");
        info.append("├─────────────────────────┤\n");
        info.append("│ ETC2:      128 KB  (8x) │\n");
        info.append("├─────────────────────────┤\n");
        info.append("│ ASTC 4x4:  256 KB  (4x) │\n");
        info.append("├─────────────────────────┤\n");
        info.append("│ ASTC 8x8:  64 KB  (16x) │\n");
        info.append("└─────────────────────────┘\n\n");
        info.append("📱 Khuyến nghị:\n\n");
        info.append("• UI elements → ETC2\n");
        info.append("• Photo-realistic → ASTC 4x4\n");
        info.append("• Subtle textures → ASTC 8x8\n");
        info.append("• HDR/Lightmaps → ASTC + HDR\n\n");
        info.append("⚡ Hiệu năng:\n");
        info.append("• Texture nén load nhanh hơn\n");
        info.append("• Cache GPU hiệu quả hơn\n");
        info.append("• Bandwidth giảm đáng kể");
        tvInfo.setText(info.toString());
    }
}