package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class ViewModelDelegate<T : ViewModel>(
    private val clazz: Class<T>,
    private val arg: Any?
) : ReadOnlyProperty<FragmentActivity, T> {

    lateinit var vmFactory :ViewModelFactory

    internal inline fun provideViewModel(arg : Any?) : ViewModelDelegate<T>{
        vmFactory = ViewModelFactory(arg?:"0")
        //ViewModelProvider(this, vmFactory).get(clazz)
        return this
    }

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        return ViewModelProvider(thisRef, vmFactory).get(clazz)
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