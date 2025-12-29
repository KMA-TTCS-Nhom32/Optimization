package com.example.optimization; // Đảm bảo dòng này đúng với package của bạn

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ViewStubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stub);

        Button btnLoad = findViewById(R.id.btnLoadMap);
        ViewStub viewStub = findViewById(R.id.stub_map);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewStub.getParent() != null) {
                    viewStub.inflate();
                    btnLoad.setEnabled(false);


                    btnLoad.setText("Đã tải xong!");
                }
            }
        });
    }
}