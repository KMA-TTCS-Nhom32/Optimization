package com.example.optimization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class JankActivity extends AppCompatActivity {

    private ListView listView;
    // Tăng số lượng item lên 1000 để tha hồ cuộn
    private static final int ITEM_COUNT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jank);

        listView = findViewById(R.id.listView);

        findViewById(R.id.btnShowBad).setOnClickListener(v -> setAdapter(true));
        findViewById(R.id.btnShowGood).setOnClickListener(v -> setAdapter(false));

        // Mặc định load Bad
        setAdapter(true);
    }

    private void setAdapter(boolean isBad) {
        JankAdapter adapter = new JankAdapter(isBad);
        listView.setAdapter(adapter);
        String mode = isBad ? "BAD Mode (Heavy Layout)" : "GOOD Mode (Flat Layout)";
        Toast.makeText(this, mode, Toast.LENGTH_SHORT).show();
    }

    // Adapter tuỳ chỉnh cho ListView
    private class JankAdapter extends BaseAdapter {
        private final boolean isBad;
        private final LayoutInflater inflater;

        public JankAdapter(boolean isBad) {
            this.isBad = isBad;
            this.inflater = LayoutInflater.from(JankActivity.this);
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TRICK ĐỂ DEMO:
            // Nếu là BAD: Chúng ta cố tình KHÔNG tái sử dụng convertView (null)
            // và inflate layout nặng nề -> Ép CPU tính toán lại từ đầu mỗi khi cuộn.

            // Nếu là GOOD: Chúng ta làm chuẩn, tái sử dụng view và dùng layout nhẹ.

            View itemView = convertView;

            if (isBad) {
                // BAD CASE: Luôn inflate mới view nặng nề
                itemView = inflater.inflate(R.layout.item_jank_bad, parent, false);
            } else {
                // GOOD CASE: Tái sử dụng view nếu có thể (Cơ chế caching của Android)
                if (itemView == null) {
                    itemView = inflater.inflate(R.layout.item_jank_good, parent, false);
                }
            }

            return itemView;
        }
    }
}