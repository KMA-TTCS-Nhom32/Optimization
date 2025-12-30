package com.example.optimization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class JankActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvFpsInfo;
    private FpsMonitor fpsMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jank);

        recyclerView = findViewById(R.id.recyclerView);
        tvFpsInfo = findViewById(R.id.tvFpsInfo);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Thêm dòng kẻ cho giống ListView cũ
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));

        // Start đo FPS
        fpsMonitor = new FpsMonitor(tvFpsInfo);

        // Xử lý nút bấm
        findViewById(R.id.btnShowBad).setOnClickListener(v -> setAdapter(true));
        findViewById(R.id.btnShowGood).setOnClickListener(v -> setAdapter(false));

        // Mặc định chạy Bad để thấy lag luôn
        setAdapter(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fpsMonitor.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fpsMonitor.stop();
    }

    private void setAdapter(boolean isBad) {
        JankRecyclerAdapter adapter = new JankRecyclerAdapter(isBad);
        recyclerView.setAdapter(adapter);
        String mode = isBad ? "BAD MODE: Đang lag..." : "GOOD MODE: Mượt mà!";
        Toast.makeText(this, mode, Toast.LENGTH_SHORT).show();
    }

    // --- ADAPTER ---
    private static class JankRecyclerAdapter extends RecyclerView.Adapter<JankRecyclerAdapter.ViewHolder> {
        private final boolean isBad;
        private static final int ITEM_COUNT = 1000;

        public JankRecyclerAdapter(boolean isBad) {
            this.isBad = isBad;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view;

            if (isBad) {
                // BAD TRICK: Tạo một cái vỏ rỗng (Container)
                // Để tí nữa trong onBind mình sẽ nhét layout nặng vào đây liên tục
                view = new LinearLayout(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                // GOOD: Inflate layout nhẹ và chuẩn chỉ 1 lần
                view = inflater.inflate(R.layout.item_jank_good, parent, false);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isBad) {
                // --- BAD LOGIC: Cực Lag ---
                // Mỗi khi cuộn tới item này, ta lại Inflate layout nặng và add vào
                // Đây là mô phỏng việc "Không tái sử dụng View" của ListView cũ
                ViewGroup container = (ViewGroup) holder.itemView;
                container.removeAllViews(); // Xóa cái cũ

                // Inflate layout nặng (item_jank_bad)
                LayoutInflater.from(container.getContext())
                        .inflate(R.layout.item_jank_bad, container, true);

                // Giả lập thêm tính toán nặng (nếu máy bạn quá mạnh vẫn chưa thấy lag)
                try { Thread.sleep(8); } catch (Exception e) {}
            }
            // GOOD LOGIC: Không làm gì cả, vì View đã được tạo sẵn ở onCreateViewHolder rồi
        }

        @Override
        public int getItemCount() {
            return ITEM_COUNT;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}