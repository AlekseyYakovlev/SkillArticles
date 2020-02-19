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

    private lateinit var viewModel: T

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (!this::viewModel.isInitialized) {
            viewModel = when (arg) {
                null -> ViewModelProvider(thisRef).get(clazz)
                else -> ViewModelProvider(thisRef, ViewModelFactory(arg)).get(clazz)
            }
        }
        return viewModel
    }
}