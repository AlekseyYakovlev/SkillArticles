package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class ViewModelDelegate<T : ViewModel>(
    private val clazz: Class<T>,
    private val arg: Any?
) : ReadOnlyProperty<FragmentActivity, T> {

    private lateinit var viewModel :T

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(thisRef, ViewModelFactory(arg ?: "0")).get(clazz)
        }
        return viewModel
    }
}

//class ViewModelFactory1(private val params: Any) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
//            return ArticleViewModel(params as String) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

//val vmFactory = ViewModelFactory("0")
//ViewModelProvider(this, vmFactory).get(ArticleViewModel::class.java)