package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.R

class ChoseCategoryDialog : DialogFragment() {
    companion object {
        const val CHOOSE_CATEGORY_KEY = "CHOOSE_CATEGORY_KEY"
        const val SELECTED_CATEGORIES = "SELECTED_CATEGORIES"
    }


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
            CategoryAdapter() { categoryItem, isChecked ->
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
                setFragmentResult(
                    CHOOSE_CATEGORY_KEY,
                    bundleOf(SELECTED_CATEGORIES to selectedCategories.toList())
                )
            }
            .setNegativeButton("Reset") { _, _ ->
                setFragmentResult(
                    CHOOSE_CATEGORY_KEY,
                    bundleOf(SELECTED_CATEGORIES to emptyList<String>())
                )
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
}