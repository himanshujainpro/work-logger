package com.worklogger.ui.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.worklogger.R;
import com.worklogger.data.model.firebase.SingleLog;
import com.worklogger.ui.adapters.LogAdapter;
import com.worklogger.utils.UI;

public class MyLogsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference userLogs = db.collection("users").document(mAuth.getUid())
            .collection("logs");

    private LogAdapter adapter;
    private UI mUI;
    private ShimmerFrameLayout mShimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_logs);
        mUI = new UI(this);
        setUpRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setUpRecyclerView() {
//        mUI.showProgressDialog();
        Query baseQuery = userLogs.orderBy("mDate", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<SingleLog> options = new FirestorePagingOptions.Builder<SingleLog>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, SingleLog.class)
                .build();

        mShimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        RecyclerView recyclerView = findViewById(R.id.rv_my_logs);
        adapter = new LogAdapter(options, mUI, mShimmerFrameLayout, recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerFrameLayout.stopShimmerAnimation();
    }
}
