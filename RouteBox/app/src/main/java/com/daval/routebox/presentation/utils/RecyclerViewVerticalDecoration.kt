package com.daval.routebox.presentation.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class RecyclerViewVerticalDecoration(private val flag: String, private val divHeight: Int) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            if (flag == "top")
                outRect.top = divHeight
            else if (flag == "bottom")
                outRect.bottom = divHeight
        }
    }
}