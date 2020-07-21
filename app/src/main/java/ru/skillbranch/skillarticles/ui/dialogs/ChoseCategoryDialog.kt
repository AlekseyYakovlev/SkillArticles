package ru.skillbranch.skillarticles.ui.dialogs


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
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
    private val articlesViewModel: ArticlesViewModel by activityViewModels()
    private val viewModel: CategoryDialogViewModel by viewModels()
    private val args: ChoseCategoryDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val categories = args.categories.toList()
        val checked = args.selectedCategories.toList()


        val adapter = CategoriesListAdapter()

        if (!viewModel.isInUse) {
            viewModel.isInUse = true
            viewModel.selectedCategories.addAll(checked)
        }

        val categoryItems = categories.map {
            it.toCategoryItem(isChecked = it.categoryId in viewModel.selectedCategories)
        }
        adapter.submitList(categoryItems)


        val rv = layoutInflater.inflate(R.layout.layout_category_dialog_list, null)
        rv as RecyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter


        val adb = AlertDialog.Builder(requireContext())
            .setTitle("Choose categories")
            .setView(rv)
            .setPositiveButton("Apply") { _, _ ->
                articlesViewModel.applyCategories(viewModel.selectedCategories.toList())
                viewModel.selectedCategories.clear()
            }
            .setNegativeButton("Reset") { _, _ ->
                articlesViewModel.applyCategories(emptyList())
                viewModel.selectedCategories.clear()
            }
        return adb.create()
    }


    private fun populate(
        adapter: CategoriesListAdapter,
        categories: List<CategoryData>,
        checked: Set<String>
    ) {
        val categoryItems =
            categories.map { dataItem ->
                dataItem.toCategoryItem(isChecked = dataItem.categoryId in checked)
            }
        adapter.submitList(categoryItems)
    }


//    private fun subscribeUi(adapter: CategoriesListAdapter) {
//        articlesViewModel.observeCategories(this) { categories ->
//            val categoryItems =
//                categories.map { it.toCategoryItem(isChecked = it.categoryId in viewModel.selectedCategories) }
//            adapter.submitList(categoryItems)
//        }
//    }


    inner class CategoriesListAdapter() :
        ListAdapter<CategoryItem, RecyclerView.ViewHolder>(CategoriesDiffUtilCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CategoriesVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_category_dialog_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = getItem(position)
            (holder as CategoriesVH).bind(item)
        }

    }

    inner class CategoriesVH(val containerView: View) : RecyclerView.ViewHolder(containerView) {
        fun bind(
            item: CategoryItem
//            isCheckedListener: (item: CategoryItem, isChecked: Boolean) -> Unit
        ) {
            with(containerView) {
                Glide.with(containerView)
                    .load(item.icon)
                    .into(iv_icon)

                tv_category.text = item.title
                tv_count.text = item.articlesCount.toString()
                ch_select.isChecked = item.isChecked
                ch_select.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) viewModel.selectedCategories.add(item.categoryId)
                    else viewModel.selectedCategories.remove(item.categoryId)
                }
                setOnClickListener {
                    ch_select.isChecked = !ch_select.isChecked
                }
            }
        }
    }

    class CategoriesDiffUtilCallback : DiffUtil.ItemCallback<CategoryItem>() {
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


}

class CategoryDialogViewModel : ViewModel() {
    val selectedCategories = mutableSetOf<String>()
    var isInUse = false
}