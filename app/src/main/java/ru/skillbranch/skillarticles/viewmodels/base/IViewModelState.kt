package ru.skillbranch.skillarticles.viewmodels.base

import androidx.lifecycle.SavedStateHandle

interface IViewModelState {

    fun save(outState: SavedStateHandle){
        // default empty implementation
    }
    fun restore(savedState: SavedStateHandle) : IViewModelState{
        // default empty implementation
        return this
    }
}