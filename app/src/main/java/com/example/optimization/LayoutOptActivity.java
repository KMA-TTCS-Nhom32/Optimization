package com.example.optimization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LayoutOptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_opt);

        // Nút 1: Làm phẳng Layout
        findViewById(R.id.btnFlatten).setOnClickListener(v ->
                startActivity(new Intent(this, FlattenLayoutActivity.class)));

        // Nút 2: Tối ưu cấu trúc (Merge/Include)
        findViewById(R.id.btnMerge).setOnClickListener(v ->
                startActivity(new Intent(this, MergeLayoutActivity.class)));

        // Nút 3: ViewStub (Lazy Loading)
        findViewById(R.id.btnViewStub).setOnClickListener(v ->
                startActivity(new Intent(this, ViewStubActivity.class)));

    }
}