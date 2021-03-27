package com.taetae98.something.utility

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(private val spanCount: Int, private val dp: Int, private val orientation: Int = RecyclerView.VERTICAL, private val includeEdge: Boolean = true) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildLayoutPosition(view)
        val spacing = dp.toDp()
        when(orientation) {
            RecyclerView.VERTICAL -> {
                val column = position % spanCount
                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount
                    outRect.right = (column + 1) * spacing / spanCount
                    outRect.bottom = spacing
                    if (position < spanCount) {
                        outRect.top = spacing
                    }
                } else {
                    outRect.left = column * spacing / spanCount
                    outRect.right = spacing - (column + 1) * spacing / spanCount
                    if (position >= spanCount) {
                        outRect.top = spacing
                    }
                }
            }
            RecyclerView.HORIZONTAL -> {
                val row = position % spanCount
                if (includeEdge) {
                    outRect.top = spacing - row * spacing / spanCount
                    outRect.bottom = (row + 1) * spacing / spanCount
                    outRect.right = spacing
                    if (position < spanCount) {
                        outRect.left = spacing
                    }
                } else {
                    outRect.top = row * spacing / spanCount
                    outRect.bottom = spacing - (row + 1) * spacing / spanCount
                    if (position >= spanCount) {
                        outRect.left = spacing
                    }
                }
            }
        }
    }

    private fun Int.toDp(): Int {
        return (this * Resources.getSystem().displayMetrics.density + 0.5F).toInt()
    }
}