package com.worklogger.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.worklogger.R;
import com.worklogger.data.model.firebase.SingleLog;
import com.worklogger.utils.UI;

public class LogAdapter extends FirestorePagingAdapter<SingleLog, LogAdapter.LogHolder> {

    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    private UI mUI;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private RecyclerView mRecyclerView;

    public LogAdapter(@NonNull FirestorePagingOptions<SingleLog> options, UI ui
    ,ShimmerFrameLayout shimmerFrameLayout,RecyclerView recyclerView) {
        super(options);
        this.mUI = ui;
        this.mRecyclerView=recyclerView;
        this.mShimmerFrameLayout=shimmerFrameLayout;
    }


    @Override
    protected void onBindViewHolder(@NonNull LogHolder holder, int position, @NonNull SingleLog model) {
        holder.textViewDate.setText(model.getmDate());
        holder.textViewCountAndUnit.setText(model.getmCount() + " " + model.getmUnit());
        holder.textViewDes.setText(model.getmDescription());
    }

    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_item, parent, false);

        return new LogHolder(view);
    }

    class LogHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewCountAndUnit;
        TextView textViewDes;

        public LogHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewCountAndUnit = itemView.findViewById(R.id.text_view_count_and_unit);
            textViewDes = itemView.findViewById(R.id.text_view_description);
        }
    }

    @Override
    protected void onError(@NonNull Exception e) {
        mShimmerFrameLayout.stopShimmerAnimation();
        mShimmerFrameLayout.setVisibility(View.GONE);
        mUI.showToast("Something went wrong");
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull LogHolder holder) {
        mShimmerFrameLayout.stopShimmerAnimation();
        mShimmerFrameLayout.setVisibility(View.GONE);
        mUI.showToast("Something went wrong");
        return super.onFailedToRecycleView(holder);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
                // The initial load has begun
                // ...
                break;
            case LOADING_MORE:
                // The adapter has started to load an additional page
                // ...
            case LOADED:
                // The previous load (either initial or additional) completed
                // ...
                mShimmerFrameLayout.stopShimmerAnimation();
                mShimmerFrameLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                break;
            case ERROR:
                // The previous load (either initial or additional) failed. Call
                // the retry() method in order to retry the load operation.
                // ...
                mShimmerFrameLayout.stopShimmerAnimation();
                mShimmerFrameLayout.setVisibility(View.GONE);
                mUI.showToast("Something went wrong");
            case FINISHED:
                mShimmerFrameLayout.stopShimmerAnimation();
                mShimmerFrameLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                break;
        }
    }
}
