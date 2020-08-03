package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_category_dialog_item.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ChoseCategoryDialog : DialogFragment() {
    private val viewModel: ArticlesViewModel by activityViewModels()
    private val selectedCategories = mutableSetOf<String>()
    private val args: ChoseCategoryDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val categories = args.categories.toList()
        selectedCategories.addAll(
            savedInstanceState?.getStringArray(::selectedCategories.name) ?: args.selectedCategories
        )

        val categoryItems = categories.map {
            it.toCategoryItem(isChecked = it.categoryId in selectedCategories)
        }

        val categoriesListAdapter =
            CategoriesListAdapter() { categoryItem, isChecked ->
                toggleCategoryItem(categoryItem, isChecked)
            }.apply {
                submitList(categoryItems)
            }

        val rv =
            (layoutInflater.inflate(R.layout.layout_category_dialog_list, null) as RecyclerView)
                .apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = categoriesListAdapter
                }

        val adb = AlertDialog.Builder(requireContext())
            .setTitle("Choose categories")
            .setView(rv)
            .setPositiveButton("Apply") { _, _ ->
                viewModel.applyCategories(selectedCategories.toList())
            }
            .setNegativeButton("Reset") { _, _ ->
                viewModel.applyCategories(emptyList())
            }
        return adb.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(::selectedCategories.name, selectedCategories.toTypedArray())
    }

    private fun toggleCategoryItem(item: CategoryItem, isChecked: Boolean) {
        if (isChecked) selectedCategories.add(item.categoryId)
        else selectedCategories.remove(item.categoryId)
    }

    private class CategoriesListAdapter(
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

    private data class CategoryItem(
        val categoryId: String,
        val icon: String,
        val title: String,
        val articlesCount: Int = 0,
        val isChecked: Boolean = false
    )

    private fun CategoryData.toCategoryItem(isChecked: Boolean = false) = CategoryItem(
        categoryId = categoryId,
        icon = icon,
        title = title,
        articlesCount = articlesCount,
        isChecked = isChecked
    )
}