@file:Suppress("UNCHECKED_CAST")

package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.skillbranch.skillarticles.viewmodels.article.ArticleViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle = bundleOf(),
    private val params: Any
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {


    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(
                handle,
                params as String
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}