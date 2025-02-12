package com.example.dress_den.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

object RecyclerUtils {

    fun setVerticalLayout(recyclerView: RecyclerView, reverseLayout: Boolean = false) {
        recyclerView.layoutManager = LinearLayoutManager(
            recyclerView.context,
            LinearLayoutManager.VERTICAL,
            reverseLayout
        )
    }

    fun setHorizontalLayout(recyclerView: RecyclerView, reverseLayout: Boolean = false) {
        recyclerView.layoutManager = LinearLayoutManager(
            recyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            reverseLayout
        )
    }

    fun setGridLayout(recyclerView: RecyclerView, spanCount: Int) {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, spanCount)
    }

    fun setStaggeredGridLayout(recyclerView: RecyclerView, spanCount: Int, isVertical: Boolean = true) {
        recyclerView.layoutManager = StaggeredGridLayoutManager(
            spanCount,
            if (isVertical) StaggeredGridLayoutManager.VERTICAL else StaggeredGridLayoutManager.HORIZONTAL
        )
    }

    fun addDivider(recyclerView: RecyclerView, orientation: Int = DividerItemDecoration.VERTICAL) {
        val divider = DividerItemDecoration(recyclerView.context, orientation)
        recyclerView.addItemDecoration(divider)
    }

    fun addCustomDivider(
        recyclerView: RecyclerView,
        @DrawableRes drawableRes: Int,
        orientation: Int = DividerItemDecoration.VERTICAL
    ) {
        val drawable = ContextCompat.getDrawable(recyclerView.context, drawableRes)
        drawable?.let {
            val divider = DividerItemDecoration(recyclerView.context, orientation)
            divider.setDrawable(it)
            recyclerView.addItemDecoration(divider)
        }
    }

    fun addSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(SpacingItemDecoration(spacing))
    }

    fun addGridSpacing(recyclerView: RecyclerView, spacing: Int, includeEdge: Boolean = true) {
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacing, includeEdge))
    }

    class SpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position != RecyclerView.NO_POSITION) {
                outRect.left = spacing
                outRect.right = spacing
                outRect.top = spacing
                outRect.bottom = spacing
            }
        }
    }

    class GridSpacingItemDecoration(
        private val spacing: Int,
        private val includeEdge: Boolean = true
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }

    class DividerItemDecorator(
        private val divider: Drawable,
        private val orientation: Int = DividerItemDecoration.VERTICAL
    ) : RecyclerView.ItemDecoration() {

        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            if (orientation == DividerItemDecoration.VERTICAL) {
                drawVertical(canvas, parent)
            } else {
                drawHorizontal(canvas, parent)
            }
        }

        private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            for (i in 0 until parent.childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + divider.intrinsicHeight
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }

        private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
            val top = parent.paddingTop
            val bottom = parent.height - parent.paddingBottom

            for (i in 0 until parent.childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val left = child.right + params.rightMargin
                val right = left + divider.intrinsicWidth
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (orientation == DividerItemDecoration.VERTICAL) {
                outRect.set(0, 0, 0, divider.intrinsicHeight)
            } else {
                outRect.set(0, 0, divider.intrinsicWidth, 0)
            }
        }
    }

    abstract class PaginationScrollListener(
        private val layoutManager: LinearLayoutManager
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (!isLoading() && !isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    loadMoreItems()
                }
            }
        }

        protected abstract fun loadMoreItems()
        abstract fun isLastPage(): Boolean
        abstract fun isLoading(): Boolean
    }

    class RecyclerException(message: String) : Exception(message)
}
