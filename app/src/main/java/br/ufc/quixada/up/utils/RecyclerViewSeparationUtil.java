package br.ufc.quixada.up.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewSeparationUtil extends RecyclerView.ItemDecoration {

    private final int rightPadding;

    public RecyclerViewSeparationUtil(int edgePadding) {
        this.rightPadding = edgePadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = state.getItemCount();

        final int itemPosition = parent.getChildAdapterPosition(view);

        // no position, leave it alone
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // last item
        if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.set(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
        // every other item
        else {
            outRect.set(view.getPaddingLeft(), view.getPaddingTop(), rightPadding, view.getPaddingBottom());
        }
    }
}