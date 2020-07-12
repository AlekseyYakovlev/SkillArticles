package ru.skillbranch.skillarticles.ui.article

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.ui.custom.CommentItemView

class CommentsAdapter(private val listener: (CommentItemData) -> Unit) :
    PagedListAdapter<CommentItemData, CommentVH>(CommentDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH = CommentVH(CommentItemView(parent.context), listener)

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.bind(getItem(position))
    }
}

class CommentVH(private val containerView: View, val listener: (CommentItemData) -> Unit) :
    RecyclerView.ViewHolder(containerView) {
    fun bind(item: CommentItemData?) {
        (containerView as CommentItemView).bind(item)
        if (item != null) itemView.setOnClickListener { listener(item) }
    }
}

class CommentDiffCallback() : DiffUtil.ItemCallback<CommentItemData>() {
    override fun areItemsTheSame(oldItem: CommentItemData, newItem: CommentItemData): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CommentItemData, newItem: CommentItemData): Boolean =
        oldItem == newItem
}