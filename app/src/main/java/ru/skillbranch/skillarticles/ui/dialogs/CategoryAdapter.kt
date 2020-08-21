package ru.skillbranch.skillarticles.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_category_dialog_item.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData

class CategoryAdapter(
    private val listener: (CategoryItem, Boolean) -> Unit
) : ListAdapter<CategoryItem, RecyclerView.ViewHolder>(CategoriesDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_category_dialog_item, parent, false)
        return CategoriesVH(view, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as CategoriesVH).bind(item)
    }

}

private class CategoriesVH(
    val containerView: View,
    val listener: (CategoryItem, Boolean) -> Unit
) : RecyclerView.ViewHolder(containerView) {

    fun bind(item: CategoryItem) {
        with(containerView) {
            Glide.with(containerView)
                .load(item.icon)
                .into(iv_icon)

            tv_category.text = item.title
            tv_count.text = item.articlesCount.toString()
            ch_select.isChecked = item.isChecked

            ch_select.setOnCheckedChangeListener { _, isChecked ->
                listener(item, isChecked)
            }
            setOnClickListener {
                ch_select.toggle()
            }
        }
    }
}

private class CategoriesDiffUtilCallback : DiffUtil.ItemCallback<CategoryItem>() {
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
        oldItem == newItem
}

data class CategoryItem(
    val categoryId: String,
    val icon: String,
    val title: String,
    val articlesCount: Int = 0,
    val isChecked: Boolean = false
)

fun CategoryData.toCategoryItem(isChecked: Boolean = false) = CategoryItem(
    categoryId = categoryId,
    icon = icon,
    title = title,
    articlesCount = articlesCount,
    isChecked = isChecked
)