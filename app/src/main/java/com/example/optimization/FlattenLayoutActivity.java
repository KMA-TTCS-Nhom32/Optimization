package com.example.optimization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class FlattenLayoutActivity extends AppCompatActivity {

    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flatten);

        container = findViewById(R.id.container);

        // Nút BAD (Linear Nested)
        findViewById(R.id.btnShowBad).setOnClickListener(v -> loadLayout(1));

        // Nút Better (Relative)
        findViewById(R.id.btnShowRelative).setOnClickListener(v -> loadLayout(2));

        // Nút Best (Constraint)
        findViewById(R.id.btnShowConstraint).setOnClickListener(v -> loadLayout(3));

        // Mặc định load Bad
        loadLayout(1);
    }

    private void loadLayout(int type) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        switch (type) {
            case 1: // BAD
                inflater.inflate(R.layout.layout_flatten_bad, container, true);
                break;
            case 2: // BETTER (Relative)
                inflater.inflate(R.layout.layout_flatten_good, container, true); // layout_flatten_good cũ là Relative
                break;
            case 3: // BEST (Constraint)
                inflater.inflate(R.layout.layout_flatten_constraint, container, true);
                break;
        }
    }
}