package com.example.dress_den.presentation.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            // Add margin to all sides for grid layout
            if (parent.layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
                left = margin
                top = margin
                right = margin
                bottom = margin
            } else {
                // For horizontal linear layout, add margin to left and right
                if (parent.layoutManager?.canScrollHorizontally() == true) {
                    left = margin
                    right = margin
                }
                // For vertical linear layout, add margin to top and bottom
                if (parent.layoutManager?.canScrollVertically() == true) {
                    top = margin
                    bottom = margin
                }
            }
        }
    }
}
