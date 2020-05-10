package com.hmisdoctor.callbacks;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hmisdoctor.ui.configuration.model.ConfigResponseContent;
import com.hmisdoctor.ui.configuration.view.ConfigFavRecyclerAdapter;

import org.jetbrains.annotations.NotNull;

public class ItemMoveMyPumpCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveMyPumpCallback(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }



    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ConfigFavRecyclerAdapter.MyViewHolder) {
                ConfigFavRecyclerAdapter.MyViewHolder myViewHolder=
                        (ConfigFavRecyclerAdapter.MyViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ConfigFavRecyclerAdapter.MyViewHolder) {
            ConfigFavRecyclerAdapter.MyViewHolder myViewHolder=
                    (ConfigFavRecyclerAdapter.MyViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(ConfigFavRecyclerAdapter.MyViewHolder myViewHolder);
        void onRowClear(ConfigFavRecyclerAdapter.MyViewHolder myViewHolder);


    }

}
